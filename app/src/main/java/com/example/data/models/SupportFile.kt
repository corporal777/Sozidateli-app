package com.example.data.models

import android.content.Context
import android.net.Uri
import com.example.util.FileUtils
import com.example.util.UriUtils

data class SupportFile(
    val type : SupportFileType,
    val uri : Uri?
){
    fun getAbsolutePath(context : Context): Pair<String, String>? {
        if (uri == null) return null

        val filePath = FileUtils.getPath(context, uri)
        val mimeType = FileUtils.getMimeType(context, uri)

        if (filePath.isEmpty()) {
            val path = UriUtils.pickedExistingPicture(context, uri).path
            val type = UriUtils.getMimeType(context, uri) ?: ""
            return Pair(path, type)
        } else return Pair(filePath, mimeType)
    }
}

enum class SupportFileType{
    IMAGE, FILE
}

