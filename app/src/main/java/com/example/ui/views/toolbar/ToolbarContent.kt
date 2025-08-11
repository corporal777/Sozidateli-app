package com.example.ui.views.toolbar

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import com.example.app.R
import com.example.extensions.setTint

class ToolbarContent(
    private val backButton : ImageView,
    private val toolbarTitle: TextView,
    private val toolbarContainer : ViewGroup
) {
    init {
        backButton.apply {
            isInvisible = false
            alpha = 1f
            setTint(R.color.black)
        }
        toolbarContainer.removeAllViews()
    }

    fun setToolbarTitle(title: CharSequence) {
        toolbarTitle.text = title
    }

    fun getToolbarTitleView() = toolbarTitle
    fun getToolbarContainer() = toolbarContainer
    fun getBackButton() = backButton
}