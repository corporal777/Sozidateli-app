package com.example.data

import com.example.data.models.Event
import com.example.data.models.user.User

object AppData {
    lateinit var uid: String
    lateinit var user: User

    var token:String?=null
    var event: Event? = null
}