package com.example.bloodpressuremonitoring.classify

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.*
import android.hardware.Camera
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.bloodpressuremonitoring.R
import com.example.bloodpressuremonitoring.ResultActivity
import com.example.bloodpressuremonitoring.SessionManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_submit.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SubmitActivity : AppCompatActivity() {
    private var result:Int? = null
    private var prediction:PredictionResult? = null
    private var loc_position: Int? = null
    private var pos_position: Int? = null
    private var arm_position:Int? = null
    lateinit var session: SessionManager
    private var fpath:String? = null
    private val cameraId = Camera.CameraInfo.CAMERA_FACING_BACK
    lateinit var loadingDialog: ProgressDialog
    lateinit var dataReference: DatabaseReference
    private val location = listOf<String>("--- เลือกสถานที่วัด ---", "บ้าน", "โรงพยาบาล")
    private val posture = listOf<String>("--- เลือกท่าการวัด ---", "ท่านั่ง", "ท่านอน")
    private val arm = listOf<String>("--- เลือกแขนข้างที่วัด ---", "แขนซ้าย", "แขนขวา")

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
                session.logoutUser()
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit)

        val toolbar = findViewById<Toolbar>(R.id.camera_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        session = SessionManager(applicationContext)

        val intent1 = intent.extras
//        val result = "123"
        loc_position = intent1.getInt("location")
        pos_position = intent1.getInt("posture")
        arm_position = intent1.getInt("arm")
        posture_text.text = "ท่าการวัด : " + posture[pos_position!!]
        location_text.text = "สถานที่วัด : " + location[loc_position!!]
        arm_text.text = "แขนข้างที่วัด : " + arm[arm_position!!]

        var bitmap = BitmapFactory.decodeByteArray(CameraUtil.bytedata, 0, CameraUtil.bytedata.size)
        sm_preview_imageView.setImageBitmap(bitmap)

        sm_backbtn.setOnClickListener {
            finish()
        }

        sm_submitbtn.setOnClickListener {
            val file = CameraUtil.savePicture("img_");
            val orientation = CameraUtil.getCameraDisplayOrientation(this, cameraId)
            CameraUtil.setImageOrientation(file, orientation)
            CameraUtil.updateMediaScanner(this, file)
            fpath = file.absolutePath

            callRetrofit()
        }
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

        val usersess = session.userDetails
        val name = usersess[SessionManager.KEY_NAME]
        val hn = usersess[SessionManager.KEY_HN]

        val date = Date()
        val dateformat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")

        Log.e("hn :: ",hn)
        val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body1 = MultipartBody.Part.createFormData("image", file.getName(), reqFile)
        val body2 = MultipartBody.Part.createFormData("hn", hn.toString())
        val body3 = MultipartBody.Part.createFormData("username", name)
        val body4 = MultipartBody.Part.createFormData("timestamp", dateformat.format(date))
        val service = retrofit.create(Service::class.java)
        val callservice: Call<Prediction> = service.postImage(body1,body2,body3,body4)
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
                                prediction!!.pulse.toString(), loc_position.toString(), pos_position.toString(), arm_position.toString(),
                                CameraUtil.takentime,dateformat.format(date).toString())
                        dataReference.child(name).child(dateformat.format(date).toString()).setValue(userData).addOnCompleteListener {
                            Toast.makeText(this@SubmitActivity, "Data Added", Toast.LENGTH_SHORT).show()
                        }

                        val intent = Intent(this@SubmitActivity, ResultActivity::class.java)
                        intent.putExtra("dia", prediction!!.dia.toString())
                        intent.putExtra("sys", prediction!!.sys.toString())
                        intent.putExtra("pulse", prediction!!.pulse.toString())
                        startActivity(intent)
                    }
                    else if (result == 0) {
                        Toast.makeText(applicationContext, "กรุณาถ่ายใหม่", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Log.e("error", response.errorBody().string())
                    Log.e("message", response.message().toString())
                    Log.e("response code", response.code().toString())
                    Log.e("header", response.headers().toString())
                    val intent = Intent(this@SubmitActivity, CameraActivity::class.java)
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
}
