package com.example.common

import android.content.ContentResolver
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Parcelable
import android.provider.OpenableColumns
import android.util.Base64
import android.util.SparseArray
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import kotlin.properties.ReadOnlyProperty


//inline fun <reified F : Fragment> Fragment.setArgument(key: String, args: Any?): F {
//    return (this as F).apply {
//        arguments = Bundle(1).apply { putParcelable(key, args.asArgument()) }
//    }
//}

internal inline fun <reified T : Parcelable> parcelableArgument(name: String): ReadOnlyProperty<Fragment, T> {
    return object : ReadOnlyProperty<Fragment, T> {
        private var value: T? = null
        override fun getValue(thisRef: Fragment, property: kotlin.reflect.KProperty<*>): T {
            val data = BundleCompat.getParcelable(thisRef.requireArguments(), name, T::class.java)
            return value ?: requireNotNull(data) { "Arg $name is missing" }.also { value = it }
        }
    }
}


fun decodeBase64ToJson(data: String?): JSONObject? {
    if (data.isNullOrEmpty()) return null
    try {
        val base = Base64.decode(data, Base64.DEFAULT)
        return JSONObject(String(base, StandardCharsets.UTF_8))
    } catch (e: Exception) {
        e.printStackTrace()
        return JSONObject().apply {
            put("email", "null")
            put("name", "null")
            put("lastName", "null")
            put("middleName", "null")
        }
    }
}

fun Context.isConnectedToNetwork(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    return connectivityManager?.activeNetworkInfo?.isConnected ?: false
}



fun Uri.fileName(contentResolver: ContentResolver): String? {
    return contentResolver.query(this, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor ->
            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME).let { nameIndex ->
                cursor.moveToFirst()
                if (nameIndex >= 0) cursor.getString(nameIndex)
                else null
            }
        }
}

//inline fun <reified T> JsonElement?.fromJson(deserializer: JsonDeserializer<T>? = null): T? {
//    if (this == null) return null
//    return GsonBuilder()
//        .apply {
//            if (deserializer != null) registerTypeAdapter(T::class.java, deserializer)
//        }
//        .create()
//        .fromJson(this, T::class.java)
//}



fun JSONObject.getStringOrNull(name: String): String? {
    return if (has(name)) getString(name) else null
}

fun <E> SparseArray<E>.getOrPut(key: Int, put: () -> E): E {
    var value = get(key)
    if (value == null) {
        value = put()
        put(key, value)
    }

    return value
}

fun String?.parseColor(): Int? {
    if (this == null || isEmpty()) return null

    return try {
        Color.parseColor(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}




