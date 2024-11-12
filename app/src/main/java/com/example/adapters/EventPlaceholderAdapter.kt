package com.example.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.app.R
import com.example.app.databinding.ItemEventNewPlaceholderBinding
import com.example.app.databinding.ItemEventPlaceholderBinding
import com.example.app.databinding.ItemUserPlaceholderBinding

class EventPlaceholderAdapter(val count: Int) :
    CustomLoadStateAdapter<EventPlaceholderAdapter.EventPlaceholderViewHolder>() {


    override fun getViewHolder(view: ViewGroup): EventPlaceholderViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(view.context)
        return EventPlaceholderViewHolder(
            layoutInflater.inflate(
                R.layout.item_event_new_placeholder,
                view,
                false
            )
        )
    }

    override fun getItemsCount(): Int = count


    override fun onBindViewHolder(holder: EventPlaceholderViewHolder, position: Int) {
        holder.bind()
    }


    inner class EventPlaceholderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val viewBinding by viewBinding(ItemEventNewPlaceholderBinding::bind)

        fun bind() {
            with(viewBinding) {}
        }
    }
}