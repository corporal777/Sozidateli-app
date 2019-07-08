package com.example.util.pagination

import androidx.paging.PagedList
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

class PaginationList<T>(
        private val pagination: Observable<PagedList<T>>
) : ObservableOnSubscribe<List<T>> {

    private val compositeDisposable = CompositeDisposable()
    private val paginationCallback = PaginationCallback { pagedList?.let { emitter.onNext(it.snapshot()) } }

    private lateinit var emitter: ObservableEmitter<List<T>>

    private var pagedList: PagedList<T>? = null

    override fun subscribe(emitter: ObservableEmitter<List<T>>) {
        this.emitter = emitter
        emitter.setDisposable(compositeDisposable)
        compositeDisposable += pagination.subscribe {
            pagedList?.removeWeakCallback(paginationCallback)
            pagedList = it.apply { addWeakCallback(null, paginationCallback) }

            emitter.onNext(it.snapshot())
        }
    }

    fun onItemTake(position: Int) {
        pagedList?.loadAround(position)
    }

    fun invalidate() {
        pagedList?.dataSource?.invalidate()
    }
}