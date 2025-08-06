package com.example.util.paginationNew

import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import kotlinx.coroutines.Deferred

open class PagingSourceFactory<I : Any>(
    private val paginationRequest: (limit: Int, offset: Int) -> Deferred<PaginationResponse<I>>
) {

    var source: EventPagingSource<I>? = null

    fun createDataSource(): EventPagingSource<I> {
        val source = EventPagingSource<I>().apply {
            this.request = paginationRequest
        }
        this.source = source
        return source
    }
}