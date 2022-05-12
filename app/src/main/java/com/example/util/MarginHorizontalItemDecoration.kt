package com.example.util

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginHorizontalItemDecoration(private val marginFirst: Int, private val marginOther: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            val lastItem = state.itemCount - 1
            when(parent.getChildAdapterPosition(view)){
                0 -> {
                    left = marginFirst
                    right = marginOther
                }
                lastItem -> {
                    right = marginFirst
                    left = marginOther
                }
                else -> {
                    right = marginOther
                    left = marginOther
                }
            }
        }
    }
}