package com.example.util.pagination

import android.util.Log
import com.example.util.PAGE_SIZE
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class PaginationGroupAdapter <VH : GroupieViewHolder> : GroupAdapter<VH>() {

    private var onItemTakeCallback: OnItemTakeCallback? = null

    fun setOnItemTakeCallback(callback: OnItemTakeCallback?) {
        onItemTakeCallback = callback
    }

    override fun getItem(pos: Int): Item<*> {
        val position = if ((pos - 1) > 0) pos - 1 else pos
        if (itemCount <= position) return super.getItem(pos)
        else if (itemCount < PAGE_SIZE) return super.getItem(pos)
        else {
            if ((itemCount - position) <= 5) onItemTakeCallback?.onItemTake(position)
            return super.getItem(pos)
        }
    }


    interface OnItemTakeCallback {
        fun onItemTake(position: Int)
    }
}