package com.example.util.chat

import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query

class QueryPageOptions<T>(
        val query: Query,
        val parser: SnapshotParser<T>,
        val pageSize: Int
)