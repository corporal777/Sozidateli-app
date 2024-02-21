package com.example.ui.views.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.R
import com.example.extensions.dp
import com.example.util.getDrawable
import com.example.util.setLeftDrawableWithIntrinsicBounds
import io.github.inflationx.calligraphy3.CalligraphyUtils


class ToolbarCircleButton : AppCompatButton {


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var iconDrawable: Int = 0


    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ToolbarCircleButton)
        iconDrawable = a.getResourceId(R.styleable.ToolbarCircleButton_circleButtonDrawable, 0)
        a.recycle()

        setLeftIcon(iconDrawable)
    }


    init {
        CalligraphyUtils.applyFontToTextView(context, this, "fonts/sf_pro_text_semibold.ttf")
        setPadding(
            resources.getDimension(R.dimen.toolbar_circle_button_padding).toInt(),
            0,
            resources.getDimension(R.dimen.toolbar_circle_button_padding).toInt(),
            0
        )
        background = getDrawable(R.drawable.custom_btn_rounded_corners_active_selectable)
        stateListAnimator = null
        letterSpacing = -0.01f
        minimumHeight = 25.dp
        minHeight = 25.dp
        setTextColor(ContextCompat.getColor(context, R.color.main_brown_color_new))
        textSize = 11f
        isAllCaps = false
    }

    fun setLeftIcon(icon: Int) {
        if (icon != 0) {
            setLeftDrawableWithIntrinsicBounds(icon)
            compoundDrawablePadding = 5.dp
        }
    }


    fun setButtonMargins(top: Int, bottom: Int, left: Int, right: Int) {
        val marginParams = ViewGroup.MarginLayoutParams(layoutParams)
        marginParams.setMargins(left, top, right, bottom)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(marginParams)
        setLayoutParams(layoutParams)
    }
}