package com.example.data.models

import com.example.data.models.EventActivityModel
import com.example.data.models.MemberModel

data class AboutSubEventData(
    val subEvent : EventActivityModel,
    val isApproved : Boolean,
    val members : List<MemberModel>
)