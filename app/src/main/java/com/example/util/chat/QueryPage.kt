package com.example.util.chat

import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.FirestoreArray
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query


class QueryPage<T>(
        query: Query,
        parser: SnapshotParser<T>
) : FirestoreArray<T>(query, parser), ChangeEventListener {

    var eventListener: QueryPageChangeEventListener<T>? = null

    override fun onDataChanged() {
        eventListener?.onDataChanged(this)
    }

    override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {
        eventListener?.onChildChanged(this, type, snapshot, newIndex, oldIndex)
    }

    override fun onError(e: FirebaseFirestoreException) {
        eventListener?.onError(this, e)
    }

    fun getPageSnapshot(): List<DocumentSnapshot> {
        return super.getSnapshots()
    }

    fun startListening() {
        if (!isListening(this)) addChangeEventListener(this)
    }

    fun stopListening() {
        removeChangeEventListener(this)
    }
}