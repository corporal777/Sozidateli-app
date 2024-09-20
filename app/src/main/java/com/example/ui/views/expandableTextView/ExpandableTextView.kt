package com.example.ui.views.expandableTextView

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.R
import com.example.ui.views.CustomSpannableString
import kotlin.math.abs


class ExpandableTextView : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    private var limitedMaxLines: Int = 5
    private var oldTextWidth = 0
    private var collapsedDisplayedText: CharSequence? = null

    var originalText: CharSequence? = ""
        set(value) {
            field = value
            updateCollapsedDisplayedText()
        }


    init {
        ellipsize = TextUtils.TruncateAt.END
        movementMethod = LinkMovementMethod.getInstance()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val givenWidth = MeasureSpec.getSize(widthMeasureSpec)
        val textWidth = givenWidth - compoundPaddingStart - compoundPaddingEnd
        if (textWidth == oldTextWidth) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        oldTextWidth = textWidth
        updateCollapsedDisplayedText(textWidth)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun setMaxLines(maxLines: Int) {
        super.setMaxLines(maxLines)
        updateCollapsedDisplayedText()
    }


    override fun setEllipsize(where: TextUtils.TruncateAt?) {
        super.setEllipsize(TextUtils.TruncateAt.END)
    }


    private fun collapseOriginalText(staticLayout: StaticLayout, ): CharSequence? {
        if (staticLayout.text.isNullOrEmpty() || originalText.isNullOrEmpty())
            return originalText

        val truncatedText = staticLayout.text

        if (truncatedText.toString() != originalText.toString()) {
            var defaultEllipsisStart = truncatedText.indexOf(Typography.ellipsis)
            if (defaultEllipsisStart == -1 || defaultEllipsisStart == 0) {
                val tWidth = (0 until staticLayout.lineCount)
                    .sumOf { staticLayout.getLineWidth(it).toInt() }
                val textWithoutCta = TextUtils.ellipsize(originalText, paint, tWidth.toFloat(), TextUtils.TruncateAt.END)
                defaultEllipsisStart = textWithoutCta.indexOf(Typography.ellipsis)
            }

            val collapsedText =
                SpannableStringBuilder(truncatedText.subSequence(0, defaultEllipsisStart)).apply {
                    append("\u2026")
                }

            return collapsedText
        } else return originalText
    }

    private fun updateCollapsedDisplayedText(textWidth: Int = measuredWidth - compoundPaddingStart - compoundPaddingEnd, ) {
        if (textWidth <= 0) return
        if (originalText == null) return
        val collapsedStaticLayout = getStaticLayout(limitedMaxLines, originalText!!, textWidth)

        if (collapsedDisplayedText.isNullOrEmpty()) {
            collapsedDisplayedText = collapseOriginalText(collapsedStaticLayout)
        }
        text = collapsedDisplayedText
    }


    private fun getStaticLayout(
        targetMaxLines: Int,
        text: CharSequence,
        textWidth: Int
    ): StaticLayout {
        val maximumLineWidth = textWidth.coerceAtLeast(0)
        return StaticLayout.Builder
            .obtain(text, 0, text.length, paint, maximumLineWidth)
            .setIncludePad(false)
            .setEllipsize(TextUtils.TruncateAt.END)
            .setMaxLines(targetMaxLines)
            .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
            .build()
    }

}
