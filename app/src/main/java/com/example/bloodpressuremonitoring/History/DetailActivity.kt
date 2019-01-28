package com.example.bloodpressuremonitoring.History

import android.app.ProgressDialog
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.example.bloodpressuremonitoring.BPObject
import com.example.bloodpressuremonitoring.R
import com.example.bloodpressuremonitoring.SessionManager
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_detail.*
import java.text.SimpleDateFormat

class DetailActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_detail)

        val toolbar = findViewById<Toolbar>(R.id.detail_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        session = SessionManager(applicationContext)

        val intent1 = intent.extras
        val position = intent1.getInt("position")

        val time = BPObject.bplist[position].phototimestamp
        setImageViewFromFirebase(time + ".jpg")

        val date = SimpleDateFormat("yyyyMMdd_HHmmss").parse(time)
        val dateformat = SimpleDateFormat("dd/MM/yyyy").format(date)
        val timeformat = SimpleDateFormat("HH:mm").format(date)

        val sys = BPObject.bplist[position].sys.toDouble().toInt()
        val dia = BPObject.bplist[position].dia.toDouble().toInt()
        val pulse = BPObject.bplist[position].pulse.toDouble().toInt()
//        วันที่ xx/xx/xxxx เวลา: xx:xx
//        ค่าความดัน: xxx/xxx
//        อัตราการเต้นของหัวใจ: xxx

        timetxt.text = "วันที่ $dateformat | เวลา: $timeformat"
        bptxt.text = "ค่าความดัน: $sys/$dia"
        pulse_text.text = "อัตราการเต้นของหัวใจ: $pulse"

        home_btn.setOnClickListener {
            finish()
        }
    }

    private fun setImageViewFromFirebase(filename : String) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child("pic/$filename")
        storageReference.getBytes(java.lang.Long.MAX_VALUE).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imageView!!.setImageBitmap(bitmap)
        }.addOnFailureListener { }
    }
}
