package com.example.adapters

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

abstract class SimpleRecyclerViewAdapter<T>(var items: List<T>) : androidx.recyclerview.widget.RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(getItemLayout(viewType), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBindItem(holder, items[position], position)
    }

    @LayoutRes
    abstract fun getItemLayout(itemView: Int): Int

    abstract fun onBindItem(holder: ViewHolder, item: T?, position: Int)
}