package com.dlbcsemse.iuthesisconnect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.util.zip.Inflater

class DashboardItemAdapter (
    val context : Context, val item : List <DashboardItem>) : BaseAdapter() {

    override fun getCount(): Int {
        return item.size
    }

    override fun getItem(position: Int): Any {
        return item[position]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View
        if (convertView == null) {
            view = inflater.inflate(R.layout.dashboard_item_layout, parent, false)
        } else {
            view = convertView
        }
        val item = getItem(position) as DashboardItem
        val itemTextView = view.findViewById<TextView>(R.id.dashboardItemtextView)
        val itemImageView = view.findViewById<ImageView>(R.id.dashboardItemimageView)
        itemTextView.text = item.itemName
        itemImageView.setImageResource(item.ImageID)
        itemImageView.setOnClickListener {
            Toast.makeText(context, "was", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}