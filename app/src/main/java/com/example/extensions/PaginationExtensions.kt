package com.example.extensions

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.example.util.pagination.DataSourceFactory
import com.example.util.pagination.PaginationList
import io.reactivex.Observable

fun <K, V> DataSourceFactory<K, V>.build(initialSize: Int = 20, pageSize: Int = 20, enablePlaceholders: Boolean = false): Observable<PagedList<V>> {
    val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(initialSize)
            .setPageSize(pageSize)
            .setEnablePlaceholders(enablePlaceholders)
            .build()

    return RxPagedListBuilder(this, config)
            .buildObservable()
}

fun <K, V> DataSourceFactory<K, V>.buildList(initialSize: Int = 20, pageSize: Int = 20, enablePlaceholders: Boolean = false): PaginationList<V> {
    return PaginationList(this.build(initialSize, pageSize, enablePlaceholders))
}