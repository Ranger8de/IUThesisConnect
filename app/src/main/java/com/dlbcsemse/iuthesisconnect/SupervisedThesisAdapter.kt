package com.dlbcsemse.iuthesisconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dlbcsemse.iuthesisconnect.model.Thesis

class SupervisedThesisAdapter(
    private val theses: List<Thesis>,
    private val onItemClick: (Thesis) -> Unit
) : RecyclerView.Adapter<SupervisedThesisAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val themeTextView: TextView = view.findViewById(R.id.thesisThemeTextView)
        val studentNameTextView: TextView = view.findViewById(R.id.studentNameTextView)
        val stateTextView: TextView = view.findViewById(R.id.thesisStateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.supervised_thesis_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val thesis = theses[position]
        holder.themeTextView.text = thesis.theme
        holder.studentNameTextView.text = "Student: ${thesis.student}"
        holder.stateTextView.text = "Status: ${thesis.state}"

        holder.itemView.setOnClickListener {
            onItemClick(thesis)
        }
    }

    override fun getItemCount() = theses.size
}