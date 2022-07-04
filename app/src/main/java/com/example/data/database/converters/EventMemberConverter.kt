package com.example.data.database.converters

import androidx.room.TypeConverter
import com.example.data.models.EventActivity
import com.example.data.models.MemberModel
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class EventMemberConverter : Converter() {

    @TypeConverter
    fun eventMemberToJson(value: List<MemberModel>?): String? {
        var type: Type = object : TypeToken<List<MemberModel>>() {}.type
        return toJson(value, type)
    }


    @TypeConverter
    fun jsonStringToEventMember(json: String?): List<MemberModel>? {
        var type: Type = object : TypeToken<List<MemberModel>>() {}.type
        return fromJson(json, type)
    }
}