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
import com.example.bloodpressuremonitoring.History.HistoryActivity
import com.example.bloodpressuremonitoring.R

class ThreeHelpFragment : Fragment() {
    private var username: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_three_help, container, false)
        val help3 = view.findViewById<Button>(R.id.help3_btn)
        val help31image = view.findViewById<ImageView>(R.id.help3_1_imageView)
        val help32image = view.findViewById<ImageView>(R.id.help3_2_imageView)
        val help33image = view.findViewById<ImageView>(R.id.help3_3_imageView)


        val bd: BitmapDrawable
        help33image.setImageResource(R.drawable.applogo)
        bd = this.resources.getDrawable(R.drawable.applogo) as BitmapDrawable


        val sv = ScrollView(context)
        val ll = LinearLayout(context)
        ll.setOrientation(LinearLayout.VERTICAL)
        sv.addView(ll)
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.getDefaultDisplay()
        val size = Point()
        display.getSize(size)
        val intendedWidth = size.x*80/100

        val bd1 = this.resources.getDrawable(R.drawable.applogo) as BitmapDrawable
        val width1: Double = bd1.bitmap.width.toDouble()
        val height1: Double = bd1.bitmap.height.toDouble()
        val scale1 = intendedWidth / width1
        val newHeight1 = Math.round(height1 * scale1).toInt()

        val params1 = LinearLayout.LayoutParams(intendedWidth, newHeight1)
        params1.gravity = Gravity.CENTER_HORIZONTAL
        help31image.setLayoutParams(params1)

        val bd2 = this.resources.getDrawable(R.drawable.applogo) as BitmapDrawable
        val width2: Double = bd2.bitmap.width.toDouble()
        val height2: Double = bd2.bitmap.height.toDouble()
        val scale2 = intendedWidth / width2
        val newHeight2 = Math.round(height2 * scale2).toInt()

        val params2 = LinearLayout.LayoutParams(intendedWidth, newHeight2)
        params2.gravity = Gravity.CENTER_HORIZONTAL
        help32image.setLayoutParams(params2)

        val width3: Double = bd.bitmap.width.toDouble()
        val height3: Double = bd.bitmap.height.toDouble()
        val scale3 = intendedWidth / width3
        val newHeight3 = Math.round(height3 * scale3).toInt()

        val params3 = LinearLayout.LayoutParams(intendedWidth, newHeight3)
        params3.gravity = Gravity.CENTER_HORIZONTAL
        help33image.setLayoutParams(params3)

        help3.setOnClickListener {
            activity.finish()
                val intent = Intent(context, HistoryActivity::class.java)
                startActivity(intent)
        }

        return view
    }
}
