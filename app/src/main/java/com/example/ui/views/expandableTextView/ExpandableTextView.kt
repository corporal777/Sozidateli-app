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
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }


    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)
        val originalText = a.getText(R.styleable.ExpandableTextView_originalText)
        val expandActionText = a.getText(R.styleable.ExpandableTextView_expandAction)
        a.recycle()
    }

    var isCanExpand = true
    var onExpandClick: () -> Unit = {}


    private var oldTextWidth = 0
    private var animator: Animator? = null
    private var expandActionSpannable = SpannableString("")
    private var collapsedDisplayedText: CharSequence? = null

    var originalText: CharSequence = ""
        set(value) {
            field = value
            updateCollapsedDisplayedText(ctaChanged = false)
        }

    var expandAction: CharSequence = ""
        set(value) {
            field = value
            expandActionSpannable = CustomSpannableString(value).apply {
                setColorSpan(R.color.main_brown_color_new, context)
                setTextSizeSpan(R.dimen.sub_event_description_show_more_text_size, context)
                setFontSpan("fonts/sf_pro_text_bold.ttf", context)
                setClickSpan(this@ExpandableTextView) {
                    if (isCanExpand) toggle()
                    onExpandClick.invoke()
                }
            }
        }

    var limitedMaxLines: Int = 3
        set(value) {
            field = value
        }


    init {
        ellipsize = TextUtils.TruncateAt.END
        movementMethod = LinkMovementMethod.getInstance()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val givenWidth = MeasureSpec.getSize(widthMeasureSpec)
        val textWidth = givenWidth - compoundPaddingStart - compoundPaddingEnd
        if (textWidth == oldTextWidth || animator?.isRunning == true) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        oldTextWidth = textWidth
        updateCollapsedDisplayedText(ctaChanged = true, textWidth)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun setMaxLines(maxLines: Int) {
        super.setMaxLines(maxLines)
        updateCollapsedDisplayedText(ctaChanged = false)
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun setEllipsize(where: TextUtils.TruncateAt?) {
        super.setEllipsize(TextUtils.TruncateAt.END)
    }

    private fun toggle() {
        val height0 = height
        measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED)
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
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        text = originalText
                        val params = layoutParams
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        layoutParams = params
                    }
                })
                start()
            }
    }


    private fun collapseOriginalText(staticLayout: StaticLayout, ): CharSequence? {
        if (staticLayout.text.isNullOrEmpty() || originalText.toString().isNullOrEmpty())
            return originalText

        val truncatedText = staticLayout.text
        if (truncatedText.toString() != originalText.toString()) {
            var defaultEllipsisStart = truncatedText.indexOf(Typography.ellipsis)
            if (defaultEllipsisStart == -1 || defaultEllipsisStart == 0) {
                val tWidth = (0 until staticLayout.lineCount)
                    .sumOf { staticLayout.getLineWidth(it).toInt() }
                val textWithoutCta = TextUtils.ellipsize(originalText, paint, tWidth.toFloat(), TextUtils.TruncateAt.END)
                defaultEllipsisStart = textWithoutCta.indexOf(Typography.ellipsis) + "\u2026".length
            } else  defaultEllipsisStart -= 6



            val collapsedText =
                SpannableStringBuilder(truncatedText.subSequence(0, defaultEllipsisStart)).apply {
                    append("\u2026")
                    append(expandActionSpannable)
                }

            return collapsedText
        } else return originalText
    }

    private fun updateCollapsedDisplayedText(
        ctaChanged: Boolean,
        textWidth: Int = measuredWidth - compoundPaddingStart - compoundPaddingEnd,
    ) {
        if (textWidth <= 0) return

        val collapsedStaticLayout = getStaticLayout(limitedMaxLines, originalText, textWidth)

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
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder
                .obtain(text, 0, text.length, paint, maximumLineWidth)
                .setIncludePad(false)
                .setEllipsize(TextUtils.TruncateAt.END)
                .setMaxLines(targetMaxLines)
                .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                .build()
        } else {
            StaticLayout(
                text,
                0,
                text.length,
                paint, maximumLineWidth,
                Layout.Alignment.ALIGN_NORMAL,
                lineSpacingExtra,
                lineSpacingMultiplier,
                false,
                TextUtils.TruncateAt.END,
                textWidth.coerceAtLeast(0)
            )
        }
    }

}