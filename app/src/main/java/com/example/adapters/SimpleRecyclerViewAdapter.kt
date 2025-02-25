package com.example.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class SimpleRecyclerViewAdapter() : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(getItemLayout(viewType), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBindItem(holder, position)
    }

    @LayoutRes
    abstract fun getItemLayout(itemView: Int): Int

    abstract fun onBindItem(holder: ViewHolder, position: Int)
}