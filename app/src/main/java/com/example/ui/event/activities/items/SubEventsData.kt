package com.example.ui.event.activities.items

import com.example.data.models.EventActivityModel
import io.reactivex.internal.operators.maybe.MaybeIsEmpty

class SubEventsData(
    val hasDate : Boolean = true,
    val date : String,
    val data : List<EventActivityModel>,
    var isEmpty: Boolean = false,
    var noParam : Boolean = false
)