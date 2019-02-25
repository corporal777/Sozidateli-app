package com.example.util.chat

import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.ObservableSnapshotArray
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query

class QueryList<T>(
        queryPageOptions: QueryPageOptions<T>
) : ObservableSnapshotArray<T>(queryPageOptions.parser), QueryPageChangeEventListener<T> {

    private val query = queryPageOptions.query
    private val pageSize = queryPageOptions.pageSize.toLong()
    private val parser = queryPageOptions.parser

    private val data = mutableListOf<T>()
    private val queryPages = mutableListOf<QueryPage<T>>()

    private var firstKey: DocumentSnapshot? = null
    private var lastKey: DocumentSnapshot? = null

    private var initialPage: QueryPage<T>? = null

    init {
        subscribeInitial()
    }

    private fun subscribeInitial() {
        initialPage = QueryPage(query.limit(1), parser).apply {
            eventListener = object : SimpleQueryPageChangeEventListener<T>() {
                override fun onDataChanged(queryPage: QueryPage<T>) {
                    val snapshots = queryPage.getPageSnapshot()
                    firstKey = snapshots.firstOrNull()?.apply {
                        initialPage?.stopListening()
                        initialPage = null
                        processQuery(query.endBefore(this), null)
                        processQuery(query.startAt(this))
                    }
                }
            }
            if (this@QueryList.isListening) startListening()
        }
    }

    private fun processQuery(query: Query, limit: Long? = pageSize) {
        val queryPage = QueryPage(if (limit == null) query else query.limit(limit), parser).apply {
            eventListener = this@QueryList
        }
        queryPages.add(queryPage)
        if (isListening) queryPage.startListening()
    }

    override fun onDataChanged(queryPage: QueryPage<T>) {
        notifyOnDataChanged()
    }

    override fun onChildChanged(queryPage: QueryPage<T>, type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {
        var itemsCountBeforePage = 0
        val pages = queryPages.toList()
        for (page in pages) {
            if (page == queryPage) break
            else itemsCountBeforePage += page.size
        }

        when (type) {
            ChangeEventType.ADDED -> data.add(itemsCountBeforePage + newIndex, parser.parseSnapshot(snapshot))
            ChangeEventType.CHANGED -> data[itemsCountBeforePage + newIndex] = parser.parseSnapshot(snapshot)
//            ChangeEventType.REMOVED -> data.removeAt(oldIndex)
//            ChangeEventType.MOVED -> swap(data, oldIndex, newIndex)
            ChangeEventType.REMOVED -> throw UnsupportedOperationException("Can not remove items")
            ChangeEventType.MOVED -> throw UnsupportedOperationException("Can not move items")
            else -> throw IllegalStateException("Incomplete when statement")
        }

        notifyOnChildChanged(
                type,
                snapshot,
                if (newIndex == -1) newIndex else itemsCountBeforePage + newIndex,
                if (oldIndex == -1) oldIndex else itemsCountBeforePage + oldIndex
        )
    }

    override fun onError(queryPage: QueryPage<T>, e: FirebaseFirestoreException) {
        notifyOnError(e)
    }

    override fun getSnapshots(): MutableList<DocumentSnapshot> {
        return mutableListOf<DocumentSnapshot>().apply {
            queryPages.forEach { addAll(it.getPageSnapshot()) }
        }
    }

    override fun addChangeEventListener(listener: ChangeEventListener): ChangeEventListener {
        super.addChangeEventListener(listener)
        initialPage?.startListening()
        queryPages.forEach { it.startListening() }
        return listener
    }

    override fun removeChangeEventListener(listener: ChangeEventListener) {
        super.removeChangeEventListener(listener)
        if (!isListening) {
            initialPage?.stopListening()
            queryPages.forEach { it.stopListening() }
        }
    }

    override val size: Int
        get() = data.size

    override fun get(index: Int): T {
        val item = data[index]
        loadAround(index)
        return item
    }

    private fun loadAround(index: Int) {
        val snapshots = snapshots
        if (index < 0 || index >= snapshots.size) return
        val item = snapshots[index]
        val pages = queryPages
        val queryPageIndex = pages.indexOfFirst { it.getPageSnapshot().contains(item) }
        if (queryPageIndex < 0) return
        val queryPage = pages[queryPageIndex]

        val pageSnapshot = queryPage.getPageSnapshot()
        if (pageSnapshot.size.toLong() < pageSize) return

        if (queryPageIndex == pages.size - 1) {
            val endKey = pageSnapshot.lastOrNull()
            if (endKey != null && lastKey != endKey) {
                lastKey = endKey
                processQuery(query.startAfter(endKey))
            }
        }
    }
}