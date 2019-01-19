package com.example.bloodpressuremonitoring

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.bloodpressuremonitoring.History.AccessActivity
import com.example.bloodpressuremonitoring.History.HistoryActivity
import com.example.bloodpressuremonitoring.Rss.RssActivity
import com.example.bloodpressuremonitoring.classify.CameraActivity
import com.example.bloodpressuremonitoring.classify.UserData
import com.example.bloodpressuremonitoring.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_home.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat

class HomeActivity : AppCompatActivity() {
    lateinit var session: SessionManager
    lateinit var loadingDialog: ProgressDialog
    private val PICK_IMAGE_REQUEST = 5678
    lateinit var hn: String
    private var filePath: Uri? = null
    internal var path = ""

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                finish()
                session.logoutUser()
            }
        }

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val camera_toolbar = findViewById<Toolbar>(R.id.home_toolbar)
        setSupportActionBar(camera_toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        session = SessionManager(applicationContext)
        session.checkLogin()

        val user = session.getUserDetails()
        val email = user.get(SessionManager.KEY_EMAIL)
        val aname = user.get(SessionManager.KEY_NAME)
        hn = user.get(SessionManager.KEY_HN)!!

        val p_email = email!!.split("@")

        val head = p_email[0].substring(0, 3)
        val body = p_email[0].substring(3, p_email[0].length)
        val rebody = body.replace("[a-z|A-Z|0-9]".toRegex(), "x")

        val re_email = head+rebody+"@"+p_email[1]

        if(email != null) {
            user_infotxt.text = "รหัสคนไข้ : $hn"
            user_infotxt2.text = re_email

            loadingDialog = ProgressDialog.show(this, "", "loading...", true, false)

            setImageViewFromFirebase()
            val dataReference = FirebaseDatabase.getInstance().getReference("UserData").child(hn)
            dataReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }
                override fun onDataChange(p0: DataSnapshot?) {
                    val msgList = arrayListOf<UserData>()
                    if (p0!!.exists()) {
                        msgList.clear()
                        BPObject.bplist.clear()
                        BPObject.timelist.clear()
                        for (i in p0.children) {
                            val message = i.getValue(UserData::class.java)
                            msgList.add(message!!)
                            BPObject.bplist = msgList

                            val date = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").parse(message!!.runtimestamp)
                            val dateformat = SimpleDateFormat("dd/MM/yyyy HH:mm").format(date)
                            Log.e("date format", dateformat)
                            BPObject.timelist.add(dateformat)
                        }
                    }
                    loadingDialog.dismiss()
                }
            })
        }

        profile_image.setOnClickListener{
            val intent1 = Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent1, PICK_IMAGE_REQUEST)
        }

        camera_btn.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        history_btn.setOnClickListener {
            if (User.access == false) {
                val intent = Intent(this, AccessActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
        }

        rss_btn.setOnClickListener {
            val intent = Intent(this, RssActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setImageViewFromFirebase() {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference.child("user/$hn.jpg")
        storageReference.getBytes(java.lang.Long.MAX_VALUE).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            profile_image!!.setImageBitmap(bitmap)
        }.addOnFailureListener { }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data!!.getData() != null) {
            try {
                filePath = data!!.getData()
                val FILE = arrayOf(MediaStore.Images.Media.DATA)

                val cursor = contentResolver.query(filePath,
                        FILE, null, null, null)
                cursor!!.moveToFirst()

                val columnIndex = cursor.getColumnIndex(FILE[0])
                path = cursor.getString(columnIndex)

                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                bitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.width*0.5).toInt(), (bitmap.height*0.5).toInt(), false)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val bt = baos.toByteArray()

                val storage = FirebaseStorage.getInstance()
                val storageReference = storage.reference
                val imageRef = storageReference.child("user/$hn.jpg")
                val uploadTask = imageRef.putBytes(bt)

                profile_image!!.setImageBitmap(MediaStore.Images.Media.getBitmap(contentResolver, filePath))

            } catch (e: Exception) {
                Toast.makeText(this, "Please try again", Toast.LENGTH_LONG)
                        .show()
            }

        }
    }
}
