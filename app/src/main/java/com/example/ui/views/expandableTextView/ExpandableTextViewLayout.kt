package com.example.ui.views.expandableTextView

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.LayoutCustomExpandableTextviewBinding
import com.example.extensions.markWon
import com.example.util.URLSpanNoUnderline

class ExpandableTextViewLayout : LinearLayout {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var isCollapsed = true
    private var isMoreVisible = true
    private var originalText: CharSequence? = null

    var onCollapsed: (collapsed: Boolean) -> Unit = {}

    private val layoutView =
        LayoutCustomExpandableTextviewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        layoutView.tvReadMore.apply {
            isVisible = isMoreVisible
            setCollapsed(isCollapsed)
            setOnClickListener {
                isCollapsed = !isCollapsed
                changeTextReadMore(isCollapsed)
            }
        }
    }


    fun setText(message: String?) {
        if (message.isNullOrEmpty()) return
        originalText = getMarkdownText(context, message)

        layoutView.tvMessage.apply {
            text = originalText
            setOnLayoutListener {
                if (it.text == originalText) {
                    layoutView.tvReadMore.isVisible = false
                }
                //isMoreVisible = it.length() < (originalText?.length ?: 0)

                //invalidate()
            }


        }
    }


    private fun setCollapsed(isCollapsed: Boolean) {
        this.isCollapsed = isCollapsed
        layoutView.tvReadMore.changeTextReadMore(isCollapsed)
    }


    private fun TextView.changeTextReadMore(isExpanded: Boolean) {
        if (!isExpanded) text = context.getString(R.string.hide_all_sessions_history)
        else text = context.getString(R.string.notifications_read_more)
    }


    private fun getMarkdownText(context: Context, text: String?): SpannableStringBuilder? {
        if (text.isNullOrEmpty()) return null
        else {
            val spanned = markWon(context).toMarkdown(text)
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