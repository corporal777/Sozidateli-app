package com.example.util.pagination

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class PaginationListGroupAdapter<VH : GroupieViewHolder> : GroupAdapter<VH>() {

    private var onItemTakeCallback: OnItemTakeCallback? = null

    fun setOnItemTakeCallback(callback: OnItemTakeCallback?) {
        onItemTakeCallback = callback
    }

    override fun getItem(position: Int): Item<*> {
        onItemTakeCallback?.onItemTake(position)
        return super.getItem(position)
    }

    interface OnItemTakeCallback {
        fun onItemTake(position: Int)
    }
}