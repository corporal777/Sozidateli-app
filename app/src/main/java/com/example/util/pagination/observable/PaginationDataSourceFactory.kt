package com.example.util.pagination.observable

import androidx.paging.DataSource
import com.example.util.pagination.DataSourceFactory
import com.examle.domain.model.PaginationResponse
import io.reactivex.Maybe

open class PaginationDataSourceFactory<I : Any>(
        private val paginationRequest: (limit: Int, offset: Int) -> Maybe<PaginationResponse<I>>
) : DataSourceFactory<Int, I>() {

    var paginationErrorHandler: PaginationErrorHandler? = null

    private var loadFromStart = false

    override fun createDataSource(): PaginationDataSource<I> {
        val source = PaginationDataSource<I>().apply {
            this.request = paginationRequest
            this.errorHandler = paginationErrorHandler
            this.loadInitialFromStart = loadFromStart
        }
        loadFromStart = false
        return source
    }

    override fun <R : Any> map(converter: (item: I) -> R) = mapIndexedTotal { item, _, _ -> converter.invoke(item) }

    fun <R : Any> mapIndexed(converter: (item: I, index: Int) -> R) = mapIndexedTotal { item, index, _ -> converter.invoke(item, index) }

    fun <R : Any> mapIndexedTotal(converter: (item: I, index: Int, total: Int?) -> R) = object : DataSourceFactory<Int, R>() {
        override fun createDataSource(): DataSource<Int, R> {
            return this@PaginationDataSourceFactory.createDataSource().mapIndexed(converter)
        }
    }

    fun invalidate() {
        source?.invalidate()
    }

    fun invalidateFromStart() {
        loadFromStart = true
        invalidate()
    }
}

fun <I : Any> PaginationDataSourceFactory<I>.applyErrorHandler(handler: PaginationErrorHandler): PaginationDataSourceFactory<I> {
    paginationErrorHandler = handler
    return this
}