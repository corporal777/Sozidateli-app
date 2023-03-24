package com.example.ui.views.toolbar

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.example.R
import com.example.extensions.dp
import io.github.inflationx.calligraphy3.CalligraphyUtils

class ToolbarCircleButton : AppCompatButton {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        CalligraphyUtils.applyFontToTextView(context, this, "fonts/sf_pro_text_semibold.ttf")
        setPadding(15.dp, 0, 15.dp, 0)
        background = ContextCompat.getDrawable(context, R.drawable.custom_btn_add_favorites_selectable)
        stateListAnimator = null
        letterSpacing = -0.01f
        minimumHeight = 25.dp
        minHeight = 25.dp
        setTextColor(ContextCompat.getColor(context, R.color.main_brown_color_new))
        textSize = 11f
        isAllCaps = false
    }
}