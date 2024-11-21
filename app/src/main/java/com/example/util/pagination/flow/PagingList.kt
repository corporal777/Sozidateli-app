package com.example.util.pagination.flow

import android.util.Log
import androidx.paging.PagedList
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import androidx.paging.rxjava2.mapAsync
import com.example.util.pagination.PaginationCallback
import io.reactivex.*
import io.reactivex.rxkotlin.subscribeBy

class PagingList<T : Any>(
    private val pagination: Flowable<PagingData<T>>,
    private val dataSourceFactory: PagingDataSourceFactory<T>
) : FlowableOnSubscribe<PagingData<T>> {


    private var pagedList: PagingData<T>? = null
    private lateinit var emitter: FlowableEmitter<PagingData<T>>

    override fun subscribe(emitter: FlowableEmitter<PagingData<T>>) {
        this.emitter = emitter
        val disposable = pagination.subscribeBy(
            onError = { emitter.onError(it) },
            onNext = { emitter.onNext(it) })
        emitter.setDisposable(disposable)
    }

    fun invalidate() {
        if (dataSourceFactory.source == null) return
        if (this::emitter.isInitialized) emitter.onNext(PagingData.empty())
        dataSourceFactory.source!!.invalidateFromStart()
    }

    fun invalidateStart(){
        if (dataSourceFactory.source == null) return
        dataSourceFactory.source!!.invalidateFromStart()
    }
}