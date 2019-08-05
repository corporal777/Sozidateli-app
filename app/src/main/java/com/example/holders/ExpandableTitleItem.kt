package com.example.holders

import android.widget.TextView
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

abstract class ExpandableTitleItem(
        private val title: String
) : Item(), ExpandableItem {

    protected var isExpanded = false

    private lateinit var onToggleListener: ExpandableGroup

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            getTitleTextView(viewHolder).text = title
            setExpanded(this)
            itemView.setOnClickListener { onToggleListener.onToggleExpanded() }
        }
    }

    override fun bind(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) super.bind(holder, position, payloads)
        else holder.apply {
            (payloads[0] as? Boolean)?.let {
                isExpanded = it
                setExpanded(holder, true)
            }
        }
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.isExpanded = onToggleListener.isExpanded
        this.onToggleListener = onToggleListener
        registerGroupDataObserver(onToggleListener)
    }

    abstract fun setExpanded(viewHolder: ViewHolder, isUpdate: Boolean = false)
    abstract fun getTitleTextView(viewHolder: ViewHolder): TextView

    companion object {
        private const val EXPAND_CHANGE_ANIMATION_DURATION = 200
    }
}