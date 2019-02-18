package com.example.util.pagination

import androidx.paging.DataSource
import io.reactivex.Maybe

@Suppress("UNCHECKED_CAST")
open class PaginationDataSourceFactory<I>(
        private val paginationRequest: (limit: Int, offset: Int) -> Maybe<PaginationResponse<I>>)
    : DataSourceFactory<Int, I>() {

    private var loadFromStart = false

    override fun createDataSource(): PaginationDataSource<I> {
        val source = PaginationDataSource<I>().apply {
            this.request = paginationRequest
            this.loadInitialFromStart = loadFromStart
        }
        loadFromStart = false
        return source
    }

    fun <R> map(converter: (item: I) -> R) = mapIndexedTotal { item, _, _ -> converter.invoke(item) }

    fun <R> mapIndexed(converter: (item: I, index: Int) -> R) = mapIndexedTotal { item, index, _ -> converter.invoke(item, index) }

    fun <R> mapIndexedTotal(converter: (item: I, index: Int, total: Int?) -> R) = object : DataSourceFactory<Int, R>() {
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