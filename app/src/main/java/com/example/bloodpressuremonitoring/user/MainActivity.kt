package com.example.bloodpressuremonitoring.user

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.bloodpressuremonitoring.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123
    private val PERMISSION_CAMERA = 111
    private val PERMISSION_STORAGE = 222
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
                requestPermissions(permissionsList.toTypedArray(),
                                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
                return
            }
            requestPermissions(permissionsList.toTypedArray(),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
            return
        }
    }

    private fun addPermission(permissionsList: MutableList<String>, permission: String): Boolean {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission)
            if (!shouldShowRequestPermissionRationale(permission))
                return false
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = SigninFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment, fragment)
        fragmentTransaction.commit()

        requestPermission()

        signup_btn.setOnClickListener{
            signup_btn.setBackgroundColor(Color.parseColor("#c5e0de"))
            signin_btn.setBackgroundColor(Color.parseColor("#daebea"))
            val fragment1 = SignupFragment()
            val fragmentTransaction1 = supportFragmentManager.beginTransaction()
            fragmentTransaction1.replace(R.id.fragment, fragment1)
            fragmentTransaction1.commit()
        }

        signin_btn.setOnClickListener{
            signin_btn.setBackgroundColor(Color.parseColor("#c5e0de"))
            signup_btn.setBackgroundColor(Color.parseColor("#daebea"))
            val fragment1 = SigninFragment()
            val fragmentTransaction1 = supportFragmentManager.beginTransaction()
            fragmentTransaction1.replace(R.id.fragment, fragment1)
            fragmentTransaction1.commit()
        }
    }
}
