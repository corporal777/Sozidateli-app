package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.R

class CustomSwipeRefreshLayout : SwipeRefreshLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        setProgressViewOffset(
            true,
            resources.getDimensionPixelSize(R.dimen.swipe_distance_start_margin),
            resources.getDimensionPixelSize(R.dimen.swipe_distance_end_margin)
        )
    }

}