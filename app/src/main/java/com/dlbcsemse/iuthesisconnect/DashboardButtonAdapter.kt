package com.dlbcsemse.iuthesisconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dlbcsemse.iuthesisconnect.DashboardItem
import com.dlbcsemse.iuthesisconnect.R

class DashboardButtonAdapter(
    private val items: ArrayList<DashboardItem>,
    private val onItemClick: (DashboardItem) -> Unit) : RecyclerView.Adapter<DashboardButtonAdapter.ButtonViewHolder>() {

    class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.dashboardItemimageView)
        val titleView: TextView = itemView.findViewById(R.id.dashboardItemtextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dashboard_item_layout, parent, false)
        return ButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val item = items[position]
        holder.titleView.text = item.itemName
        holder.imageView.setImageResource(item.itemImageID)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = items.size
}