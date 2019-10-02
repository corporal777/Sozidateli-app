package com.example.util.pagination

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import java.lang.ref.WeakReference

class PaginationListGroupAdapter<VH : GroupieViewHolder> : GroupAdapter<VH>() {

    private var weakPaginationListItemTakeCallback: WeakReference<OnItemTakeCallback>? = null

    fun setOnItemTakeCallback(callback: OnItemTakeCallback) {
        weakPaginationListItemTakeCallback = WeakReference(callback)
    }

    override fun getItem(position: Int): Item<*> {
        weakPaginationListItemTakeCallback?.get()?.onItemTake(position)
        return super.getItem(position)
    }

    interface OnItemTakeCallback {
        fun onItemTake(position: Int)
    }
}