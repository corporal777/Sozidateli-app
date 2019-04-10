package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.R


class AutoPlaceholderRecyclerView : RecyclerView {

    private var placeholderViewId: Int? = null

    var placeholderView: View? = null
        set(value) {
            field = value
            checkIfEmpty()
        }

    var hideOnEmpty: Boolean = false
        set(value) {
            field = value
            checkIfEmpty()
        }

    var canShowPlaceholder: Boolean = true
        set(value) {
            field = value
            checkIfEmpty()
        }

    private val observer: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() = checkIfEmpty()
        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = checkIfEmpty()
        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) = checkIfEmpty()
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = checkIfEmpty()
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) = checkIfEmpty()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        checkAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        checkAttrs(attrs)
    }

    private fun checkAttrs(attrs: AttributeSet) {
        val t = context.obtainStyledAttributes(attrs, R.styleable.AutoPlaceholderRecyclerView)
        placeholderViewId = t.getResourceId(R.styleable.AutoPlaceholderRecyclerView_placeholder, NO_ID.toInt())
        hideOnEmpty = t.getBoolean(R.styleable.AutoPlaceholderRecyclerView_hideOnEmpty, false)
        t.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (placeholderView == null) {
            placeholderViewId?.let {
                if (it != NO_ID.toInt()) placeholderView = (parent as View).findViewById(it)
            }
        }
    }

    fun checkIfEmpty() {
        val isEmpty = adapter?.itemCount ?: 0 == 0
        placeholderView?.apply {
            visibility = if (isEmpty && canShowPlaceholder) View.VISIBLE else View.GONE
        }

        if (hideOnEmpty) {
            visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        getAdapter()?.unregisterAdapterDataObserver(observer)
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)
        checkIfEmpty()
    }

    override fun swapAdapter(adapter: Adapter<*>?, removeAndRecycleExistingViews: Boolean) {
        getAdapter()?.unregisterAdapterDataObserver(observer)
        adapter?.registerAdapterDataObserver(observer)
        super.swapAdapter(adapter, removeAndRecycleExistingViews)
        checkIfEmpty()
    }
}
