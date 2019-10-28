package com.example.data.models

import android.net.Uri
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class EventFile(
        val path: Uri,
        var name: String,
        val extension: String?
) {
    class Deserializer : JsonDeserializer<EventFile> {
        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): EventFile {
            val obj = json.asJsonObject
            return EventFile(
                    Uri.parse(obj["url"].asString),
                    obj["name"].asString,
                    obj["ext"].asString
            )
        }
    }
}