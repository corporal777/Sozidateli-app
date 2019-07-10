package com.example.util.pagination

import androidx.paging.PagedList
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe

class PaginationList<T>(
        private val pagination: Observable<PagedList<T>>
) : ObservableOnSubscribe<List<T>> {

    private val paginationCallback = PaginationCallback { pagedList?.let { emitter.onNext(it.snapshot()) } }

    private lateinit var emitter: ObservableEmitter<List<T>>

    private var pagedList: PagedList<T>? = null

    override fun subscribe(emitter: ObservableEmitter<List<T>>) {
        this.emitter = emitter
        val disposable = pagination.subscribe {
            pagedList?.removeWeakCallback(paginationCallback)
            pagedList = it.apply { addWeakCallback(null, paginationCallback) }

            emitter.onNext(it.snapshot())
        }

        emitter.setDisposable(disposable)
    }

    fun onItemTake(position: Int) {
        val list = pagedList ?: return
        if (position < 0 || position >= list.size) return
        pagedList?.loadAround(position)
    }

    fun invalidate() {
        pagedList?.dataSource?.invalidate()
    }
}