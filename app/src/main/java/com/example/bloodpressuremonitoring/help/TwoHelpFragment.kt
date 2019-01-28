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
import com.example.bloodpressuremonitoring.classify.CameraActivity

class TwoHelpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_two_help, container, false)
        val help2 = view!!.findViewById<Button>(R.id.help2_btn)
        val help21image = view.findViewById<ImageView>(R.id.help2_1_imageView)
        val help22image = view.findViewById<ImageView>(R.id.help2_2_imageView)
        val help23image = view.findViewById<ImageView>(R.id.help2_3_imageView)

        val sv = ScrollView(context)
        val ll = LinearLayout(context)
        ll.setOrientation(LinearLayout.VERTICAL)
        sv.addView(ll)

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.getDefaultDisplay()
        val size = Point()
        display.getSize(size)
        val intendedWidth = size.x*80/100

        val bd1 = this.resources.getDrawable(R.drawable.help2_1) as BitmapDrawable
        val width1: Double = bd1.bitmap.width.toDouble()
        val height1: Double = bd1.bitmap.height.toDouble()
        val scale1 = intendedWidth / width1
        val newHeight1 = Math.round(height1 * scale1).toInt()

        val params1 = LinearLayout.LayoutParams(intendedWidth, newHeight1)
        params1.gravity = Gravity.CENTER_HORIZONTAL
        help21image.setLayoutParams(params1)

        val bd2 = this.resources.getDrawable(R.drawable.help2_2) as BitmapDrawable
        val width2: Double = bd2.bitmap.width.toDouble()
        val height2: Double = bd2.bitmap.height.toDouble()
        val scale2 = intendedWidth / width2
        val newHeight2 = Math.round(height2 * scale2).toInt()

        val params2 = LinearLayout.LayoutParams(intendedWidth, newHeight2)
        params2.gravity = Gravity.CENTER_HORIZONTAL
        help22image.setLayoutParams(params2)

        val bd3 = this.resources.getDrawable(R.drawable.help2_3) as BitmapDrawable
        val width3: Double = bd3.bitmap.width.toDouble()
        val height3: Double = bd3.bitmap.height.toDouble()
        val scale3 = intendedWidth / width3
        val newHeight3 = Math.round(height3 * scale3).toInt()

        val params3 = LinearLayout.LayoutParams(intendedWidth, newHeight3)
        params3.gravity = Gravity.CENTER_HORIZONTAL
        help23image.setLayoutParams(params3)

        help2.setOnClickListener {
            activity.finish()
            val intent = Intent(context, CameraActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
