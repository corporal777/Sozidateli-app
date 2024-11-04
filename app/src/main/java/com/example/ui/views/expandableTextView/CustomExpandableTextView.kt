package com.example.ui.views.expandableTextView

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.text.*
import android.text.Layout.Alignment.ALIGN_NORMAL
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.TextUtils.TruncateAt.END
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewTreeObserver
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.app.R
import com.example.data.models.NewUserAddress
import com.example.extensions.checkIsEllipsized
import com.example.ui.views.CustomSpannableString
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import kotlin.math.abs

class CustomExpandableTextView : CustomTextViewWithUrls {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    var originalText: CharSequence? = ""
        set(value) {
            field = value
            updateCollapsedDisplayedText()
        }

    private var collapsedDisplayedText: CharSequence? = ""


    var isTextCollapsed = true
    var limitedMaxLines: Int = 5

    private var oldTextWidth = 0
    private var animator: Animator? = null

    init {
        ellipsize = END
        movementMethod = LocalLinkMovementMethod.getInstance()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val givenWidth = MeasureSpec.getSize(widthMeasureSpec)
        val textWidth = givenWidth - compoundPaddingStart - compoundPaddingEnd
        if (textWidth == oldTextWidth || animator?.isRunning == true) {
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

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun setEllipsize(where: TextUtils.TruncateAt?) {
        super.setEllipsize(END)
    }

    private fun toggle() {
        if (originalText == collapsedDisplayedText) return
        text = getExpandableText()
    }


    private fun collapseOriginalText(staticLayout: StaticLayout): CharSequence? {
        if (staticLayout.text.isNullOrEmpty() || originalText.isNullOrEmpty())
            return originalText

        val truncatedText = staticLayout.text
        if (truncatedText.toString() != originalText.toString()) {
            var defaultEllipsisStart = truncatedText.indexOf(Typography.ellipsis)
            if (defaultEllipsisStart == -1 || defaultEllipsisStart == 0) {
                val tWidth =
                    (0 until staticLayout.lineCount).sumOf { staticLayout.getLineWidth(it).toInt() }
                val textWithoutCta = TextUtils.ellipsize(
                    originalText,
                    paint,
                    tWidth.toFloat(),
                    TextUtils.TruncateAt.END
                )
                defaultEllipsisStart = textWithoutCta.indexOf(Typography.ellipsis) + "\u2026".length
            } else defaultEllipsisStart -= 6

            val collapsedText =
                SpannableStringBuilder(truncatedText.subSequence(0, defaultEllipsisStart))
            return collapsedText.append("\u2026")
        } else {
            return originalText
        }
    }

    private fun updateCollapsedDisplayedText(textWidth: Int = measuredWidth - compoundPaddingStart - compoundPaddingEnd) {
        if (textWidth <= 0 || originalText.isNullOrEmpty()) return

        val collapsedStaticLayout = getStaticLayout(limitedMaxLines, originalText!!, textWidth)
        collapsedDisplayedText = collapseOriginalText(collapsedStaticLayout)
        text = getExpandableText()

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

    private fun isAllTextVisible() = collapsedDisplayedText == originalText

    private fun getExpandableText(): CharSequence {
        return SpannableStringBuilder(if (isTextCollapsed) collapsedDisplayedText else originalText).apply {
            if (!isAllTextVisible()) {
                append("\n")
                append(CustomSpannableString(
                    if (isTextCollapsed) context.getString(R.string.notifications_read_more)
                    else context.getString(R.string.hide_all_sessions_history)
                ).apply {
                    setColorSpan(R.color.main_brown_color_new, context)
                    setTextSizeSpan(R.dimen.clickable_text_view_size, context)
                    setFontSpan("fonts/sf_pro_text_bold.ttf", context)
                    setClickSpan(this@CustomExpandableTextView) {
                        isTextCollapsed = !isTextCollapsed
                        toggle()
                    }
                })
            }
        }
    }

}

typealias OnExpandLinkListener = (link: String) -> Unit