package com.example.interfaces

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.example.ui.views.toolbar.ToolbarContent
import com.example.ui.views.toolbar.ToolbarContentActionBar
import onScrolled

//interface ToolbarFragment {
//    val title: CharSequence?
//    fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
//        toolbarContentActionBar.apply {
//            removeAllLeftViews()
//            removeAllRightViews()
//        }
//    }
//}

interface ToolbarFragment {
    val title: CharSequence
    fun actionIconContainer(view: ViewGroup)
    fun scrollValue(scroll : (value : Int) -> Unit)
    fun setupToolbarContent(toolbarContent: ToolbarContent)
}
