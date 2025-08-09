package com.example.util.paginationNew

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.examle.domain.model.PaginationResponse

open class EventPagingSource<I : Any>() : PagingSource<Int, I>() {

    lateinit var request: suspend (limit: Int, offset: Int) -> PaginationResponse<I>

    private var loadFromStart = false
    private var lastRequestedDataSize = 0
    private var lastRequestedPage = 0
    private var lastRequestedKey = 0


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, I> {
        return try {

            val position = if (loadFromStart) lastRequestedKey else params.key ?: 0
            val limit = if (loadFromStart) {
                if (lastRequestedKey == 0) params.loadSize else params.loadSize * lastRequestedKey
            } else params.loadSize
            val offset = if (loadFromStart) 0 else (params.key ?: 0) * limit

            val response = request.invoke(limit, offset)

            if (response.isEmptyData()) {
                LoadResult.Page(response.data, null, null)
            } else {
                val prevKey = null
                val nextKey = if (response.data.isEmpty()) null
                else if (response.data.size < limit || limit >= (response.totalCount ?: 0)) null
                else if (response.data.size == response.totalCount) null
                //else if (it.data.size == it.totalCount) position
                else if (loadFromStart) position
                else position + 1

                loadFromStart = false
                LoadResult.Page(data = response.data, prevKey = prevKey, nextKey = nextKey)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
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
}