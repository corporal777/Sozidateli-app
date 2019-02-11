package com.example.util.pagination

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe

class SimplePagination<I>(
        paginationRequest: (limit: Int, offset: Int) -> Maybe<PaginationResponse<I>>
) : PaginationDataSourceFactory<I>(paginationRequest) {

    fun build(initialSize: Int = 20, pageSize: Int = 20, enablePlaceholders: Boolean = false): Flowable<PagedList<I>> {
        val config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(initialSize)
                .setPageSize(pageSize)
                .setEnablePlaceholders(enablePlaceholders)
                .build()

        return RxPagedListBuilder(this, config)
                .buildFlowable(BackpressureStrategy.LATEST)
    }
}