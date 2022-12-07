package com.example.extensions

import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.example.util.pagination.observable.PaginationList
import com.example.util.pagination.flow.PaginationListFlow
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable

fun <K, V> DataSource.Factory<K, V>.build(initialSize: Int = 20, pageSize: Int = initialSize, enablePlaceholders: Boolean = false): Observable<PagedList<V>> {
    val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(initialSize)
            .setPageSize(pageSize)
            .setEnablePlaceholders(enablePlaceholders)
            .build()

    return RxPagedListBuilder(this, config)
            .buildObservable()
}

fun <K, V> DataSource.Factory<K, V>.buildList(initialSize: Int = 20, pageSize: Int = initialSize, enablePlaceholders: Boolean = false): PaginationList<V> {
    return PaginationList(this.build(initialSize, pageSize, enablePlaceholders))
}

fun <K, V> DataSource.Factory<K, V>.buildNew(initialSize: Int = 20, pageSize: Int = initialSize, enablePlaceholders: Boolean = false): Flowable<PagedList<V>> {
    val config = PagedList.Config.Builder()
        .setInitialLoadSizeHint(initialSize)
        .setPageSize(pageSize)
        .setEnablePlaceholders(enablePlaceholders)
        .build()

    return RxPagedListBuilder(this, config)
        .buildFlowable(BackpressureStrategy.BUFFER)
}

fun <K, V> DataSource.Factory<K, V>.buildListNew(initialSize: Int = 20, pageSize: Int = initialSize, enablePlaceholders: Boolean = false): PaginationListFlow<V> {
    return PaginationListFlow(this.buildNew(initialSize, pageSize, enablePlaceholders))
}