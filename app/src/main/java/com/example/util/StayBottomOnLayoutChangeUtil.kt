package com.example.util

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class StayBottomOnLayoutChangeUtil {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager

    private var lastViewBeforeScroll: View? = null
    private var topBefore: Int? = null

    private var currentScroll: Int = 0

    private val yScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            currentScroll += dy
            if (abs(dy) != 0) findCurrentBottomView()
        }
    }

    private val layoutChangeListener = View.OnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
        if (bottom != oldBottom && oldBottom != 0) {
            val changeSize = oldBottom - bottom
            val currentY = currentScroll
            if (changeSize < currentY) {
                scrollByYWithoutScrollListener(currentY)
            } else {
                scrollByYWithoutScrollListener(changeSize)
                if (changeSize < 0) {
                    val topAfter = lastViewBeforeScroll?.top
                    val topBefore = this@StayBottomOnLayoutChangeUtil.topBefore
                    if (topBefore != null && topAfter != null) {
                        val viewScrollDistance = abs(topBefore - topAfter)
                        if (viewScrollDistance > changeSize) {
                            currentScroll -= changeSize + viewScrollDistance
                        }
                    }
                } else {
                    findCurrentBottomView()
                }
            }
        }
    }

    fun setupWithRecyclerView(view: RecyclerView) {
        if (::recyclerView.isInitialized) {
            recyclerView.removeOnScrollListener(yScrollListener)
            recyclerView.removeOnLayoutChangeListener(layoutChangeListener)
        }
        this.recyclerView = view
        this.layoutManager = view.layoutManager as LinearLayoutManager
        view.apply {
            addOnScrollListener(yScrollListener)
            addOnLayoutChangeListener(layoutChangeListener)
        }
    }

    private fun scrollByYWithoutScrollListener(y: Int) {
        recyclerView.apply {
            removeOnScrollListener(yScrollListener)
            scrollBy(0, y)
            addOnScrollListener(yScrollListener)
        }
    }

    private fun findCurrentBottomView() {
        val lastViewPositionBeforeScroll = layoutManager.findFirstVisibleItemPosition()
        lastViewBeforeScroll = lastViewPositionBeforeScroll.let { layoutManager.findViewByPosition(it) }
        topBefore = lastViewBeforeScroll?.top
    }
}