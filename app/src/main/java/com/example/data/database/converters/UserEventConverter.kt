package com.example.data.database.converters

import androidx.room.TypeConverter
import com.example.data.models.EventInfo
import com.example.data.models.SubEvent
import com.google.gson.reflect.TypeToken

class UserEventConverter : Converter() {

    @TypeConverter
    fun eventInfoToStringJson(value: EventInfo?): String? = toJson(value, TypeToken.get(EventInfo::class.java).type)

    @TypeConverter
    fun jsonStringToEventInfo(json: String?): EventInfo? = fromJson(json, TypeToken.get(EventInfo::class.java).type)

    @TypeConverter
    fun subEventsToStringJson(value: List<SubEvent>?): String? = toJson(value, object : TypeToken<List<SubEvent>?>() {}.type)

    @TypeConverter
    fun jsonStringToSubEvents(json: String?): List<SubEvent>? = fromJsonList(json, object : TypeToken<List<SubEvent>?>() {}.type)
}