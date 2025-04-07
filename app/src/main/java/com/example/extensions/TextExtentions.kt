package com.example.extensions

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.util.Base64
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.example.app.BuildConfig
import com.example.app.R
import com.example.util.ClickableSpan
import com.example.util.URLSpanNoUnderline
import com.example.util.showCustomTabsBrowser
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import org.json.JSONObject
import java.nio.charset.StandardCharsets
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


fun getClickablePrivacyPolitics(context: Context): CharSequence {
    return SpannableString(context.getString(R.string.auth_user_agreement)).apply {
        setSpan(
            ClickableSpan(false) {
                showCustomTabsBrowser(context, context.getString(R.string.auth_agree_address))
            }, 52, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

fun Spanned?.removeUrlUnderline(): Spannable? {
    if (this.isNullOrEmpty()) return null
    return toSpannable().apply {
        val urls = getSpans<URLSpan>()
        urls.forEach {
            val start = getSpanStart(it)
            val end = getSpanEnd(it)
            removeSpan(it)
            set(start..end, URLSpanNoUnderline(it.url))
        }
    }
}

fun String.parseAsHtmlWithoutUnderline(): Spannable? {
    if (this.isNullOrEmpty()) return null
    val s: Spannable = Html.fromHtml(this) as Spannable
    for (u in s.getSpans(0, s.length, URLSpan::class.java)) {
        s.setSpan(object : UnderlineSpan() {
            override fun updateDrawState(tp: TextPaint) {
                tp.isUnderlineText = false
            }
        }, s.getSpanStart(u), s.getSpanEnd(u), 0)
    }
    return s
}

fun CharSequence.setRequired(isRequired: Boolean): CharSequence {
    return if (isRequired) "$this*" else this
}

fun String.setUnderlineSpan(start: Int = 0, finish: Int = length): Spannable {
    return toSpannable().apply {
        setSpan(
            UnderlineSpan(),
            start,
            finish,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
    }
}

fun String.getFileNameAndExtension(): Pair<String, String> {
    val extDotIndex = lastIndexOf(".")
    val name = if (extDotIndex == -1) this else substring(0, extDotIndex)
    val extension = substring(extDotIndex + 1, length).lowercase(Locale.getDefault())
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
//    val newStr = this.trim().replace("[\\s]+".toRegex(), " ")
//    val sb = StringBuilder(newStr)
//    val currentChar = ' '
//    var counter = 0
//    sb.forEach {
//        if (it == currentChar) counter++
//    }
//    run loop@{
//        sb.forEachIndexed { index, c ->
//            if (currentChar == c && counter > 1) {
//                sb.deleteCharAt(index)
//                return@loop
//            }
//        }
//    }
    //return sb.toString()
    return this.trim().replace("[\\s]+".toRegex(), " ")
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

fun markWon(context: Context, vararg plugin: AbstractMarkwonPlugin): Markwon {
    return Markwon.builder(context)
        .usePlugins(
            arrayListOf(
                SoftBreakAddsNewLinePlugin.create(),
                LinkifyPlugin.create(),
                HtmlPlugin.create(),
                MarkwonInlineParserPlugin.create()
            ).apply { if (!plugin.isNullOrEmpty()) addAll(plugin.toList()) }
        ).build()
}


fun String?.phoneToServer() = this?.replace("-", "")?.replace(" ", "")

fun String.firstLetterToUppercase(): String {
    return if (this.isNotBlank())
        this.substring(0, 1).uppercase() + this.substring(1).lowercase()
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

fun getSymbols(): String {
    return "\\@\\#\\$\\_\\&\\-\\+\\(\\)\\/\\*\\\"\\'\\:\\;\\!\\?\\,\\.\\~\\`\\|\\÷\\×\\^\\=\\{\\}\\%\\<\\>" +
            "\\•\\√\\π\\§\\∆\\£\\¢\\€\\¥\\°\\©\\®\\™\\✓\\[\\]"
}


class SpecialCharacterInputFilter(pattern: String) : InputFilter {
    private val regex = pattern.toRegex()

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        return if (source.toString() == "" || source.matches(regex)) {
            source
        } else {
            ""
        }
    }
}

fun SpannableString.setColorSpan(color: Int, context: Context): SpannableString {
    return this.apply {
        setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)),
            0,
            length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

fun SpannableString.setTextSizeSpan(size: Int, context: Context): SpannableString {
    return this.apply {
        setSpan(
            AbsoluteSizeSpan(context.resources.getDimensionPixelSize(size)),
            0,
            length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}
