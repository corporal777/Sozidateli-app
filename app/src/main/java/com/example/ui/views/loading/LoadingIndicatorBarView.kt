package com.example.ui.views.loading

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.RelativeLayout

class LoadingIndicatorBarView : RelativeLayout {


    private var cornerRadius: Float = 0f

    constructor(context: Context, cornerRadius: Float) : super(context) {
        this.cornerRadius = cornerRadius
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    init {
        initViews()
    }

    fun initViews() {
        setBackground(
            ToolBox.roundedCornerRectWithColor(
                Color.WHITE,
                //Color.argb(255, 255, 255, 255),
                cornerRadius
            )
        )
        setAlpha(0.5f)
    }

    fun resetColor() {
        setBackground(
            ToolBox.roundedCornerRectWithColor(
                Color.argb(255, 255, 255, 255), cornerRadius
            )
        )
        setAlpha(0.5f)
    }
}