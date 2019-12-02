package com.example.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.R

class NoDataPlaceholderLinearLayoutManager : LinearLayoutManager {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun layoutDecoratedWithMargins(child: View, left: Int, top: Int, right: Int, bottom: Int) {
        val lp = child.layoutParams as RecyclerView.LayoutParams
        if (child.id != R.id.noDataPlaceholder || lp.viewAdapterPosition < itemCount - 1)
            return super.layoutDecoratedWithMargins(child, left, top, right, bottom)

        val parentBottom = height - paddingBottom
        val heightDifference = parentBottom - bottom
        return if (heightDifference > 0) {
            super.layoutDecoratedWithMargins(child, left, top, right, top + heightDifference)
        } else {
            super.layoutDecoratedWithMargins(child, left, top, right, bottom)
        }
    }
}