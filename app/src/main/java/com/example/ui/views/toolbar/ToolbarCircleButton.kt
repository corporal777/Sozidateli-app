package com.example.ui.views.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.R
import com.example.extensions.dp
import io.github.inflationx.calligraphy3.CalligraphyUtils
import org.commonmark.internal.Bracket.image


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

    fun setButtonMargins(top : Int, bottom : Int, left : Int, right : Int){
        val marginParams = ViewGroup.MarginLayoutParams(layoutParams)
        marginParams.setMargins(left, top, right, bottom)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(marginParams)
        setLayoutParams(layoutParams)
    }
}