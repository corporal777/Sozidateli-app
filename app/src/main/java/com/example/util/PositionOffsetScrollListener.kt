package com.example.util

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class PositionOffsetScrollListener(
        private val onScroll: (position: Int, offset: Int) -> Unit)
    : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleView = layoutManager.findViewByPosition(firstVisiblePosition)
        onScroll.invoke(firstVisiblePosition, (firstVisibleView?.top
                ?: 0) - recyclerView.paddingTop)
    }
}