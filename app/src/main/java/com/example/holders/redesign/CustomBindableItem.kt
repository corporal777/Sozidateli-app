package com.example.holders.redesign

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.viewbinding.ViewBinding
import com.example.app.databinding.ItemEventDetailMainBinding
import com.example.data.models.EventNew
import com.example.extensions.markWon
import com.example.util.URLSpanNoUnderline
import com.xwray.groupie.viewbinding.BindableItem

abstract class CustomBindingItem<T : ViewBinding> : BindableItem<T>{

    constructor() : super()
    constructor(id : Long) : super(id)

    override fun bind(viewBinding: T, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else bind(viewBinding, payload)
    }

    open fun bind(binding: T, payload: Any) { }

    fun getMarkdownText(message: String?, context : Context, withoutNewLine : Boolean = false): CharSequence? {
        if (message.isNullOrBlank()) return null
        else {
            val spanned = markWon(context).toMarkdown(
                if (withoutNewLine) message.replace("\n", " ")
                else message
            )
            return SpannableStringBuilder(spanned).apply {
                val urls = getSpans<URLSpan>()
                urls.forEach {
                    val start = getSpanStart(it)
                    val end = getSpanEnd(it)
                    removeSpan(it)
                    set(start..end, URLSpanNoUnderline(it.url))
                }
            }
        }
    }
}