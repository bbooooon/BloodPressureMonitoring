package com.example.bloodpressuremonitoring.classify

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
import com.example.bloodpressuremonitoring.SessionManager
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_preview.*
import java.io.ByteArrayOutputStream
import android.graphics.Bitmap

class PreviewActivity : AppCompatActivity() {
    private var fpath:String? = null
    private val cameraId = Camera.CameraInfo.CAMERA_FACING_BACK
    private val location = listOf<String>("--- เลือกสถานที่วัด ---", "บ้าน", "โรงพยาบาล")
    private val posture = listOf<String>("--- เลือกท่าการวัด ---", "ท่านั่ง", "ท่านอน")
    private val arm = listOf<String>("--- เลือกแขนข้างที่วัด ---", "แขนซ้าย", "แขนขวา")
    private var loc_position: Int? = null
    private var pos_position: Int? = null
    private var arm_position: Int? = null
    lateinit var session: SessionManager

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.getItemId()) {
//            R.id.action_help -> {
//                return true
//            }
            R.id.action_logout -> {
                finish()
                session.logoutUser()
//                val intent = Intent(this, SigninActivity::class.java)
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

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val angle: Int = 90
        bitmap = RotateBitmap(bitmap,angle.toFloat())

        Log.e("original bitmap size", (bitmap.width).toString() + " x " + (bitmap.height))

        bitmap = Bitmap.createBitmap(bitmap, (bitmap.width/3-((size.y-bitmap.width)/6)).toInt(), 0,
                (bitmap.width/3+(size.y-bitmap.width)/3).toInt(), bitmap.height.toInt())
        Log.e("bitmap size", (bitmap.width).toString() + " x " + (bitmap.height))

        Log.e("ratio", (size.x/bitmap.width).toString() + " x " + size.y/bitmap.height)

        bitmap = scaleBitmap(bitmap, size.x.toFloat(),size.y.toFloat())
        Log.e("scale bitmap size", (bitmap.width).toString() + " x " + (bitmap.height))
        var width = (bitmap.width / 3).toInt()
        var height = width / 3 * 4

//        val displayratio :Double = ((size.y.toDouble()/size.x.toDouble())*10)
//        Log.e("display ratio ::: ", displayratio.toString())
//
        val left = (size.x / 2) - width / 2
        val top = (size.y / 4) - height/2
//        val right = size.x / 2 - width / 2
//        val bottom = size.y - height - top

        Log.e("left top : ", left.toString()+" , "+top.toString())

//        var bitmapwidth :Double? = bitmap.width.toDouble()
//        var bitmapheight :Double? = bitmap.height.toDouble()
//        if (displayratio.toInt() in 12..14){
//            Log.e("decision ", "enter 1")
//            bitmapwidth = size.x*ratiow
//            bitmapheight = size.y*ratioh
//        }
//        else if (displayratio.toInt() in 16..18){
//            Log.e("decision ", "enter 2")
//            bitmapwidth = size.x*ratiow*0.85
//            bitmapheight = size.y*ratioh*0.85
//        }
//        else if (displayratio.toInt() in 19..21){
//            Log.e("decision ", "enter 3")
//            bitmapwidth = size.x*ratiow*0.67
//            bitmapheight = size.y*ratioh*0.67
//        }
//        else{
//            Log.e("decision ", "enter 4")
//            bitmapwidth = size.x*ratiow
//            bitmapheight = size.y*ratioh
//        }
//
//        val sidew = (bitmap.width - bitmapwidth!!)/2
//        val sideh = (bitmap.height - bitmapheight!!)/2
//
//        val ratio:Double = bitmapwidth!!.toDouble()/size.x.toDouble()
//        val width = size.x/3 * ratiow
//        var height = size.y/3 * ratioh
////        val height = (width * 3 / 4) * ratioh
//
//        var cropside = Bitmap.createBitmap(bitmap, sidew.toInt(),sideh.toInt(),(bitmapwidth!!).toInt(), (bitmapheight!!).toInt())

//@todo old one down
//        var bitmapwidth :Double? = bitmap.width.toDouble()
//        var bitmapheight :Double? = bitmap.height.toDouble()
//        if (displayratio.toInt() in 12..14){
//            Log.e("decision ", "enter 1")
//            bitmapwidth = size.x*ratiow
//            bitmapheight = size.y*ratioh
//        }
//        else if (displayratio.toInt() in 16..18){
//            Log.e("decision ", "enter 2")
//            bitmapwidth = size.x*ratiow//*0.85//*0.90
//            bitmapheight = size.y*ratioh
//        }
//        else if (displayratio.toInt() in 19..21){
//            Log.e("decision ", "enter 3")
//            bitmapwidth = size.x*ratiow*0.67
//            bitmapheight = size.y*ratioh
//        }
//        else{
//            Log.e("decision ", "enter 4")
//            bitmapwidth = size.x*ratiow
//            bitmapheight = size.y*ratioh
//        }
//
//        val sidew = (bitmap.width - bitmapwidth!!)/2
//        val sideh = (bitmap.height - bitmapheight!!)/2
//
//        val ratio:Double = bitmapwidth!!.toDouble()/size.x.toDouble()
//        val width = size.x/3 * ratio
//        val height = width / 3 * 4
//
////        Log.e("display side origin", sideh.toString() + " x " + sidew.toString())
//
//        var cropside = Bitmap.createBitmap(bitmap, sidew.toInt(),sideh.toInt(),(bitmapwidth!!).toInt(), (bitmapheight!!-sideh).toInt())

        //@todo remove when test done
        val stream1 = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream1)
        val bytearr = stream1.toByteArray()
        CameraUtil.bytedata = bytearr
        val file2 = CameraUtil.savePicture("test2_");
        val orientation2 = CameraUtil.getCameraDisplayOrientation(this, cameraId)
        CameraUtil.setImageOrientation(file2, orientation2)
        CameraUtil.updateMediaScanner(this, file2)
        fpath = file2.absolutePath
//
//        val x:Int = ((cropside.width!! / 2) - (width / 2)).toInt()
//        val y:Int = ((cropside.height!! / 4) - (height / 2)).toInt()
//        height = (width * 3 / 4) * ratioh

        var resize = Bitmap.createBitmap(bitmap, left.toInt(), top.toInt(), width.toInt(), height.toInt())
        resize = Bitmap.createScaledBitmap(resize, (resize.width).toInt(), (resize.height).toInt(), false)
//        resize = lightenBitMap(resize)

        //@todo remove when test done
        val stream2 = ByteArrayOutputStream()
        resize.compress(Bitmap.CompressFormat.PNG, 100, stream2)
        val bytearr1 = stream2.toByteArray()
        CameraUtil.bytedata = bytearr1
        val file3 = CameraUtil.savePicture("test3_");
        val orientation3 = CameraUtil.getCameraDisplayOrientation(this, cameraId)
        CameraUtil.setImageOrientation(file3, orientation3)
        CameraUtil.updateMediaScanner(this, file3)
        fpath = file3.absolutePath

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
        val adapterArm = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arm);
        arm_spinner.setAdapter(adapterArm)

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
        arm_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, p: Int, id: Long) {
                arm_position = p
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        preview_backbtn.setOnClickListener {
            finish()
        }

        preview_submitbtn.setOnClickListener {
            if(loc_position == 0){
                Toast.makeText(applicationContext, "กรุณาเลือกสถานที่การวัด", Toast.LENGTH_LONG).show()
            }
            else if(pos_position == 0){
                Toast.makeText(applicationContext, "กรุณาเลือกท่าในการวัด", Toast.LENGTH_LONG).show()
            }
            else if(arm_position == 0){
                Toast.makeText(applicationContext, "กรุณาเลือกแขนข้างที่วัด", Toast.LENGTH_LONG).show()
            }
            else if(loc_position != 0 && pos_position != 0 && arm_position != 0){
                finish()
                val intent = Intent(this@PreviewActivity, SubmitActivity::class.java)
                intent.putExtra("location",loc_position)
                intent.putExtra("posture",pos_position)
                intent.putExtra("arm",arm_position)
                startActivity(intent)
            }
        }
    }

    fun RotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun scaleBitmap(bitmapToScale: Bitmap?, newWidth: Float, newHeight: Float): Bitmap? {
        if (bitmapToScale == null)
            return null
        //get the original width and height
        val width = bitmapToScale.width
        val height = bitmapToScale.height
        // create a matrix for the manipulation
        val matrix = Matrix()

        // resize the bitmap
        matrix.postScale(newWidth / width, newHeight / height)

        // recreate the new Bitmap and set it back
        return Bitmap.createBitmap(bitmapToScale, 0, 0, bitmapToScale.width, bitmapToScale.height, matrix, true)
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
            val imageRef = storageReference!!.child("file/"+CameraUtil.filename)//send to firebase folder

            imageRef.putBytes(CameraUtil.bytedata)
                    .addOnCompleteListener{
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Storage Upload Failed",Toast.LENGTH_SHORT).show()
                    }
        }
    }
}