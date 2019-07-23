package com.example.ui.views.toolbar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.example.R
import kotlinx.android.synthetic.main.view_toolbar_content.view.*

class ToolbarContentView(context: Context) : FrameLayout(context) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_toolbar_content, this)
    }

    fun getTitleView(block: TextView.() -> Unit) = block(tvTitle)
    fun getLeftViewContainer(block: ViewGroup.() -> Unit) = block(viewsLeft)
    fun getRightViewContainer(block: ViewGroup.() -> Unit) = block(viewsRight)
}

