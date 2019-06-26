package com.example.util.pagination

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PaginationScrollListener(
        private val itemsOffset: Int = 0,
        private val onNeedLoadPrevious: () -> Unit,
        private val onNeedLoadNext: () -> Unit
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

        val firstPosition: Int = layoutManager.findLastVisibleItemPosition()
        val lastPosition: Int = layoutManager.findFirstVisibleItemPosition()

        if (firstPosition + itemsOffset >= layoutManager.itemCount - 1) onNeedLoadPrevious()
        if (lastPosition - itemsOffset <= 0) onNeedLoadNext()
    }
}