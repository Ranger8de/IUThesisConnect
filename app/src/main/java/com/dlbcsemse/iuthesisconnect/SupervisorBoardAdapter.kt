package com.dlbcsemse.iuthesisconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.SupervisorProfile
import com.dlbcsemse.iuthesisconnect.model.UserProfile

class SupervisorBoardAdapter(
    private val supervisors: List<SupervisorProfile>,
    private val dbHelper: DatabaseHelper,
    private val onItemClick: (SupervisorProfile) -> Unit
) : RecyclerView.Adapter<SupervisorBoardAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.betreuerboardBetreuernametextView)
        val imageView: ImageView = view.findViewById(R.id.betreuerboardBetreuerItemimageView)
        val subjectsTextView: TextView = view.findViewById(R.id.faecherBetreuerboardItemLayoutTextview)
        val availabilityTextView: TextView = view.findViewById(R.id.verfuegbarkeitBetreuerboardItemLayoutTextview)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(supervisors[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.betreuerboard_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val supervisor = supervisors[position]
        val userProfile = supervisor.userProfile
        if (userProfile != null) {
            holder.nameTextView.text = userProfile.userName
        }
        // Hier können Sie ein Bild setzen, wenn verfügbar
        holder.subjectsTextView.text = "Fächer werden bald hinzugefügt"
        holder.availabilityTextView.text = supervisor.status.toString()
    }

    override fun getItemCount() = supervisors.size
}