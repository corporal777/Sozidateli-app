package com.example.interfaces

import android.view.ViewGroup
import com.example.ui.views.toolbar.ToolbarContent


interface ToolbarFragment {
    val title: CharSequence
    fun actionIconContainer(view: ViewGroup)
    fun scrollValue(scroll: Int)
    fun setupToolbarContent(toolbarContent: ToolbarContent)
}
