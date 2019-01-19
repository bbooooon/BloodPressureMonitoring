package com.example.bloodpressuremonitoring.History

import android.content.Context
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.BaseAdapter
import com.example.bloodpressuremonitoring.R
import java.util.*
import kotlin.collections.ArrayList


class ListViewAdapter(mContext: Context, animalNamesList: ArrayList<String>) : BaseAdapter() {
    internal var inflater: LayoutInflater
    private var animalNamesList: MutableList<String>? = null
    private val arraylist: ArrayList<String>

    init {
        this.animalNamesList = animalNamesList
        inflater = LayoutInflater.from(mContext)
        this.arraylist = ArrayList<String>()
        this.arraylist.addAll(animalNamesList)
    }

    inner class ViewHolder {
        internal var name: TextView? = null
    }

    override fun getCount(): Int {
        return animalNamesList!!.size
    }

    override fun getItem(position: Int): String {
        return animalNamesList!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        val holder: ViewHolder
        if (view == null) {
            holder = ViewHolder()
            view = inflater.inflate(R.layout.listview_item, null)
//            // Locate the TextViews in listview_item.xml
            holder.name = view!!.findViewById<View>(R.id.name) as TextView
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
//        // Set the results into TextViews
        holder.name!!.setText(animalNamesList!![position])
        return view!!
    }

    // Filter Class
    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        animalNamesList!!.clear()
        if (charText.length == 0) {
            animalNamesList!!.addAll(arraylist)
        } else {
            for (wp in arraylist) {
                if (wp.toLowerCase(Locale.getDefault()).contains(charText)) {
                    animalNamesList!!.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }

}