package com.example.extensions

import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.example.util.pagination.observable.PaginationList
import io.reactivex.Observable

fun <K, V> DataSource.Factory<K, V>.build(initialSize: Int = 20, pageSize: Int = initialSize, enablePlaceholders: Boolean = false): Observable<PagedList<V>> {
    val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(initialSize)
            .setPageSize(pageSize)
            .setEnablePlaceholders(enablePlaceholders)
            .build()
    return RxPagedListBuilder(this, config).buildObservable()
}

fun <K, V> DataSource.Factory<K, V>.buildList(initialSize: Int = 20, pageSize: Int = initialSize, enablePlaceholders: Boolean = false): PaginationList<V> {
    return PaginationList(this.build(initialSize, pageSize, enablePlaceholders))
}