package com.example.ui.views.toolbar

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import com.example.R
import setSelectableItemBackgroundBorderless

class ToolbarBackButton(context: Context) : AppCompatImageButton(context) {

    init {
        layoutParams = ViewGroup.LayoutParams(resources.getDimensionPixelSize(R.dimen.toolbar_content_button_size), ViewGroup.LayoutParams.MATCH_PARENT)
        setImageResource(R.drawable.ic_back_arrow)
        setSelectableItemBackgroundBorderless()
    }
}