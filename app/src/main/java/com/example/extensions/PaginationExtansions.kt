package com.example.extensions

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.example.util.pagination.DataSourceFactory
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

fun <K, V> DataSourceFactory<K, V>.build(initialSize: Int = 20, pageSize: Int = 20, enablePlaceholders: Boolean = false): Flowable<PagedList<V>> {
    val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(initialSize)
            .setPageSize(pageSize)
            .setEnablePlaceholders(enablePlaceholders)
            .build()

    return RxPagedListBuilder(this, config)
            .buildFlowable(BackpressureStrategy.LATEST)
}