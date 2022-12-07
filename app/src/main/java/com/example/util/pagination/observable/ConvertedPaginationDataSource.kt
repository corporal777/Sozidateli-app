package com.example.util.pagination.observable

import androidx.paging.DataSource

class ConvertedPaginationDataSource<I, R>(
    private val source: PaginationDataSource<I>,
    private val converter: (item: I, index: Int, total: Int?) -> R
) : PaginationDataSource<R>() {

    private var totalCount: Int? = 0

    override fun addInvalidatedCallback(onInvalidatedCallback: DataSource.InvalidatedCallback) {
        source.addInvalidatedCallback(onInvalidatedCallback)
    }

    override fun removeInvalidatedCallback(onInvalidatedCallback: DataSource.InvalidatedCallback) {
        source.removeInvalidatedCallback(onInvalidatedCallback)
    }

    override fun invalidate() = source.invalidate()

    override fun isInvalid() = source.isInvalid

    override fun loadInitial(params: LoadInitialParams,
                             callback: LoadInitialCallback<R>) {
        source.loadInitial(params, object : LoadInitialCallback<I>() {
            override fun onResult(data: List<I>, position: Int, totalCount: Int) {
                this@ConvertedPaginationDataSource.totalCount = totalCount
                callback.onResult(convertResult(data, position, totalCount), position, totalCount)
            }

            override fun onResult(data: List<I>, position: Int) {
                totalCount = null
                callback.onResult(convertResult(data, position, totalCount), position)
            }
        })
    }

    override fun loadRange(params: LoadRangeParams,
                           callback: LoadRangeCallback<R>) {
        source.loadRange(params, object : LoadRangeCallback<I>() {
            override fun onResult(data: List<I>) {
                callback.onResult(convertResult(data, params.startPosition, totalCount))
            }
        })
    }

    private fun convertResult(result: List<I>?, startPosition: Int, total: Int?): List<R> {
        return result?.mapIndexed { index, item -> converter.invoke(item, startPosition + index, total) }
                ?: emptyList()
    }
}