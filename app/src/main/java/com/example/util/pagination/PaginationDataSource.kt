package com.example.util.pagination

import androidx.paging.PositionalDataSource
import io.reactivex.Maybe
import kotlin.math.min

open class PaginationDataSource<I> : PositionalDataSource<I>() {

    lateinit var request: (limit: Int, offset: Int) -> Maybe<PaginationResponse<I>>
    var loadInitialFromStart: Boolean = false

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<I>) {
        callback.onResult(executeRequestData(params.loadSize, params.startPosition))
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<I>) {
        val startPosition = when {
            loadInitialFromStart || params.requestedStartPosition < params.pageSize -> 0
            else -> params.requestedStartPosition
        }

        val result = executeRequest(params.requestedLoadSize, startPosition)
        var data = getDataFromResult(result)
        val totalCount = result?.totalCount

        if (totalCount == null) {
            callback.onResult(data, startPosition)
        } else {
            var dataPosition = startPosition
            if (data.isEmpty() && totalCount > 0) {
                dataPosition = totalCount - min(params.requestedLoadSize, totalCount)
                data = executeRequestData(params.requestedLoadSize, dataPosition)
            }
            if (totalCount == 0) dataPosition = 0

            callback.onResult(data, dataPosition, totalCount)
        }
    }

    private fun executeRequest(limit: Int, offset: Int): PaginationResponse<I>? {
        val request = request.invoke(limit, offset)
        return request.blockingGet()
    }

    private fun executeRequestData(limit: Int, offset: Int): List<I> {
        return getDataFromResult(executeRequest(limit, offset))
    }

    private fun getDataFromResult(result: PaginationResponse<I>?): List<I> {
        return result?.data ?: emptyList()
    }

    fun <R> map(converter: (item: I) -> R): ConvertedPaginationDataSource<I, R> {
        return ConvertedPaginationDataSource(this) { item, _, _ -> converter.invoke(item) }
    }

    fun <R> mapIndexed(converter: (item: I, index: Int, total: Int?) -> R): ConvertedPaginationDataSource<I, R> {
        return ConvertedPaginationDataSource(this, converter)
    }
}