package com.example.bloodpressuremonitoring.Rss

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.example.bloodpressuremonitoring.R
import com.example.bloodpressuremonitoring.Rss.FeedFragment
import com.example.bloodpressuremonitoring.user.MainActivity

class RssActivity : AppCompatActivity() {

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
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rss)

        val toolbar = findViewById<Toolbar>(R.id.rss_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentrss, FeedFragment.newInstance("http://rssfeeds.sanook.com/rss/feeds/sanook/health.medicine.xml/"))
                .commit()

//        val fragment = FeedFragment()
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.fragmentrss, fragment)
//        fragmentTransaction.commit()
    }
}
