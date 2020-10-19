package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.core.content.ContextCompat
import com.example.R
import uk.co.chrisjenx.calligraphy.CalligraphyUtils

class TagChip : AppCompatToggleButton {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var isCompactTag = false
        set(value) {
            field = value
            setBackground()
        }

    init {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimensionPixelSize(R.dimen.tag_text_size).toFloat())
        CalligraphyUtils.applyFontToTextView(context, this, "fonts/Roboto-Bold.ttf")
        setTextColor(ContextCompat.getColorStateList(context, R.color.text_color_tag))
        setBackground()
        stateListAnimator = null
        isAllCaps = false
        gravity = Gravity.CENTER

        minimumHeight = 0
        minimumWidth = 0
        minHeight = 0
        minWidth = 0
    }

    private fun setBackground() {
        setBackgroundResource(if (isCompactTag) R.drawable.background_tag_compact else R.drawable.background_tag)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        textOn = text
        textOff = text
    }
}