package com.example.util.paginationNew

import android.os.Handler
import android.os.Looper
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.example.exceptions.EmptyDataException
import com.example.util.pagination.PaginationResponse
import io.reactivex.Maybe
import io.reactivex.Single

open class PagingDataSource<I : Any> : RxPagingSource<Int, I>() {

    lateinit var request: (limit: Int, offset: Int) -> Maybe<PaginationResponse<I>>
    var errorHandler: PagingErrorHandler? = null

    private var loadFromStart = false
    private var lastRequestedDataSize = 0
    private var lastRequestedPage = 0
    private var lastRequestedKey = 0


    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, I>> {
        return try {
            //val position = params.key ?: 0
            //val limit = params.loadSize
            //val offset = (params.key ?: 0) * limit

            val position = if (loadFromStart) lastRequestedKey else params.key ?: 0
            val limit = if (loadFromStart) {
                if (lastRequestedKey == 0) params.loadSize else params.loadSize * lastRequestedKey
            } else params.loadSize
            val offset = if (loadFromStart) 0 else (params.key ?: 0) * limit

            request.invoke(limit, offset).flatMapSingle {
                if (it.isEmptyData()) Single.error(EmptyDataException())
                else {
                    val prevKey = null
                    val nextKey = if (it.data.isEmpty()) null
                    else if (it.data.size < limit || limit >= (it.totalCount ?: 0)) null
                    else if (it.data.size == it.totalCount) null
                    //else if (it.data.size == it.totalCount) position
                    else if (loadFromStart) position
                    else position + 1

                    loadFromStart = false
                    Single.just(toLoadResult(it, prevKey, nextKey))
                }
            }.doOnError { executeError(it) }
                //.onErrorReturn { LoadResult.Error(it) }
                .onErrorResumeNext { Single.just(LoadResult.Error(it)) }
        } catch (e: Exception) {
            executeError(e)
            Single.just(LoadResult.Error(e))
        }

    }

    override fun getRefreshKey(state: PagingState<Int, I>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        val anchorNextKey = anchorPage.nextKey ?: 0

        loadFromStart = true

        if (anchorNextKey > 0) {
            lastRequestedKey = anchorPage.nextKey ?: 0
            lastRequestedDataSize = 3 * state.config.pageSize
            lastRequestedPage = 2
        } else {
            lastRequestedDataSize = state.config.pageSize
            lastRequestedPage = 0
        }
        return if (loadFromStart) 0
        else anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
        //return 0
    }

    private fun toLoadResult(
        data: PaginationResponse<I>,
        prevKey: Int?,
        nextKey: Int?
    ): LoadResult<Int, I> {
        return LoadResult.Page(data = data.data, prevKey = prevKey, nextKey = nextKey)
    }

    private fun executeError(t: Throwable) {
        val errorHandler = errorHandler ?: throw t
        Handler(Looper.getMainLooper()).post { errorHandler(t) }
    }

    fun invalidateFromStart() {
        invalidate()
    }
}

typealias PagingErrorHandler = (Throwable) -> Unit
