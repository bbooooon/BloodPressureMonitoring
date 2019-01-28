package com.example.bloodpressuremonitoring.user

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.bloodpressuremonitoring.R
import kotlinx.android.synthetic.main.activity_signin.*
import android.support.v4.app.ActivityCompat
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.bloodpressuremonitoring.BPObject
import com.example.bloodpressuremonitoring.HomeActivity
import com.example.bloodpressuremonitoring.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SigninActivity : AppCompatActivity() {
    private val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123
    private var email_list: MutableList<String> = mutableListOf()
    private var name_list: MutableList<String> = mutableListOf()
    private var admin_list: MutableList<Int> = mutableListOf()
    private var hn_list: MutableList<String> = mutableListOf()
    lateinit var mAuth: FirebaseAuth
    lateinit var dataReference: DatabaseReference
    lateinit var msgList: MutableList<AddUser>
    lateinit var session: SessionManager

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val v = currentFocus

        if (v != null &&
                (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
                v is EditText &&
                !v.javaClass.name.startsWith("android.webkit.")) {
            val scrcoords = IntArray(2)
            v.getLocationOnScreen(scrcoords)
            val x = ev.rawX + v.left - scrcoords[0]
            val y = ev.rawY + v.top - scrcoords[1]

            if (x < v.left || x > v.right || y < v.top || y > v.bottom)
                hideKeyboard(this)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null && activity.window.decorView != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }

    private fun requestPermission() {
        val permissionsNeeded = ArrayList<String>()

        val permissionsList = ArrayList<String>()
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera")
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write External Storage")

        if (permissionsList.size > 0) {
            if (permissionsNeeded.size > 0) {
                // Need Rationale
                var message = "You need to grant access to " + permissionsNeeded[0]
                for (i in 1 until permissionsNeeded.size)
                    message = message + ", " + permissionsNeeded[i]
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissionsList.toTypedArray(),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
                }
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionsList.toTypedArray(),

         REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
            }
            return
        }
    }
    private fun addPermission(permissionsList: MutableList<String>, permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission)
                if (!shouldShowRequestPermissionRationale(permission))
                    return false
            }
        } else {
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.CAMERA)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.CAMERA),
                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
                }
            }
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
                }
            }
        }

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val signin_submit = findViewById<Button>(R.id.signin_submit_btn)
        requestPermission()

        dataReference = FirebaseDatabase.getInstance().getReference("User")
        msgList = mutableListOf()
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    msgList.clear()
                    email_list.clear()
                    for (i in p0.children) {
                        val message = i.getValue(AddUser::class.java)
                        msgList.add(message!!)
                        email_list.add(message.email)
                        name_list.add(message.name)
                        hn_list.add(message.hn)
                        admin_list.add(message.admin)
                    }
                }
            }
        })

        signin_submit.setOnClickListener {
            val email: String = signin_email_input.text.toString()
            val password: String = signin_pass_input.text.toString()

            val encrypted = AESCrypt.encrypt(password)

            val userindex = email_list.indexOf(email)
//            for ((index, value) in email_list.withIndex()) {
//                if (email in email_list[index]){ //&& encrypted.equals(password_list[index])) {
                    val em: String = email_list[userindex]
                    val n: String = name_list[userindex]
                    val hn = hn_list[userindex]
                    val admin = admin_list[userindex]

                    if (admin == 1){
                        BPObject.hnlist = hn_list as ArrayList<String>
                    }
                    mAuth = FirebaseAuth.getInstance()
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                        if (!task.isSuccessful) {
                            Log.w(ContentValues.TAG, "signInWithEmail", task.getException());
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            User.getUser().email = email

                            session = SessionManager(this)
                            session.createLoginSession(n, em, hn, admin.toString())

                            finish()
                            val openActivity = Intent(this, HomeActivity::class.java)
                            openActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            startActivityIfNeeded(openActivity, 0)
                        }
                    }

//                    val intent = Intent(this, HomeActivity::class.java)
//                    startActivity(intent)
//                }
//                if (email in email_list[index] && encrypted != password_list[index]) {
//                    Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//            if (email !in email_list || encrypted !in password_list) {
//                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
//            }
//            if (email in email_list && encrypted !in password_list) {
//                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
//            }
        }

        forgot_password_btn.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        signup_btn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

//        val fragment = SignupAddressFragment()
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.fragment, fragment)
//        fragmentTransaction.commit()
//
//        requestPermission()
//
//        signup_btn.setOnClickListener{
//            signup_btn.setBackgroundColor(Color.parseColor("#c5e0de"))
//            signin_btn.setBackgroundColor(Color.parseColor("#daebea"))
//            val fragment1 = SignupFragment()
//            val fragmentTransaction1 = supportFragmentManager.beginTransaction()
//            fragmentTransaction1.replace(R.id.fragment, fragment1)
//            fragmentTransaction1.commit()
//        }
//
//        signin_btn.setOnClickListener{
//            signin_btn.setBackgroundColor(Color.parseColor("#c5e0de"))
//            signup_btn.setBackgroundColor(Color.parseColor("#daebea"))
//            val fragment1 = SignupAddressFragment()
//            val fragmentTransaction1 = supportFragmentManager.beginTransaction()
//            fragmentTransaction1.replace(R.id.fragment, fragment1)
//            fragmentTransaction1.commit()
//        }
    }
}
