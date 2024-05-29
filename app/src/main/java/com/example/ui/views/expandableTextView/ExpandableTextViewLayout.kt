package com.example.ui.views.expandableTextView

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.URLSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.example.R
import com.example.data.models.NewEventFormat
import com.example.databinding.LayoutCustomExpandableTextviewBinding
import com.example.extensions.markWon
import com.example.ui.views.dialogs.LayoutTextView
import com.example.ui.views.suggestFieldView.format.EventFormatBottomSheet
import com.example.util.URLSpanNoUnderline
import io.github.inflationx.calligraphy3.CalligraphyUtils

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
                isMoreVisible = it.length() < (originalText?.length ?: 0)
            }
            layoutView.tvReadMore.isVisible = isMoreVisible

        }
    }


    fun setCollapsed(isCollapsed: Boolean) {
        this.isCollapsed = isCollapsed
        layoutView.tvReadMore.apply {
            changeTextReadMore(isCollapsed)
        }
    }

    fun getTextView() = layoutView.tvMessage

    private fun View.changeTextReadMore(isExpanded: Boolean) {
        (this as TextView).apply {
            if (!isExpanded) text = context.getString(R.string.hide_all_sessions_history)
            else text = context.getString(R.string.notifications_read_more)
        }
    }

    fun setCollapsedCallback(block: (collapsed: Boolean) -> Unit): ExpandableTextViewLayout {
        onCollapsed = block
        return this
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

    companion object {
        private const val MAX_LINES_COLLAPSED = 5
    }
}