package com.example.data.models

import android.net.Uri
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class EventFile(
        val path: Uri,
        var name: String,
        val mimeType: String?
) {
    class Deserializer : JsonDeserializer<EventFile> {
        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): EventFile {
            val obj = json.asJsonObject
            return EventFile(
                    Uri.parse(obj["uri"].asString),
                    obj["name"].asString,
                    obj["mimeType"].asString
            )
        }
    }
}