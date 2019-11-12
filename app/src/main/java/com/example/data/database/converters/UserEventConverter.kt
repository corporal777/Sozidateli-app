package com.example.data.database.converters

import androidx.room.TypeConverter
import com.example.data.models.EventActivity
import com.example.data.models.EventInfo
import com.example.data.models.SubEvent
import com.google.gson.reflect.TypeToken

class UserEventConverter : Converter() {

    @TypeConverter
    fun eventInfoToStringJson(value: EventInfo?): String? = toJson(value, TypeToken.get(EventInfo::class.java).type)

    @TypeConverter
    fun jsonStringToEventInfo(json: String?): EventInfo? = fromJson(json, TypeToken.get(EventInfo::class.java).type)

    @TypeConverter
    fun eventActivityToJson(value: EventActivity?): String? = toJson(value, TypeToken.get(EventActivity::class.java).type)

    @TypeConverter
    fun jsonStringToEventActivity(json: String?): EventActivity? = fromJson(json, TypeToken.get(EventActivity::class.java).type)
}