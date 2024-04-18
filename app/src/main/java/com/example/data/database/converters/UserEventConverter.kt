package com.example.data.database.converters

import androidx.room.TypeConverter
import com.example.data.models.EventActivity
import com.example.data.models.EventInfo
import com.example.data.models.EventNew
import com.example.data.models.SubEvent
import com.google.gson.reflect.TypeToken

class UserEventConverter : Converter() {

    @TypeConverter
    fun eventInfoToStringJson(value: EventNew?): String? = toJson(value, TypeToken.get(EventNew::class.java).type)

    @TypeConverter
    fun jsonStringToEventInfo(json: String?): EventNew? = fromJson(json, TypeToken.get(EventNew::class.java).type)

    @TypeConverter
    fun eventActivityToJson(value: EventActivity?): String? = toJson(value, TypeToken.get(EventActivity::class.java).type)

    @TypeConverter
    fun jsonStringToEventActivity(json: String?): EventActivity? = fromJson(json, TypeToken.get(EventActivity::class.java).type)
}