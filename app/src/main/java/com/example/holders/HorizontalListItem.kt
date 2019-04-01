package com.example.holders

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.R
import com.example.util.PositionOffsetScrollListener
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_horizontal_list.*

open class HorizontalListItem<VH : RecyclerView.ViewHolder> : Item() {

    private var scrollPosition = 0
    private var scrollOffset = 0
    protected var recyclerView: RecyclerView? = null
        private set

    private val scrollListener = PositionOffsetScrollListener(LinearLayoutManager.HORIZONTAL) { position, offset ->
        scrollPosition = position
        scrollOffset = offset
    }

    var adapter: RecyclerView.Adapter<VH>? = null

    var backgroundColor = Color.TRANSPARENT

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.recyclerView.apply {
            recyclerView = this
            adapter = this@HorizontalListItem.adapter
            scrollToPositionWithOffset(scrollPosition, scrollOffset)
            addOnScrollListener(scrollListener)
            setBackgroundColor(backgroundColor)
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
        if (recyclerView == null) {
            scrollPosition = position
            scrollOffset = 0
        } else {
            recyclerView.smoothScrollToPosition(position)
        }
    }

    override fun unbind(holder: ViewHolder) {
        super.unbind(holder)
        recyclerView?.apply {
            removeOnScrollListener(scrollListener)
        }
        recyclerView = null
    }

    override fun getLayout() = R.layout.item_horizontal_list
}