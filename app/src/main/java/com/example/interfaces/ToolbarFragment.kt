package com.example.interfaces

import com.example.ui.views.toolbar.ToolbarContentActionBar

interface ToolbarFragment {
    val title: String
    fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        toolbarContentActionBar.apply {
            removeAllLeftViews()
            removeAllRightViews()
        }
    }
}