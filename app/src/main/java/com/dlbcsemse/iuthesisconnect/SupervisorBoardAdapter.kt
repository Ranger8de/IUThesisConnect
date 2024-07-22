package com.dlbcsemse.iuthesisconnect

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.dlbcsemse.iuthesisconnect.helper.DatabaseHelper
import com.dlbcsemse.iuthesisconnect.model.AvailabilityStatus
import com.dlbcsemse.iuthesisconnect.model.SupervisorProfile
import com.dlbcsemse.iuthesisconnect.model.UserProfile
import java.util.Base64

class SupervisorBoardAdapter(
    private var supervisors: List<SupervisorProfile>,
    private val dbHelper: DatabaseHelper,
    private val onItemClick: (SupervisorProfile) -> Unit
) : RecyclerView.Adapter<SupervisorBoardAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.betreuerboardBetreuernametextView)
        val imageView: ImageView = view.findViewById(R.id.betreuerboardBetreuerItemimageView)
        val subjectsTextView: TextView = view.findViewById(R.id.faecherBetreuerboardItemLayoutTextview)
        val availabilityTextView: TextView = view.findViewById(R.id.verfuegbarkeitBetreuerboardItemLayoutTextview)
        val availabilityImageView : ImageView = view.findViewById(R.id.betreuerboardImageViewStatus)
        val _view : View = view
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
            holder.nameTextView.text = supervisor.userProfile.userName

        // Hier können Sie ein Bild setzen, wenn verfügbar
        holder.subjectsTextView.text = supervisor.topicCategories.joinToString("\r\n")
        holder.availabilityTextView.text = holder._view.context.getString( getAvailabilityText(supervisor.status))

        val decodedString: ByteArray = Base64.getDecoder().decode(supervisor.userProfile.picture.toByteArray())
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        holder.imageView.setImageBitmap(decodedByte)

        holder.availabilityImageView.setImageResource(getAvailabilityFlag(supervisor.status))
    }

    private fun getAvailabilityFlag(status: AvailabilityStatus): Int {
        return when (status) {
            AvailabilityStatus.free -> {
                R.drawable.flag_green
            }
            AvailabilityStatus.blocked -> {
                R.drawable.flag_red
            }
            AvailabilityStatus.limited -> {
                R.drawable.flag_yellow
            }
        }
    }

    private fun getAvailabilityText(status: AvailabilityStatus): Int {
        return when (status) {
            AvailabilityStatus.free -> {
                R.string.availabilityStatus_free
            }
            AvailabilityStatus.blocked -> {
                R.string.availabilityStatus_blocked
            }
            AvailabilityStatus.limited -> {
                R.string.availabilityStatus_limited
            }
        }
    }
    fun updateSupervisors(newSupervisors: List<SupervisorProfile>) {
        supervisors = newSupervisors
        notifyDataSetChanged()
    }

    override fun getItemCount() = supervisors.size
}