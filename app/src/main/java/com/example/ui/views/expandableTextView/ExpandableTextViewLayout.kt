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
import android.view.ViewGroup
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
import com.example.ui.views.suggestFieldView.format.EventFormatBottomSheet
import com.example.util.URLSpanNoUnderline
import io.github.inflationx.calligraphy3.CalligraphyUtils

class ExpandableTextViewLayout : ConstraintLayout {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var isCollapsed = true
    private var collapsedHeight = 0
    private var expandedHeight = 0
    private var originalText: CharSequence? = null

    var onCollapsed: (collapsed: Boolean) -> Unit = {}

    private val layoutView =
        LayoutCustomExpandableTextviewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        layoutView.tvShowMore.apply {
            text = if (isCollapsed) context.getString(R.string.notifications_read_more)
            else context.getString(R.string.hide_all_sessions_history)

            setOnClickListener {
                layoutView.tvText.toggle()
                isCollapsed = !isCollapsed
                layoutView.tvText.isTextCollapsed = isCollapsed
            }
        }
        layoutView.tvText.apply {

        }
    }


    fun setText(text: String?) {
        layoutView.tvText.apply {
            if (text.isNullOrEmpty()) isVisible = false
            else {
                originalText = text
                limitedMaxLines = 5
            }
        }
    }

    fun setTextFont(font: String) {
        CalligraphyUtils.applyFontToTextView(context, layoutView.tvText, font)
    }

    fun setTextSize(res: Int) {
        layoutView.tvText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            context.resources.getDimensionPixelSize(res).toFloat()
        )

    }

    fun setCollapsed(isCollapsed: Boolean) {
        this.isCollapsed = isCollapsed
        layoutView.tvText.isTextCollapsed = isCollapsed
        layoutView.tvShowMore.apply {
            text = if (isCollapsed) context.getString(R.string.notifications_read_more)
            else context.getString(R.string.hide_all_sessions_history)
        }
    }

    fun getTextView() = layoutView.tvText

    fun setCollapsedCallback(block: (collapsed: Boolean) -> Unit): ExpandableTextViewLayout {
        onCollapsed = block
        return this
    }


    private fun getMarkdownText(context: Context, text: String?): SpannableStringBuilder? {
        if (text.isNullOrEmpty()) return null
        else {
            val spanned = markWon(context).toMarkdown(text ?: "")
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