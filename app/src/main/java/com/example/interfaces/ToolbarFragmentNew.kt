package com.example.interfaces

import android.graphics.drawable.Drawable
import com.example.ui.views.toolbar.ToolbarContent

interface ToolbarFragmentNew {
    val title: CharSequence
    val actionIconHidden: Boolean
    val actionIcon: Drawable?

    fun actionIconClick()
    fun toolbarTitleClick()
    fun setupToolbarContent(toolbarContent: ToolbarContent)
}