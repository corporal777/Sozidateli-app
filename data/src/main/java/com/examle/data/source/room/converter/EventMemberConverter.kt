package com.examle.data.source.room.converter

import androidx.room.TypeConverter
import com.examle.data.models.event.MemberModel
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