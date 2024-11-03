package com.example.util.pagination.flow

import com.example.util.pagination.PaginationResponse
import io.reactivex.Flowable
import io.reactivex.Maybe


open class PagingDataSourceFactory<I : Any>(
    private val paginationRequest: (limit: Int, offset: Int) -> Maybe<PaginationResponse<I>>
)  {

    var source : PagingDataSource<I>? = null

    fun createDataSource(): PagingDataSource<I> {
        val source = PagingDataSource<I>().apply {
            this.request = paginationRequest
        }
        this.source = source
        return source
    }

}