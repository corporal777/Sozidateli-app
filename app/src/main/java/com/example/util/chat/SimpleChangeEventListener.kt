package com.example.util.chat

import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException

abstract class SimpleChangeEventListener : ChangeEventListener {
    override fun onDataChanged() {
    }

    override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {
    }

    override fun onError(e: FirebaseFirestoreException) {
    }
}