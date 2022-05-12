package com.example.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginVerticalItemDecoration (private val marginFirst: Int, private val marginOther: Int) :
        RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            val lastItem = state.itemCount - 1
            when(parent.getChildAdapterPosition(view)){
                0 -> {
                    bottom = marginOther
                    top = marginOther
                }
                lastItem -> {
                    bottom = marginFirst
                    top = marginOther
                }
                else -> {
                    bottom = marginOther
                    top = marginOther
                }
            }
        }
    }
}