package com.example.ui.views.toolbar

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.AppCompatImageButton
import com.example.R


class ToolbarBackButton(context: Context) : AppCompatImageButton(context) {

    init {
        layoutParams = ViewGroup.LayoutParams(resources.getDimensionPixelSize(R.dimen.toolbar_content_button_size), ViewGroup.LayoutParams.MATCH_PARENT)
        setImageDrawable(DrawerArrowDrawable(context).apply { progress = 1f })

        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
        setBackgroundResource(outValue.resourceId)
    }
}