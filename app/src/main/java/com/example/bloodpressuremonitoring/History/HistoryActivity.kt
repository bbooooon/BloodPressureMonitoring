package com.example.bloodpressuremonitoring.History

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SearchView
import com.example.bloodpressuremonitoring.BPObject
import com.example.bloodpressuremonitoring.R
import com.example.bloodpressuremonitoring.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    var adapter: ListViewAdapter? = null

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
                FirebaseAuth.getInstance().signOut()
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
        setContentView(R.layout.activity_history)

        val toolbar = findViewById<Toolbar>(R.id.home_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val intent1 = intent.extras
        val email = intent1.getString("email")
        val aname = intent1.getString("name")
        val hn = intent1.getString("hn")

        val parts = aname!!.split(" ")
        val name = parts[0]

        user_infotxt.text = "รหัสคนไข้ : $hn"
        user_infotxt2.text = "$aname\n$email"

        adapter = ListViewAdapter(this, BPObject.timelist)

        listview.setAdapter(adapter)
        search.setOnQueryTextListener(this)

        listview.setOnItemClickListener { adapterView, view, position, id ->
//            search.setQuery(med_name, false);
//            finish()

            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("position", position)
            startActivity(intent)
        }
    }

}
