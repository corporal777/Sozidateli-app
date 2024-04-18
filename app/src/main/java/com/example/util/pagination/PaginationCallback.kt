package com.example.util.pagination

import androidx.paging.PagedList

class PaginationCallback(private val onListUpdated: () -> Unit) : PagedList.Callback() {

    override fun onChanged(position: Int, count: Int) = onListUpdated()

    override fun onInserted(position: Int, count: Int) = onListUpdated()

    override fun onRemoved(position: Int, count: Int) = onListUpdated()
}