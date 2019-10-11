package com.example.ui.views.toolbar

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.setPadding
import com.example.extensions.dp
import setSelectableItemBackgroundBorderless

@SuppressLint("ViewConstructor")
class ToolbarButton(
        context: Context,
        @DrawableRes imageRes: Int = 0
) : AppCompatImageButton(context) {

    init {
        setPadding(8.dp)
        scaleType = ScaleType.CENTER_INSIDE
        if (imageRes != 0) setImageResource(imageRes)
        setSelectableItemBackgroundBorderless()
    }
}