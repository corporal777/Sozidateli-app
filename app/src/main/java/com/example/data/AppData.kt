package com.example.data

import com.example.data.models.Event
import com.example.data.models.User

object AppData {
    lateinit var uid: String
    lateinit var user: User

    var event: Event? = null
}