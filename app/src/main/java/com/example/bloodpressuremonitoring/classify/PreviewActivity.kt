package com.example.bloodpressuremonitoring.classify

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.hardware.Camera
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.bloodpressuremonitoring.R
import com.example.bloodpressuremonitoring.ResultActivity
import com.example.bloodpressuremonitoring.SessionManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_preview.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PreviewActivity : AppCompatActivity() {
    private var result:Int? = null
    private var prediction:PredictionResult? = null
    private var fpath:String? = null
    lateinit var loadingDialog: ProgressDialog
    private val cameraId = Camera.CameraInfo.CAMERA_FACING_BACK
    private val location = listOf<String>("--- เลือกสถานที่วัด ---", "บ้าน", "โรงพยาบาล")
    private val posture = listOf<String>("--- เลือกท่าการวัด ---", "ท่านั่ง", "ท่านอน")
    lateinit var dataReference: DatabaseReference
    private var loc_position: Int? = null
    private var pos_position: Int? = null
    lateinit var session: SessionManager

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.getItemId()) {
            R.id.action_help -> {
                return true
            }
            R.id.action_logout -> {
                finish()
                session!!.logoutUser()
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        val toolbar = findViewById<Toolbar>(R.id.camera_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        session = SessionManager(applicationContext)

        var bitmap = BitmapFactory.decodeByteArray(CameraUtil.bytedata, 0, CameraUtil.bytedata.size)
        val angle: Int = 90
        bitmap = RotateBitmap(bitmap,angle.toFloat())

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x*2/3
        val height = width / 3 * 4
        Log.e("display size", size.x.toString() + " x " + size.y)
        Log.e("display size", (bitmap.width).toString() + " x " + (bitmap.height))
        val x:Int = bitmap.width*58/100
        val y:Int = bitmap.height*18/100
//        var resize = Bitmap.createBitmap(bitmap, x, y,(bitmap.width/4).toInt(), (bitmap.height/4).toInt())
        var resize = Bitmap.createBitmap(bitmap, x, y,(width).toInt(), (height).toInt())
        resize = Bitmap.createScaledBitmap(resize, (resize.width*0.5).toInt(), (resize.height*0.5).toInt(), false)
        resize = lightenBitMap(resize)
        preview_imageView.setImageBitmap(resize)

        Log.e("resize resolution", resize.width.toString()+"x"+resize.height)
        val stream = ByteArrayOutputStream()
        resize.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        CameraUtil.bytedata = byteArray

        val adapterLocation = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, location);
        location_spinner.setAdapter(adapterLocation)
        val adapterPosture = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, posture);
        posture_spinner.setAdapter(adapterPosture)

        location_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, p: Int, id: Long) {
                loc_position = p
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        posture_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, p: Int, id: Long) {
                pos_position = p
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        preview_backbtn.setOnClickListener {
            finish()
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        preview_submitbtn.setOnClickListener {
            if(loc_position == 0){
                Toast.makeText(applicationContext, "กรุณาเลือกสถานที่การวัด", Toast.LENGTH_LONG).show()
            }
            else if(pos_position == 0){
                Toast.makeText(applicationContext, "กรุณาเลือกท่าในการวัด", Toast.LENGTH_LONG).show()
            }
            else if(loc_position != 0 && pos_position != 0){
                val file = CameraUtil.savePicture();
                val orientation = CameraUtil.getCameraDisplayOrientation(this, cameraId)
                CameraUtil.setImageOrientation(file, orientation)
                CameraUtil.updateMediaScanner(this, file)
                fpath = file.absolutePath

//                val date = Date()
//                val dateformat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
//                dataReference = FirebaseDatabase.getInstance().getReference("UserData")
//                val user = session.userDetails
//                val name = user[SessionManager.KEY_NAME]
//                val userData = UserData("temp","temp","temp", loc_position.toString(), pos_position.toString())
//                dataReference.child(name).child(dateformat.format(date).toString()).setValue(userData).addOnCompleteListener {
////                    Toast.makeText(this@PreviewActivity, "Data Added", Toast.LENGTH_SHORT).show()
//                }

                callRetrofit()
            }
//            val intent = Intent(this@PreviewActivity, ResultActivity::class.java)
//            startActivity(intent)
        }
    }

    fun RotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun callRetrofit(){
        loadingDialog = ProgressDialog.show(this, "Identifying", "Please wait...", true, false)
        val gson = GsonBuilder()
                .setLenient()
                .create()
        val client = OkHttpClient.Builder()
        client.addInterceptor{ chain ->
            val req = chain.request()
            val requestBuilder = req.newBuilder().method(req.method(),req.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        val retrofit = Retrofit.Builder()
//                .baseUrl("http://35.198.247.234/")
                .baseUrl("http://103.76.181.221:3000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

        val file = File(fpath)

        val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("image", file.getName(), reqFile)

        val service = retrofit.create(Service::class.java)
        val callservice: Call<Prediction> = service.postImage(body)
        callservice.enqueue(object : Callback<Prediction> {
            override fun onResponse(call: Call<Prediction>, response: Response<Prediction>) {
                if(response.isSuccessful) {
                    loadingDialog.dismiss()
                    val pre = response.body()!!
                    result = pre.result
                    prediction = pre.msg
                    Log.e(" server result", result.toString())
//                    Toast.makeText(applicationContext, result, Toast.LENGTH_LONG).show()
                    if (prediction != null && result==1) {
                        val date = Date()
                        val dateformat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
                        dataReference = FirebaseDatabase.getInstance().getReference("UserData")
                        val user = session.userDetails
                        val name = user[SessionManager.KEY_NAME]
                        val userData = UserData(prediction!!.dia.toString(), prediction!!.sys.toString(),
                                prediction!!.pulse.toString(), loc_position.toString(), pos_position.toString())
                        dataReference.child(name).child(dateformat.format(date).toString()).setValue(userData).addOnCompleteListener {
                            Toast.makeText(this@PreviewActivity, "Data Added", Toast.LENGTH_SHORT).show()
                        }

                        val intent = Intent(this@PreviewActivity, ResultActivity::class.java)
                        intent.putExtra("dia", prediction!!.dia.toString())
                        intent.putExtra("sys", prediction!!.sys.toString())
                        intent.putExtra("pulse", prediction!!.pulse.toString())
                        startActivity(intent)
                    }
                    else if (result == 0) {
                        Toast.makeText(applicationContext, "server error", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Log.e("error", response.errorBody().string())
                    Log.e("message", response.message().toString())
                    Log.e("response code", response.code().toString())
                    Log.e("header", response.headers().toString())
                    val intent = Intent(this@PreviewActivity, CameraActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<Prediction>, t: Throwable) {
                t.printStackTrace();
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun lightenBitMap(bm:Bitmap): Bitmap {
        val canvas = Canvas(bm)
        val p: Paint= Paint(Color.RED)
        val filter: ColorFilter = LightingColorFilter(0xFFFFFFFF.toInt() , 0x00222222)
        p.setColorFilter(filter)
        canvas.drawBitmap(bm, Matrix(), p)
        return bm
    }

    private fun uploadToStorage(){
        if (fpath!=null){
            val storage = FirebaseStorage.getInstance()
            val storageReference = storage!!.reference
            val imageRef = storageReference!!.child("pills/"+CameraUtil.filename)//send to firebase folder

            imageRef.putBytes(CameraUtil.bytedata)
                    .addOnCompleteListener{
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Storage Upload Failed",Toast.LENGTH_SHORT).show()
                    }
        }
    }
}