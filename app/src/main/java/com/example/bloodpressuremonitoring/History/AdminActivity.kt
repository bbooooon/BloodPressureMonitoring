package com.example.bloodpressuremonitoring.History

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SearchView
import com.example.bloodpressuremonitoring.BPObject
import com.example.bloodpressuremonitoring.R
import com.example.bloodpressuremonitoring.SessionManager
import com.example.bloodpressuremonitoring.classify.UserData
import com.example.bloodpressuremonitoring.user.AddUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_admin.*
import java.text.SimpleDateFormat

class AdminActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    var adapter: ListViewAdapter? = null
    lateinit var session: SessionManager
    lateinit var loadingDialog: ProgressDialog

    override fun onBackPressed() {
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter!!.filter(newText!!)
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.getItemId()) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                finish()
                session.logoutUser()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val toolbar = findViewById<Toolbar>(R.id.admin_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        adapter = ListViewAdapter(this, BPObject.hnlist)

        name_listview.adapter = adapter
        search_name.setOnQueryTextListener(this)

        name_listview.setOnItemClickListener { adapterView, view, position, id ->
            loadingDialog = ProgressDialog.show(this@AdminActivity, "", "loading...", true, false)

            BPObject.bplist.clear()
            BPObject.timelist.clear()
            val dataReference = FirebaseDatabase.getInstance().getReference("UserData").child(BPObject.hnlist[position])
            dataReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }
                override fun onDataChange(p0: DataSnapshot?) {
                    val msgList = arrayListOf<UserData>()
                    if (p0!!.exists()) {
                        msgList.clear()
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

                    val intent = Intent(this@AdminActivity, HistoryActivity::class.java)
                    intent.putExtra("hn", BPObject.hnlist[position])
                    intent.putExtra("email", BPObject.emaillist[position])
                    intent.putExtra("name", BPObject.namelist[position])
                    startActivity(intent)
                }
            })
        }
    }
}
