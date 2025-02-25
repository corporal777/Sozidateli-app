package com.example.extensions

import androidx.paging.CombinedLoadStates
import androidx.paging.DataSource
import androidx.paging.LoadState
import androidx.paging.PagedList
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingDataAdapter
import androidx.paging.RxPagedListBuilder
import androidx.paging.rxjava2.flowable
import androidx.paging.rxjava2.observable
import com.example.adapters.event.FavoriteEventPagingAdapter.Companion.withLoadStateAdapters
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.flow.PagingListFlow
import com.example.util.pagination.observable.PaginationList
import com.example.util.paginationNew.observable.PagingListObservable
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

fun <K : Any, V : Any> DataSource.Factory<K, V>.buildFlow(
    initialSize: Int = 20,
    pageSize: Int = initialSize,
    enablePlaceholders: Boolean = false
): PaginationList<V> {
    return PaginationList(this.build(initialSize, pageSize, enablePlaceholders))
}

fun <K : Any> PagingDataSourceFactory<K>.buildFlow(
    initialSize: Int = 20,
    pageSize: Int = initialSize,
    distance: Int = 5,
    enablePlaceholders: Boolean = false
): PagingListFlow<K> {
    val config = PagingConfig(
        pageSize = pageSize,
        initialLoadSize = initialSize,
        //maxSize = pageSize * 3,
        prefetchDistance = distance,
        enablePlaceholders = enablePlaceholders
    )
    val pager = Pager(config = config, pagingSourceFactory = { this.createDataSource() }).flowable
    return PagingListFlow(pager, this)
}

fun <K : Any> PagingDataSourceFactory<K>.buildObservable(
    initialSize: Int = 20,
    pageSize: Int = initialSize,
    distance: Int = 5,
    enablePlaceholders: Boolean = false
): PagingListObservable<K> {
    val config = PagingConfig(
        pageSize = pageSize,
        initialLoadSize = initialSize,
        //maxSize = pageSize * 3,
        prefetchDistance = distance,
        enablePlaceholders = enablePlaceholders
    )
    val pager = Pager(config = config, pagingSourceFactory = { this.createDataSource() }).observable
    return PagingListObservable(pager, this)
}

fun PagingDataAdapter<*, *>.executePlaceholderLoadState(
    loadState: CombinedLoadStates,
    onEmpty: (show: Boolean) -> Unit
) {

    if (loadState.refresh is LoadState.Error || loadState.refresh is LoadState.NotLoading)
        if (this.snapshot().isEmpty()) onEmpty.invoke(true)
        else onEmpty.invoke(false)
    else onEmpty.invoke(false)
}