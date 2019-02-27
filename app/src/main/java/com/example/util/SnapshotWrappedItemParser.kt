package com.example.util

import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.DocumentSnapshot
import com.xwray.groupie.Item

class SnapshotWrappedItemParser<T, I : Item<*>>(
        private val snapshotParser: SnapshotParser<T>,
        private val toItem: (T) -> I
) : SnapshotParser<I> {

    override fun parseSnapshot(snapshot: DocumentSnapshot): I = toItem(snapshotParser.parseSnapshot(snapshot))
}