package com.example.util

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PositionOffsetScrollListener(
        private val orientation: Int? = LinearLayoutManager.VERTICAL,
        private val onScroll: (position: Int, offset: Int) -> Unit)
    : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleView = layoutManager.findViewByPosition(firstVisiblePosition)
        onScroll.invoke(firstVisiblePosition, calculateOffset(firstVisibleView, recyclerView))
    }

    private fun calculateOffset(firstVisibleView: View?, recyclerView: RecyclerView): Int {
        return if (firstVisibleView == null) 0
        else when (orientation) {
            RecyclerView.VERTICAL -> calculateOffsetVertical(firstVisibleView, recyclerView)
            RecyclerView.HORIZONTAL -> calculateOffsetHorizontal(firstVisibleView, recyclerView)
            else -> 0
        }
    }

    private fun calculateOffsetVertical(firstVisibleView: View, recyclerView: RecyclerView): Int {
        val top = firstVisibleView.top
        val margin = (firstVisibleView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin
                ?: 0
        val parentPadding = if (recyclerView.clipChildren) recyclerView.paddingTop else 0
        return top - margin - parentPadding
    }

    private fun calculateOffsetHorizontal(firstVisibleView: View, recyclerView: RecyclerView): Int {
        val left = firstVisibleView.left
        val margin = (firstVisibleView.layoutParams as? ViewGroup.MarginLayoutParams)?.leftMargin
                ?: 0
        val parentPadding = if (recyclerView.clipChildren) recyclerView.paddingLeft else 0
        return left - margin - parentPadding
    }
}