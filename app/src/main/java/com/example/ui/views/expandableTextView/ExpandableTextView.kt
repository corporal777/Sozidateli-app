package com.example.ui.views.expandableTextView

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.example.R
import com.example.ui.views.CustomSpannableString
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt




class ExpandableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    private var mOriginalText: CharSequence? = ""
    private var mCollapsedLines = 5
    private var mReadMoreText: CharSequence = "Read more"
    private var mReadLessText: CharSequence = "Read less"
    var isExpanded: Boolean = false
        private set
    private var mAnimationDuration: Int? = 0
    private var foregroundColor: Int? = 0
    private var initialText = ""

    private lateinit var collapsedVisibleText: String

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (initialText.isBlank()) {
            initialText = text.toString()
            collapsedVisibleText = collapsedVisibleText()
            //Override expand property in specific scenarios
            isExpanded = if (collapsedVisibleText.isAllTextVisible()) true else isExpanded

            setEllipsizedText(isExpanded)
        }
    }

    private fun toggleExpandableTextView() {
        if (collapsedVisibleText.isAllTextVisible()) return

        isExpanded = !isExpanded
        configureMaxLines()

        measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        setEllipsizedText(isExpanded)
    }

    private fun configureMaxLines(){
        if (mCollapsedLines < COLLAPSED_MAX_LINES){
            maxLines = if (isExpanded) COLLAPSED_MAX_LINES else mCollapsedLines + 1
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        mOriginalText = text
        super.setText(text, type)
    }

    fun setCollapsedLines(collapsedLines: Int): ExpandableTextView {
        mCollapsedLines = collapsedLines
        return this
    }

    fun setIsExpanded(isExpanded: Boolean): ExpandableTextView {
        this.isExpanded = isExpanded
        return this
    }


    //private functions
    init {
        context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView).apply {
            try {
                mCollapsedLines = getInt(R.styleable.ExpandableTextView_collapsedLines, 5)
                mAnimationDuration = getInt(R.styleable.ExpandableTextView_animDuration, 500)
                mReadMoreText = getString(R.styleable.ExpandableTextView_readMoreText) ?: "Read more"
                mReadLessText = getString(R.styleable.ExpandableTextView_readLessText) ?: "Read less"
                foregroundColor = getColor(R.styleable.ExpandableTextView_foregroundColor, Color.TRANSPARENT)
                isExpanded = getBoolean(R.styleable.ExpandableTextView_isExpanded, false)
            } finally {
                this.recycle()
            }
        }

        configureMaxLines()
        movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setEllipsizedText(isExpanded: Boolean) {
        if (initialText.isBlank()) return
        Log.e("TEXT", collapsedVisibleText)
        text = if (collapsedVisibleText.isAllTextVisible()) initialText
        else {
            if (isExpanded) getExpandText() else getCollapseText()
        }
    }

    private fun getExpandText(): SpannableStringBuilder {
        return SpannableStringBuilder(initialText)
            .append("\n")
            .append(mReadLessText.toString().span())
    }

    private fun getCollapseText(): SpannableStringBuilder {

        val ellipseTextLength = ((mReadMoreText.length + DEFAULT_ELLIPSIZED_TEXT.length) * 2.0).roundToInt()
        val textAvailableLength = max(0, collapsedVisibleText.length - ellipseTextLength)
        val ellipsizeAvailableLength = min(collapsedVisibleText.length, DEFAULT_ELLIPSIZED_TEXT.length)
        val readMoreAvailableLength = min(collapsedVisibleText.length - ellipsizeAvailableLength, mReadMoreText.length)

//        return SpannableStringBuilder(collapsedVisibleText.substring(0, textAvailableLength))
//            .append(DEFAULT_ELLIPSIZED_TEXT.substring(0, ellipsizeAvailableLength))
//            .append(mReadMoreText.toString().span())

        return SpannableStringBuilder(collapsedVisibleText)
            .append(DEFAULT_ELLIPSIZED_TEXT)
            .append(mReadMoreText.toString().span())
    }

    private fun collapsedVisibleText(): String {
        try {
            var finalTextOffset = 0
            if (mCollapsedLines < COLLAPSED_MAX_LINES) {
                for (i in 0 until mCollapsedLines) {
                    val textOffset = layout.getLineEnd(i)
                    if (textOffset == initialText.length)
                        return initialText
                    else
                        finalTextOffset = textOffset
                }
                return initialText.substring(0, finalTextOffset)
            }else {
                return initialText
            }
        }catch (e: Exception){
            e.printStackTrace()
            return initialText
        }
    }

    private fun String.isAllTextVisible(): Boolean = this == initialText

    private fun String.span(): SpannableString {
        return CustomSpannableString(this).apply {
            setColorSpan(R.color.main_brown_color_new, context)
            setTextSizeSpan(R.dimen.clickable_text_view_size, context)
            setFontSpan("fonts/sf_pro_text_bold.ttf", context)
            setClickSpan(this@ExpandableTextView){
                toggleExpandableTextView()
            }
        }
    }



    companion object {
        const val TAG = "ExpandableTextView"
        const val MAX_VALUE_ALPHA = 255
        const val MIN_VALUE_ALPHA = 0
        const val ANIMATION_PROPERTY_MAX_HEIGHT = "maxHeight"
        const val ANIMATION_PROPERTY_ALPHA = "alpha"

        const val COLLAPSED_MAX_LINES = Int.MAX_VALUE
        const val DEFAULT_ANIM_DURATION = 450
        const val READ_MORE  = "Read more"
        const val READ_LESS  = "Read less"
        const val DEFAULT_ELLIPSIZED_TEXT = "… "
        const val EMPTY_SPACE = " "
    }
}
