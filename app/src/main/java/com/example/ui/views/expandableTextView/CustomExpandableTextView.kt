package com.example.ui.views.expandableTextView


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.*
import android.text.Layout.Alignment.ALIGN_NORMAL
import android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.TextUtils.TruncateAt.END
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.TextViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.R
import com.example.extensions.dp
import com.example.util.ClickableSpan
import com.example.util.ClickableSpanNew
import com.example.util.markWon
import kotlin.math.abs


@SuppressLint("ViewConstructor")
class CustomExpandableTextView : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }

    @SuppressLint("Recycle")
    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)
        val originalText = a.getText(R.styleable.ExpandableTextView_originalText)
        val expandActionText = a.getText(R.styleable.ExpandableTextView_expandAction)
    }

    private val click = ClickableSpanNew(this) { toggle() }

    private var oldTextWidth = 0
    private var animator: Animator? = null
    private var expandActionSpannable = SpannableString("")
    private var expandActionStaticLayout: StaticLayout? = null
    private var collapsedDisplayedText: CharSequence? = null

    var originalText: CharSequence = ""
        set(value) {
            field = value
            updateCollapsedDisplayedText(ctaChanged = false)
            this.postInvalidate()
        }

    var expandAction: CharSequence = ""
        set(value) {
            field = value
            expandActionSpannable = SpannableString(value)
            val expandColor = ContextCompat.getColor(context, R.color.main_brown_color_new)
            expandActionSpannable.setSpan(ForegroundColorSpan(expandColor), 0, expandActionSpannable.length, SPAN_EXCLUSIVE_EXCLUSIVE)
            val font: Typeface = Typeface.createFromAsset(context.assets, "fonts/sf_pro_text_bold.ttf")
            expandActionSpannable.setSpan(CustomTypefaceSpan("", font), 0, expandActionSpannable.length, SPAN_EXCLUSIVE_EXCLUSIVE)
            val textSize = resources.getDimensionPixelSize(R.dimen.sub_event_description_show_more_text_size)
            expandActionSpannable.setSpan(AbsoluteSizeSpan(textSize), 0, expandActionSpannable.length, SPAN_EXCLUSIVE_EXCLUSIVE)
            expandActionSpannable.setSpan(click, 0, expandActionSpannable.length, SPAN_EXCLUSIVE_EXCLUSIVE)
        }

    var limitedMaxLines: Int = 3
        set(value) {
            check(maxLines == -1 || value <= maxLines) {
                """
                    maxLines ($maxLines) must be greater than or equal to limitedMaxLines ($value). 
                    maxLines can be -1 if there is no upper limit for lineCount.
                """.trimIndent()
            }
            field = value
        }



    init {
        ellipsize = END
        movementMethod = LinkMovementMethod.getInstance()
        check(maxLines == -1 || limitedMaxLines <= maxLines) {
            """
                maxLines ($maxLines) must be greater than or equal to limitedMaxLines ($limitedMaxLines).
                maxLines can be -1 if there is no upper limit for lineCount.
            """.trimIndent()
        }
        //setOnClickListener { toggle() }
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
        check(maxLines == -1 || limitedMaxLines <= maxLines) {
            """
                maxLines ($maxLines) must be greater than or equal to limitedMaxLines ($limitedMaxLines). 
                maxLines can be -1 if there is no upper limit for lineCount.
            """.trimIndent()
        }
        super.setMaxLines(maxLines)
        updateCollapsedDisplayedText(ctaChanged = false)
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun setEllipsize(where: TextUtils.TruncateAt?) {
        super.setEllipsize(END)
    }

    private fun toggle() {
        val height0 = height
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
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        text = originalText
                        val params = layoutParams
                        layoutParams.height = WRAP_CONTENT
                        layoutParams = params
                    }
                })
                start()
            }
    }

    private fun resolveDisplayedText(staticLayout: StaticLayout): CharSequence? {
        val truncatedTextWithoutCta = staticLayout.text
        if (truncatedTextWithoutCta.toString() != originalText.toString()) {
            val totalTextWidthWithoutCta =
                (0 until staticLayout.lineCount).sumOf { staticLayout.getLineWidth(it).toInt() }
            val totalTextWidthWithCta =
                totalTextWidthWithoutCta - expandActionStaticLayout!!.getLineWidth(0)
            val textWithoutCta =
                TextUtils.ellipsize(originalText, paint, totalTextWidthWithCta, END)
            val defaultEllipsisStart = textWithoutCta.indexOf(Typography.ellipsis)
            if (textWithoutCta == "") return expandActionStaticLayout!!.text
            if (defaultEllipsisStart == -1) {
                return truncatedTextWithoutCta
            }

            return SpannableStringBuilder(textWithoutCta).append(expandActionSpannable)
        } else return originalText
    }


    private fun updateCollapsedDisplayedText(
        ctaChanged: Boolean,
        textWidth: Int = measuredWidth - compoundPaddingStart - compoundPaddingEnd,
    ) {
        if (textWidth <= 0) return
        val collapsedStaticLayout = getStaticLayout(limitedMaxLines, originalText, textWidth)
        if (ctaChanged) expandActionStaticLayout =
            getStaticLayout(1, expandActionSpannable, textWidth)

        collapsedDisplayedText = resolveDisplayedText(collapsedStaticLayout)
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
                .setEllipsize(END)
                .setMaxLines(targetMaxLines)
                .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                .build()
        } else {
            StaticLayout(
                text,
                0,
                text.length,
                paint, maximumLineWidth,
                ALIGN_NORMAL,
                lineSpacingExtra,
                lineSpacingMultiplier,
                false,
                END,
                textWidth.coerceAtLeast(0)
            )
        }
    }


    interface TextStateListener {
        fun onChangeState(isCollapsed: Boolean)
    }

}