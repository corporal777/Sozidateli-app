package com.example.util.pagination.flow

import androidx.paging.PagedList
import com.example.util.pagination.PaginationCallback
import io.reactivex.*

class PaginationListFlow<T>(
    private val pagination: Flowable<PagedList<T>>
) : FlowableOnSubscribe<List<T>> {

    private val paginationCallback = PaginationCallback {
        pagedList?.let {
            emitter.onNext(it.snapshot())
        }
    }

    private lateinit var emitter: FlowableEmitter<List<T>>

    private var pagedList: PagedList<T>? = null

    override fun subscribe(emitter: FlowableEmitter<List<T>>) {
        this.emitter = emitter

        val disposable = pagination.subscribe({
            pagedList?.removeWeakCallback(paginationCallback)
            pagedList = it.apply { addWeakCallback(null, paginationCallback) }

            emitter.onNext(it.snapshot())
        }, {
            emitter.onError(it)
        })

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