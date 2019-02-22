package com.example.util.pagination

import androidx.paging.PageKeyedDataSource
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

class FireStorePaginationDataSource<T>(
        private val query: Query,
        private val parser: SnapshotParser<T>
) : PageKeyedDataSource<DocumentSnapshot, T>() {

    override fun loadInitial(params: LoadInitialParams<DocumentSnapshot>, callback: LoadInitialCallback<DocumentSnapshot, T>) {
        query.limit(params.requestedLoadSize.toLong())
                .get()
                .addOnSuccessListener {
                    val documents = it?.documents ?: listOf()
                    val data = documents.map { parser.parseSnapshot(it) }
                    val previous = documents.firstOrNull()
                    val next = documents.lastOrNull()
                    callback.onResult(data, if (previous == next) null else previous, next)
                }
    }

    override fun loadAfter(params: LoadParams<DocumentSnapshot>, callback: LoadCallback<DocumentSnapshot, T>) {
        query.limit(params.requestedLoadSize.toLong())
                .startAfter(params.key)
                .get()
                .addOnSuccessListener {
                    val documents = it?.documents ?: listOf()
                    val data = documents.map { parser.parseSnapshot(it) }
                    val next = documents.lastOrNull()
                    callback.onResult(data, next)
                }
    }

    override fun loadBefore(params: LoadParams<DocumentSnapshot>, callback: LoadCallback<DocumentSnapshot, T>) {
        query.limit(params.requestedLoadSize.toLong())
                .endBefore(params.key)
                .get()
                .addOnSuccessListener {
                    val documents = it?.documents ?: listOf()
                    val data = documents.map { parser.parseSnapshot(it) }
                    val next = documents.lastOrNull()
                    callback.onResult(data, next)
                }
    }
}