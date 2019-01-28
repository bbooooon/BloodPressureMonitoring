package com.example.bloodpressuremonitoring.user

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import com.example.bloodpressuremonitoring.R
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_signup.*


class SignupActivity : AppCompatActivity() {
    private var mPager: ViewPager? = null
    private var mPagerAdapter: PagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        User.userinfo = AddUser()

        val fragment = SignupFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment, fragment)
        fragmentTransaction.commit()

        back_text.setOnClickListener {
            finish()
        }
    }
}
//        mPager = findViewById<ViewPager>(R.id.pager);
//        mPagerAdapter = SignupActivity.ScreenSlidePagerAdapter(getSupportFragmentManager())
//        mPager!!.setAdapter(mPagerAdapter)
//
//        val tabLayout = findViewById<View>(R.id.tabDots) as TabLayout
//        tabLayout.setupWithViewPager(mPager, true)
//    }
//
//    override fun onBackPressed() {
//        if (mPager!!.currentItem === 0) {
//            super.onBackPressed()
//        } else {
//            mPager!!.setCurrentItem(mPager!!.getCurrentItem() - 1)
//        }
//    }
//
//    private class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
//        private val NUM_PAGES = 2
//
//        override fun getItem(position: Int): Fragment? {
//            if (position == 0)
//                return SignupFragment()
//            else if (position == 1)
//                return SignupAddressFragment()
//            return null
//        }
//
//        override fun getCount(): Int {
//            return NUM_PAGES
//        }
//    }
//}
