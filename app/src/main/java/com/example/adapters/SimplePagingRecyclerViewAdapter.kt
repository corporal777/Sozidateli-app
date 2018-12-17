package com.example.adapters

import androidx.paging.PagedListAdapter
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup

abstract class SimplePagingRecyclerViewAdapter<T>(
        areItemsTheSame: (oldItem: T, newItem: T) -> Boolean,
        areContentsTheSame: (oldItem: T, newItem: T) -> Boolean
) : PagedListAdapter<T, ViewHolder>(
        object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return areItemsTheSame(oldItem, newItem)
            }

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return areContentsTheSame(oldItem, newItem)
            }
        }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(getItemLayout(viewType), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        onBindItem(viewHolder, getItem(position), position)
    }

    @LayoutRes
    abstract fun getItemLayout(itemView: Int): Int

    abstract fun onBindItem(viewHolder: ViewHolder, item: T?, position: Int)
}