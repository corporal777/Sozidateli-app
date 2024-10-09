package com.example.ui.views.educationlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R

class PopupWindowAdapter(private val values: List<String>, private val onClick: (item: String) -> Unit): RecyclerView.Adapter<PopupWindowAdapter.PopupHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopupHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_search_education, parent, false)
        return PopupHolder(itemView)
    }

    override fun onBindViewHolder(holder: PopupHolder, position: Int) {
        holder.name?.apply {
            text = values[position]
            setOnClickListener {
                onClick(values[position])
            }
        }
    }

    class PopupHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView? = null

        init {
            name = itemView.findViewById(R.id.tv_name)
        }
    }
}