package com.example.util.pagination

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PaginationScrollListener(
        private val itemsOffset: Int = 0,
        private val onNeedLoadMore: () -> Unit
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

        val itemPosition = if (layoutManager.reverseLayout) layoutManager.findLastVisibleItemPosition()
        else layoutManager.findFirstVisibleItemPosition()

        val offset = if (layoutManager.reverseLayout) itemsOffset
        else -itemsOffset

        if (itemPosition + offset >= layoutManager.itemCount - 1) onNeedLoadMore()
    }
}