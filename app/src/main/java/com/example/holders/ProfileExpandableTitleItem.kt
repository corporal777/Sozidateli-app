package com.example.holders

import android.graphics.drawable.TransitionDrawable
import android.view.View
import com.example.R
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_expandable_title.*

class ProfileExpandableTitleItem(
        private val title: String
) : Item(), ExpandableItem {

    private var isExpanded = false

    private lateinit var onToggleListener: ExpandableGroup

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvTitle.text = title
            setExpanded(this)

            container.setOnClickListener { onToggleListener.onToggleExpanded() }
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

    private fun setExpanded(viewHolder: ViewHolder, animate: Boolean = false) {
        viewHolder.container.apply {
            (background as TransitionDrawable).apply {
                isCrossFadeEnabled = true
                if (animate) {
                    if (isExpanded) startTransition(EXPAND_CHANGE_ANIMATION_DURATION)
                    else reverseTransition(EXPAND_CHANGE_ANIMATION_DURATION)
                } else {
                    if (isExpanded) startTransition(0)
                    else resetTransition()
                }
            }
        }

        viewHolder.ivArrow.apply {
            val toRotation = if (isExpanded) 0f else 180f
            if (animate) {
                animate().rotation(toRotation).duration = EXPAND_CHANGE_ANIMATION_DURATION.toLong()
            } else {
                rotation = toRotation
            }
        }

        viewHolder.divider.apply {
            visibility = if (isExpanded) View.INVISIBLE else View.VISIBLE
        }
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.isExpanded = onToggleListener.isExpanded
        this.onToggleListener = onToggleListener
        registerGroupDataObserver(onToggleListener)
    }

    override fun getLayout() = R.layout.item_profile_expandable_title

    companion object {
        private const val EXPAND_CHANGE_ANIMATION_DURATION = 200
    }
}