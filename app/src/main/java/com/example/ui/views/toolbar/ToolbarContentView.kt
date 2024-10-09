package com.example.ui.views.toolbar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.example.app.R
import com.example.app.databinding.ViewToolbarContentBinding

class ToolbarContentView(context: Context) : FrameLayout(context) {

    val binding = ViewToolbarContentBinding.inflate(LayoutInflater.from(context), this, false)

    fun getTitleView(block: TextView.() -> Unit) = block(binding.tvTitle)
    fun getLeftViewContainer(block: ViewGroup.() -> Unit) = block(binding.viewsLeft)
    fun getRightViewContainer(block: ViewGroup.() -> Unit) = block(binding.viewsRight)
    fun getElevationValue() = Float
}

