package com.example.util.paginationNew.observable

import androidx.paging.PagingData
import com.example.util.paginationNew.PagingDataSourceFactory
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.rxkotlin.subscribeBy

class PagingListObservable <T : Any>(
    private val pagination: Observable<PagingData<T>>,
    private val dataSourceFactory: PagingDataSourceFactory<T>
) : ObservableOnSubscribe<PagingData<T>> {


    private lateinit var emitter: ObservableEmitter<PagingData<T>>

    override fun subscribe(emitter: ObservableEmitter<PagingData<T>>) {
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