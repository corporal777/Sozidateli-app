package com.example.data.models

data class Subevent(
        var id:String,
        var time:String,
        var tags:List<String>,
        var isSpeaker:Boolean,
        var isInSchedule:Boolean
)