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
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.R
import com.example.data.models.NewUserAddress
import com.example.ui.views.CustomSpannableString
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import kotlin.math.abs

class CustomExpandableTextView : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    var originalText: CharSequence? = ""
        set(value) {
            field = value
            updateCollapsedDisplayedText()
        }

    private var collapsedDisplayedText: CharSequence? = ""

    var expandAction: CharSequence = ""
    var limitedMaxLines: Int = 5
        set(value) {
            field = value
            //updateCollapsedDisplayedText()
        }


    var isTextCollapsed = true

    private var oldTextWidth = 0
    private var animator: Animator? = null

    var onLinkClickListener: OnExpandLinkListener? = null

    init {
        ellipsize = END
        BetterLinkMovementMethod.linkifyHtml(this)
            .setOnLinkClickListener { textView, url ->
                onLinkClickListener?.invoke(url)
                true
            }
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

    fun toggle() {
        if (originalText == collapsedDisplayedText) return

        val height0 = height
        text = if (isTextCollapsed) originalText else collapsedDisplayedText
        measure(
            MeasureSpec.makeMeasureSpec(width, EXACTLY),
            MeasureSpec.makeMeasureSpec(height, UNSPECIFIED)
        )
        val height1 = measuredHeight
        animator?.cancel()
        val dur = (abs(height1 - height0) * 2L).coerceAtMost(300L)
        animator = ValueAnimator.ofInt(height0, height1)
            .apply {
                interpolator = FastOutSlowInInterpolator()
                duration = dur
                addUpdateListener { value ->
                    val params = layoutParams
                    layoutParams.height = value.animatedValue as Int
                    layoutParams = params
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        text = originalText
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        text = if (isTextCollapsed) collapsedDisplayedText else originalText
                        val params = layoutParams
                        layoutParams.height = WRAP_CONTENT
                        layoutParams = params
                    }
                })
                start()
            }
    }

//    private fun collapseOriginalText(staticLayout: StaticLayout): CharSequence? {
//        if (staticLayout.text.isNullOrEmpty() || originalText.toString().isNullOrEmpty())
//            return originalText
//
//        val truncatedText = staticLayout.text
//        Log.e("SHORT TEXT", truncatedText.toString())
//        Log.e("FULL TEXT", originalText.toString())
//        if (truncatedText.toString() != originalText.toString()) return truncatedText
//        else return originalText
//    }

    private fun collapseOriginalText(staticLayout: StaticLayout): CharSequence? {
        if (staticLayout.text.isNullOrEmpty() || originalText.isNullOrEmpty())
            return originalText

        val truncatedText = staticLayout.text
        if (truncatedText.toString() != originalText.toString()) {
            var defaultEllipsisStart = truncatedText.indexOf(Typography.ellipsis)
            if (defaultEllipsisStart == -1 || defaultEllipsisStart == 0) {
                val tWidth = (0 until staticLayout.lineCount).sumOf { staticLayout.getLineWidth(it).toInt() }
                val textWithoutCta = TextUtils.ellipsize(originalText, paint, tWidth.toFloat(), TextUtils.TruncateAt.END)
                defaultEllipsisStart = textWithoutCta.indexOf(Typography.ellipsis) + "\u2026".length
            } else defaultEllipsisStart -= 6

            val collapsedText = SpannableStringBuilder(truncatedText.subSequence(0, defaultEllipsisStart))
            return collapsedText.append("\u2026")
        } else return originalText
    }

    private fun updateCollapsedDisplayedText(textWidth: Int = measuredWidth - compoundPaddingStart - compoundPaddingEnd) {
        if (textWidth <= 0 || originalText.isNullOrEmpty()) return

        val collapsedStaticLayout = getStaticLayout(limitedMaxLines, originalText!!, textWidth)
        collapsedDisplayedText = collapseOriginalText(collapsedStaticLayout)
        text = if (isTextCollapsed) collapsedDisplayedText else originalText
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

typealias OnExpandLinkListener = (link: String) -> Unit