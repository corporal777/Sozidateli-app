package com.examle.data.source.room.converter

import androidx.room.TypeConverter
import com.examle.data.models.EventActivity
import com.examle.data.models.event.EventResponse
import com.google.gson.reflect.TypeToken

class UserEventConverter : Converter() {

    @TypeConverter
    fun eventInfoToStringJson(value: EventResponse?): String? = toJson(value, TypeToken.get(
        EventResponse::class.java).type)

    @TypeConverter
    fun jsonStringToEventInfo(json: String?): EventResponse? = fromJson(json, TypeToken.get(
        EventResponse::class.java).type)

    @TypeConverter
    fun eventActivityToJson(value: EventActivity?): String? = toJson(value, TypeToken.get(EventActivity::class.java).type)

    @TypeConverter
    fun jsonStringToEventActivity(json: String?): EventActivity? = fromJson(json, TypeToken.get(EventActivity::class.java).type)
}