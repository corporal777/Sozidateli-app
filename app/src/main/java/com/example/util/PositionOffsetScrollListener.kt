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
        (recyclerView.layoutManager as LinearLayoutManager).apply {
            val firstVisiblePosition = findFirstVisibleItemPosition()
            val firstVisibleView = findViewByPosition(firstVisiblePosition)
            onScroll.invoke(firstVisiblePosition, calculateOffset(firstVisibleView, recyclerView, this))
        }
    }

    private fun calculateOffset(firstVisibleView: View?, recyclerView: RecyclerView, layoutManager: LinearLayoutManager): Int {
        return if (firstVisibleView == null) 0
        else when (orientation) {
            RecyclerView.VERTICAL -> calculateOffsetVertical(firstVisibleView, recyclerView, layoutManager)
            RecyclerView.HORIZONTAL -> calculateOffsetHorizontal(firstVisibleView, recyclerView)
            else -> 0
        }
    }

    private fun calculateOffsetVertical(firstVisibleView: View, recyclerView: RecyclerView, layoutManager: LinearLayoutManager): Int {
        val itemSide: Int
        val margin: Int
        val parentPadding: Int
        if (layoutManager.reverseLayout) {
            itemSide =  recyclerView.bottom - firstVisibleView.bottom
            margin = (firstVisibleView.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin
                    ?: 0
            parentPadding = if (recyclerView.clipChildren) recyclerView.paddingBottom else 0
        } else {
            itemSide = firstVisibleView.top
            margin = (firstVisibleView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin
                    ?: 0
            parentPadding = if (recyclerView.clipChildren) recyclerView.paddingTop else 0
        }
        return itemSide - margin - parentPadding
    }

    private fun calculateOffsetHorizontal(firstVisibleView: View, recyclerView: RecyclerView): Int {
        val left = firstVisibleView.left
        val margin = (firstVisibleView.layoutParams as? ViewGroup.MarginLayoutParams)?.leftMargin
                ?: 0
        val parentPadding = if (recyclerView.clipChildren) recyclerView.paddingLeft else 0
        return left - margin - parentPadding
    }
}