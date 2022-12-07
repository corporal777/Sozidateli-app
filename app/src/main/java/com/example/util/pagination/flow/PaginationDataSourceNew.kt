package com.example.util.pagination.flow

import android.os.Handler
import android.os.Looper
import androidx.paging.PositionalDataSource
import com.example.util.pagination.PaginationResponse
import io.reactivex.Flowable
import kotlin.math.min

open class PaginationDataSourceNew<I> : PositionalDataSource<I>() {

    lateinit var request: (limit: Int, offset: Int) -> Flowable<PaginationResponse<I>>

    var errorHandler: PaginationErrorHandler? = null

    var loadInitialFromStart: Boolean = false

    private var lastTotalCount = 0

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<I>) {
        if (lastTotalCount <= params.startPosition) {
            callback.onResult(emptyList())
            return
        }

        val result = executeRequest(params.loadSize, params.startPosition)
        val data = getDataFromResult(result)
        result?.totalCount?.let { lastTotalCount = it }
        callback.onResult(data)
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<I>) {
        val startPosition = when {
            loadInitialFromStart || params.requestedStartPosition < params.pageSize -> 0
            else -> params.requestedStartPosition
        }

        val result = executeRequest(params.requestedLoadSize, startPosition)
        if (result == null) {
            callback.onResult(emptyList(), startPosition, 0)
            return
        }
        var data = getDataFromResult(result)
        val totalCount = result.totalCount
        result.data

        if (totalCount == null) {
            callback.onResult(data, startPosition)
        } else {
            var dataPosition = startPosition
            if (data.isEmpty() && totalCount > 0) {
                dataPosition = totalCount - min(params.requestedLoadSize, totalCount)
                data = executeRequestData(params.requestedLoadSize, dataPosition)
            }
            if (totalCount == 0) dataPosition = 0

            lastTotalCount = totalCount
            //callback.onResult(data, dataPosition, totalCount)
            try {
                callback.onResult(data, dataPosition, totalCount)
            } catch (e: Exception) {
                e.printStackTrace()
                if (e.message == "List size + position too large, last item in list beyond totalCount."){
                    callback.onResult(data, dataPosition, data.size)
                }
            }
        }
    }

    private fun executeRequest(limit: Int, offset: Int): PaginationResponse<I>? {
        val request = request.invoke(limit, offset)

        return try {
            request.blockingLast()
        } catch (t: Throwable) {
            val errorHandler = errorHandler ?: throw t
            Handler(Looper.getMainLooper()).post { errorHandler(t) }
            null
        }
    }

    private fun executeRequestData(limit: Int, offset: Int): List<I> {
        return getDataFromResult(executeRequest(limit, offset))
    }

    private fun getDataFromResult(result: PaginationResponse<I>?): List<I> {
        return result?.data ?: emptyList()
    }

    fun <R> map(converter: (item: I) -> R): ConvertedPaginationDataSourceNew<I, R> {
        return ConvertedPaginationDataSourceNew(this) { item, _, _ -> converter.invoke(item) }
    }

    fun <R> mapIndexed(converter: (item: I, index: Int, total: Int?) -> R): ConvertedPaginationDataSourceNew<I, R> {
        return ConvertedPaginationDataSourceNew(this, converter)
    }
}

typealias PaginationErrorHandler = (Throwable) -> Unit