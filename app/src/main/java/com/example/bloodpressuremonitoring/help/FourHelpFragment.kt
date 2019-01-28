package com.example.bloodpressuremonitoring.help

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import com.example.bloodpressuremonitoring.R
import com.example.bloodpressuremonitoring.Rss.RssActivity

class FourHelpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_four_help, container, false)
        val help4 = view!!.findViewById<Button>(R.id.help4_btn)
        val help4image = view.findViewById<ImageView>(R.id.help4_imageView)

        val sv = ScrollView(context)
        val ll = LinearLayout(context)
        ll.setOrientation(LinearLayout.VERTICAL)
        sv.addView(ll)

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.getDefaultDisplay()
        val size = Point()
        display.getSize(size)
        val intendedWidth = size.x*80/100

        val bd = this.resources.getDrawable(R.drawable.help4_1) as BitmapDrawable
        val width: Double = bd.bitmap.width.toDouble()
        val height: Double = bd.bitmap.height.toDouble()
        val scale = intendedWidth / width
        val newHeight = Math.round(height * scale).toInt()

        val params = LinearLayout.LayoutParams(intendedWidth, newHeight)
        params.gravity = Gravity.CENTER_HORIZONTAL
        help4image.setLayoutParams(params)

        help4.setOnClickListener {
            activity.finish()
            val intent = Intent(context, RssActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
