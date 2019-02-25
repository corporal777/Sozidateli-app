package com.example.util.chat

import com.firebase.ui.common.ChangeEventType
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException

interface QueryPageChangeEventListener<T> {
    fun onDataChanged(queryPage: QueryPage<T>)
    fun onChildChanged(queryPage: QueryPage<T>, type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int)
    fun onError(queryPage: QueryPage<T>, e: FirebaseFirestoreException)
}

abstract class SimpleQueryPageChangeEventListener<T> : QueryPageChangeEventListener<T> {
    override fun onDataChanged(queryPage: QueryPage<T>) {}
    override fun onChildChanged(queryPage: QueryPage<T>, type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {}
    override fun onError(queryPage: QueryPage<T>, e: FirebaseFirestoreException) {}
}
