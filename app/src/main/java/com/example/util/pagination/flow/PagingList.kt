package com.example.util.pagination.flow

import androidx.paging.PagedList
import androidx.paging.PagingData
import com.example.util.pagination.PaginationCallback
import io.reactivex.*

class PagingList<T : Any>(
    private val pagination: Flowable<PagingData<T>>,
    private val dataSourceFactory: PagingDataSourceFactory<T>
) : FlowableOnSubscribe<PagingData<T>> {


    private var pagedList: PagingData<T>? = null

    override fun subscribe(emitter: FlowableEmitter<PagingData<T>>) {
        val disposable = pagination.subscribe({
            emitter.onNext(it)
        }, {
            emitter.onError(it)
        })
        emitter.setDisposable(disposable)
    }

    fun invalidate(){
        if (dataSourceFactory.source == null) return
        dataSourceFactory.source!!.invalidateFromStart()
    }
}