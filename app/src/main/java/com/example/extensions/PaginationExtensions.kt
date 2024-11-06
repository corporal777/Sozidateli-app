package com.example.extensions

import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RxPagedListBuilder
import androidx.paging.rxjava2.flowable
import com.example.util.pagination.flow.PagingDataSourceFactory
import com.example.util.pagination.flow.PagingList
import com.example.util.pagination.observable.PaginationList
import io.reactivex.Flowable
import io.reactivex.Observable

fun <K : Any, V : Any> DataSource.Factory<K, V>.build(
    initialSize: Int = 20,
    pageSize: Int = initialSize,
    enablePlaceholders: Boolean = false
): Observable<PagedList<V>> {
    val config = PagedList.Config.Builder()
        .setInitialLoadSizeHint(initialSize)
        .setPageSize(pageSize)
        .setEnablePlaceholders(enablePlaceholders)
        .build()
    return RxPagedListBuilder(this, config).buildObservable()
}

fun <K : Any, V : Any> DataSource.Factory<K, V>.buildList(
    initialSize: Int = 20,
    pageSize: Int = initialSize,
    enablePlaceholders: Boolean = false
): PaginationList<V> {
    return PaginationList(this.build(initialSize, pageSize, enablePlaceholders))
}

fun <K : Any> PagingDataSourceFactory<K>.buildList(
    initialSize: Int = 20,
    pageSize: Int = initialSize,
    distance : Int = 5,
    enablePlaceholders: Boolean = false
): PagingList<K> {
    val config = PagingConfig(
        pageSize = pageSize,
        initialLoadSize = initialSize,
        //maxSize = pageSize * 3,
        prefetchDistance = distance,
        enablePlaceholders = enablePlaceholders
    )
    val pager = Pager(config = config, pagingSourceFactory = { this.createDataSource() }).flowable
    return PagingList(pager, this)
}