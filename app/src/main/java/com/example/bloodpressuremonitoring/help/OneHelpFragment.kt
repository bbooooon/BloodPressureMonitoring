package com.example.bloodpressuremonitoring.help

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.graphics.drawable.BitmapDrawable
import android.view.WindowManager
import android.view.Gravity
import com.example.bloodpressuremonitoring.R
import com.example.bloodpressuremonitoring.SessionManager

class OneHelpFragment : Fragment() {
    lateinit var session: SessionManager

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_one_help, container, false)
        val help1btn = view.findViewById<Button>(R.id.help1_btn)
        val help1image = view.findViewById<ImageView>(R.id.help1_imageView)

        val bd: BitmapDrawable

        help1image.setImageResource(R.drawable.applogo)
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

        val width: Double = bd.bitmap.width.toDouble()
        val height: Double = bd.bitmap.height.toDouble()
        val scale = intendedWidth / width
        val newHeight = Math.round(height * scale).toInt()

        val params = LinearLayout.LayoutParams(intendedWidth, newHeight)
        params.gravity = Gravity.CENTER_HORIZONTAL
        help1image.layoutParams = params

        help1btn.setOnClickListener {
            activity.finish()
        }
        return view
    }
}
