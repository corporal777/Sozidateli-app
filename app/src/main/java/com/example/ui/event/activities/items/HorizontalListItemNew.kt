package com.example.ui.event.activities.items

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.R
import com.example.util.PositionOffsetScrollListener
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.socket.client.On
import kotlinx.android.synthetic.main.item_horizontal_list.*

open class HorizontalListItemNew<VH : RecyclerView.ViewHolder> : Item() {

    private var scrollPosition = 0
    private var scrollOffset = 0
    protected var recyclerView: RecyclerView? = null
        private set

    var adapter: RecyclerView.Adapter<VH>? = null

    var backgroundColor = Color.TRANSPARENT

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.recyclerView.apply {
            recyclerView = this
            adapter = this@HorizontalListItemNew.adapter
            layoutManager = CenterLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setBackgroundColor(backgroundColor)
            isNestedScrollingEnabled = false
        }
    }

    fun scrollToPositionWithOffset(position: Int, offset: Int) {
        val recyclerView = this.recyclerView
        if (recyclerView == null) {
            scrollPosition = position
            scrollOffset = offset
        } else {
            (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(scrollPosition, scrollOffset)
        }
    }

    fun smoothScrollToPosition(position: Int) {
        val recyclerView = this.recyclerView
        recyclerView?.smoothScrollToPosition(position)
    }

    override fun unbind(holder: GroupieViewHolder) {
        super.unbind(holder)
        recyclerView = null
    }

    override fun getLayout() = R.layout.item_horizontal_list_new

    inner class CenterLayoutManager : LinearLayoutManager {
        constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(
            context,
            orientation,
            reverseLayout
        )

        override fun smoothScrollToPosition(
            recyclerView: RecyclerView,
            state: RecyclerView.State,
            position: Int
        ) {
            val centerSmoothScroller = CenterSmoothScroller(recyclerView.context)
            centerSmoothScroller.targetPosition = position
            startSmoothScroll(centerSmoothScroller)
        }

        inner class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {
            override fun calculateDtToFit(
                viewStart: Int,
                viewEnd: Int,
                boxStart: Int,
                boxEnd: Int,
                snapPreference: Int
            ): Int = (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 20f / displayMetrics.densityDpi
            }
        }
    }
}