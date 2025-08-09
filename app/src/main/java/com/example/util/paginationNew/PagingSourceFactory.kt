package com.example.util.paginationNew

import com.examle.domain.model.PaginationResponse

open class PagingSourceFactory<I : Any>(
    private val paginationRequest: suspend (limit: Int, offset: Int) -> PaginationResponse<I>
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