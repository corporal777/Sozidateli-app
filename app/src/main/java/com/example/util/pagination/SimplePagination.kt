package com.example.util.pagination

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe

class SimplePagination<I>(
        private val paginationRequest: (limit: Int, offset: Int) -> Maybe<PaginationResponse<I>>
) {
    fun create(initialSize: Int = 20, pageSize: Int = 20, enablePlaceholders: Boolean = false): Flowable<PagedList<I>> {
        val factory = PaginationDataSourceFactory(paginationRequest)

        val config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(initialSize)
                .setPageSize(pageSize)
                .setEnablePlaceholders(enablePlaceholders)
                .build()

        return RxPagedListBuilder(factory, config)
                .buildFlowable(BackpressureStrategy.LATEST)
    }
}