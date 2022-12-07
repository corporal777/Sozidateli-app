package com.example.util.pagination.flow

import androidx.paging.DataSource
import com.example.util.pagination.DataSourceFactory
import com.example.util.pagination.PaginationResponse
import io.reactivex.Flowable

@Suppress("UNCHECKED_CAST")
open class PaginationDataSourceFactoryNew<I>(
    private val paginationRequest: (limit: Int, offset: Int) -> Flowable<PaginationResponse<I>>
) : DataSourceFactory<Int, I>() {

    var paginationErrorHandler: PaginationErrorHandler? = null

    private var loadFromStart = false

    override fun createDataSource(): PaginationDataSourceNew<I> {
        val source = PaginationDataSourceNew<I>().apply {
            this.request = paginationRequest
            this.errorHandler = paginationErrorHandler
            this.loadInitialFromStart = loadFromStart
        }
        loadFromStart = false
        return source
    }

    fun <R> map(converter: (item: I) -> R) = mapIndexedTotal { item, _, _ -> converter.invoke(item) }

    fun <R> mapIndexed(converter: (item: I, index: Int) -> R) = mapIndexedTotal { item, index, _ -> converter.invoke(item, index) }

    fun <R> mapIndexedTotal(converter: (item: I, index: Int, total: Int?) -> R) = object : DataSourceFactory<Int, R>() {
        override fun createDataSource(): DataSource<Int, R> {
            return this@PaginationDataSourceFactoryNew.createDataSource().mapIndexed(converter)
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

fun <I> PaginationDataSourceFactoryNew<I>.applyErrorHandler(handler: PaginationErrorHandler): PaginationDataSourceFactoryNew<I> {
    paginationErrorHandler = handler
    return this
}