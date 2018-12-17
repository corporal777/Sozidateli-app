package com.example.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PositionOffsetScrollListener(
        private val onScroll: (position: Int, offset: Int) -> Unit)
    : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleView = layoutManager.findViewByPosition(firstVisiblePosition)
        onScroll.invoke(firstVisiblePosition, (firstVisibleView?.top
                ?: 0) - recyclerView.paddingTop)
    }
}