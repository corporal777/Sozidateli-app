package com.example.ui.views.toolbar

import android.widget.TextView

class ToolbarContent(
    private val toolbarTitle: TextView
) {


    fun setToolbarTitle(title: CharSequence) {
        toolbarTitle.text = title
    }

    fun getToolbarTitleView() = toolbarTitle
}