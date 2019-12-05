package com.example.interfaces

import com.example.ui.views.toolbar.ToolbarContentActionBar

interface ToolbarFragment {
    val title: CharSequence?
    fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        toolbarContentActionBar.apply {
            removeAllLeftViews()
            removeAllRightViews()
        }
    }
}