package com.example.ui.views.toolbar

import android.view.ViewGroup
import android.widget.TextView

class ToolbarContent(
    private val toolbarTitle: TextView,
    private val toolbarContainer : ViewGroup
) {
    init {
        toolbarContainer.removeAllViews()
    }

    fun setToolbarTitle(title: CharSequence) {
        toolbarTitle.text = title
    }

    fun getToolbarTitleView() = toolbarTitle
    fun getToolbarContainer() = toolbarContainer
}