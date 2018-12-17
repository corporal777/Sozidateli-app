package com.example.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer

open class ViewHolder(override val containerView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(containerView),
        LayoutContainer