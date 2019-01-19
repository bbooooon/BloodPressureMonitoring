package com.example.bloodpressuremonitoring.help

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.Toolbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.support.design.widget.TabLayout
import com.example.bloodpressuremonitoring.R


class HelpActivity: AppCompatActivity() {
    private var mPager: ViewPager? = null
    private var mPagerAdapter: PagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val toolbar = findViewById<Toolbar>(R.id.addmed_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mPager = findViewById<ViewPager>(R.id.pager);
        mPagerAdapter = ScreenSlidePagerAdapter(getSupportFragmentManager())
        mPager!!.setAdapter(mPagerAdapter)

        val tabLayout = findViewById<View>(R.id.tabDots) as TabLayout
        tabLayout.setupWithViewPager(mPager, true)
    }

    override fun onBackPressed() {
        if (mPager!!.getCurrentItem() === 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            mPager!!.setCurrentItem(mPager!!.getCurrentItem() - 1)
        }
    }

    private class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private val NUM_PAGES = 6

        override fun getItem(position: Int): Fragment? {
            if (position == 0)
                return OneHelpFragment()
            else if (position == 1)
                return TwoHelpFragment()
            else if (position == 2)
                return ThreeHelpFragment()
            else if (position == 3)
                return FourHelpFragment()
            return null
        }

        override fun getCount(): Int {
            return NUM_PAGES
        }
    }
}
