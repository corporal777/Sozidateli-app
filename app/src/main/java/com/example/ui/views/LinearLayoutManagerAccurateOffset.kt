package com.example.ui.views

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LinearLayoutManagerAccurateOffset(
    context: Context,
    //val map: MutableMap<Int, Int>
) :
    LinearLayoutManager(context) {

    private val map = mutableMapOf<Int, Int>()
    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child != null) {
                map[getPosition(child)] = child.height
            }
        }
    }

    override fun computeVerticalScrollOffset(state: RecyclerView.State): Int {
        if (childCount == 0) {
            return 0
        }
        val firstChild = getChildAt(0)
        return if (firstChild != null) {
            val firstChildPosition = getPosition(firstChild)
            var scrolledY: Int = -firstChild.y.toInt()
            for (i in 0 until firstChildPosition) {
                scrolledY += map[i] ?: 0
            }
            scrolledY
        } else 0
    }

    fun getMapOfChildes() = map
}