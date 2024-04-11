package com.example.data.models

import android.content.ContentResolver
import android.net.Uri
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type

data class EventFile(
    val id: String,
    val path: Uri,
    var name: String,
    val mimeType: String?
) {
    class Deserializer : JsonDeserializer<EventFile> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): EventFile {
            val obj = json.asJsonObject
            return EventFile(
                obj["id"].asString,
                Uri.parse(obj["uri"].asString),
                obj["name"].asString,
                obj["mimeType"].asString
            )
        }
    }

    fun getReadBytes(contentResolver : ContentResolver, block : (body : RequestBody) -> Unit){
        contentResolver.openInputStream(path)?.buffered()
            ?.use { stream -> stream.readBytes() }?.let { bytes ->
                val body =
                    bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                block.invoke(body)
            }
    }

    fun isFilePDF() : Boolean{
        if (mimeType == "application/pdf" || mimeType?.contains("pdf", true) == true)
            return true
        else return name.contains("pdf", true)
    }
}