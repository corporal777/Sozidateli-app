package com.example.ui.views.toolbar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.setPadding
import com.example.extensions.dp
import setSelectableItemBackgroundBorderless

@SuppressLint("ViewConstructor")
class ToolbarButton : AppCompatImageButton {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setPadding(8.dp)
        scaleType = ScaleType.CENTER_INSIDE
        setSelectableItemBackgroundBorderless()
    }
}