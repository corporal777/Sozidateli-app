package com.example.extensions

import android.content.Context
import android.os.Build
import android.text.Spannable
import android.text.Spanned
import android.text.TextUtils
import android.text.style.UnderlineSpan
import androidx.core.text.toSpannable
import com.example.BuildConfig
import com.example.util.getCurrentYear
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.noties.markwon.Markwon
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import java.util.*

fun CharSequence.substringToWholeWord(maxLength: Int = this.length): CharSequence {
    return if (maxLength in 1 until length) {
        val maxString = subSequence(0, maxLength - 1).trim()
        val spaceIndex = maxString.indexOfLast { it.isWhitespace() }
        if (spaceIndex > 0) maxString.subSequence(0, spaceIndex) else maxString
    } else {
        this
    }
}

fun CharSequence.setRequired(isRequired: Boolean): CharSequence {
    return if (isRequired) "$this*" else this
}

fun String.setUnderlineSpan(start: Int = 0, finish: Int = length): Spannable {
    return toSpannable().apply { setSpan(UnderlineSpan(), start, finish, Spanned.SPAN_INCLUSIVE_INCLUSIVE) }
}

fun String.getFileNameAndExtension(): Pair<String, String> {
    val extDotIndex = lastIndexOf(".")
    val name = if (extDotIndex == -1) this else substring(0, extDotIndex)
    val extension = substring(extDotIndex + 1, length).toLowerCase(Locale.getDefault())
    return name to extension
}

fun String.parsePhone(context: Context, defaultRegion: String = "RU"): String {
    return PhoneNumberUtil.createInstance(context).let {
        val parsed = kotlin.runCatching { it.parse(this, defaultRegion) }.getOrNull()
        if (parsed != null) it.format(parsed, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        else this
    }
}

fun String.removeAllDoubleSpaces(): String {
    val newStr = this.trim().replace("[\\s]+".toRegex(), " ")
    val sb = StringBuilder(newStr)
    val currentChar = ' '
    var counter = 0
    sb.forEach {
        if (it == currentChar) counter++
    }
    run loop@{
        sb.forEachIndexed { index, c ->
            if (currentChar == c && counter > 1) {
                sb.deleteCharAt(index)
                return@loop
            }
        }
    }
    return sb.toString()
}


fun getDeviceName(): String {
    val manufacturer: String = Build.MANUFACTURER
    val model: String = Build.MODEL
    return if (model.startsWith(manufacturer)) capitalize(model)
    else capitalize(manufacturer) + " " + model
}

fun getAppVersion(): String {
    return BuildConfig.VERSION_NAME
}

fun getAppVersionCode(): String {
    return BuildConfig.VERSION_CODE.toString()
}

private fun capitalize(str: String): String {
    if (TextUtils.isEmpty(str)) {
        return str
    }
    val arr = str.toCharArray()
    var capitalizeNext = true
    var phrase = ""
    for (c in arr) {
        if (capitalizeNext && Character.isLetter(c)) {
            phrase += Character.toUpperCase(c)
            capitalizeNext = false
            continue
        } else if (Character.isWhitespace(c)) {
            capitalizeNext = true
        }
        phrase += c
    }
    return phrase
}

fun removeFirstAndLastSpaces(str: String?): String {
    val reg = "[\\s]+$".toRegex()
    val regLast = "^[\\s]+".toRegex()
    val value = str?.replace(regLast, "")
    return value?.replace(reg, "") ?: ""
}

fun markWon(context: Context): Markwon {
    return Markwon.builder(context)
        .usePlugins(
            listOf(
                SoftBreakAddsNewLinePlugin.create(),
                LinkifyPlugin.create(),
                HtmlPlugin.create(),
                MarkwonInlineParserPlugin.create()
            )
        )
        .build();
}

fun String?.phoneToServer() = this?.replace("-", "")?.replace(" ", "")

fun String.firstLetterToUppercase(): String {
    return if (this.isNotBlank())
        this.substring(0, 1).toUpperCase() + this.substring(1).toLowerCase()
    else this
}

fun getMonthName(calendar: Calendar?): String {
    var month = ""
    val monthNames = arrayOf(
        "Январь",
        "Февраль",
        "Март",
        "Апрель",
        "Май",
        "Июнь",
        "Июль",
        "Август",
        "Сентябрь",
        "Октябрь",
        "Ноябрь",
        "Декабрь"
    )

    return if (calendar == null) ""
    else {
        month =
            if (getCurrentYear() == calendar.get(Calendar.YEAR)) monthNames[calendar.get(Calendar.MONTH)]
            else monthNames[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.YEAR)

        month
    }
}