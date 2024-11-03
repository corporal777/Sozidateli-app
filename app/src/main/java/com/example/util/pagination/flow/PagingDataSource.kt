package com.example.util.pagination.flow

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.paging.PagingState
import androidx.paging.PositionalDataSource
import androidx.paging.rxjava2.RxPagingSource
import com.example.util.pagination.PaginationResponse
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlin.math.min

open class PagingDataSource<I : Any> : RxPagingSource<Int, I>() {

    lateinit var request: (limit: Int, offset: Int) -> Maybe<PaginationResponse<I>>

    private var loadFromStart = false
    private var lastRequestedDataSize = 0
    private var lastRequestedPage = 0


    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, I>> {
        val position = if (loadFromStart) lastRequestedPage else params.key ?: 0
        val limit = if (loadFromStart) lastRequestedDataSize else params.loadSize
        val offset = if (loadFromStart) 0 else (params.key ?: 0) * limit
        //val position = params.key ?: 0
        //val limit = params.loadSize
        //val offset = (params.key ?: 0) * limit

        return request.invoke(limit, offset)
            .map {
                val prevKey =  null
                val nextKey = if (it.data.isEmpty()) null
                else if (loadFromStart) position + 1
                else if (it.data.size < limit || it.data.size < offset) null
                else if (it.data.size == it.totalCount || limit >= (it.totalCount ?: 0)) null
                else position + 1

                loadFromStart = false
                toLoadResult(it, prevKey, nextKey)
            }
            .onErrorReturn { LoadResult.Error(it) }.toSingle()
    }

    override fun getRefreshKey(state: PagingState<Int, I>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null

        val anchorNextKey = anchorPage.nextKey ?: 0

        loadFromStart = true
        if (anchorNextKey > 0) {
            lastRequestedDataSize = 3 * state.config.pageSize
            lastRequestedPage = 2
        } else {
            lastRequestedDataSize = state.config.pageSize
            lastRequestedPage = 0
        }
        return if (loadFromStart) 0
        else anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }

    private fun toLoadResult(data: PaginationResponse<I>, prevKey : Int?, nextKey : Int?): LoadResult<Int, I> {
        return LoadResult.Page(
            data = data.data,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }

    fun invalidateFromStart(){
        invalidate()
    }
}
