package com.example.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.core.content.ContextCompat
import com.example.app.R
import com.example.common.dp
import io.github.inflationx.calligraphy3.CalligraphyUtils

class TagChipNew : AppCompatToggleButton {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimensionPixelSize(R.dimen.tag_text_size_new).toFloat())
        CalligraphyUtils.applyFontToTextView(context, this, "fonts/sf_pro_text_bold.ttf")
        setBackground()
        stateListAnimator = null
        isAllCaps = false
        gravity = Gravity.CENTER
        setTextColor(ContextCompat.getColorStateList(context, R.color.text_color_tag_new))
        setPadding(10.dp, 0, 10.dp, 0)
        minimumHeight = 30.dp
        minimumWidth = 0
        minHeight = 30.dp
        minWidth = 0
    }

    @SuppressLint("ResourceAsColor")
    private fun setBackground() {
        setBackgroundResource(R.drawable.background_tag_new)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        textOn = text
        textOff = text
    }
}