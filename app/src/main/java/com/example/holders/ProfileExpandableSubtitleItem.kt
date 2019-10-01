package com.example.holders

import android.widget.TextView
import androidx.core.view.isInvisible
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_expandable_subtitle.*

class ProfileExpandableSubtitleItem(
        title: String
) : ExpandableTitleItem(title) {

    var badgeCount = 0

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        setBadge(viewHolder, badgeCount)
    }

    override fun bind(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull()
        if (payload as? Int != null) {
            setBadge(holder, payload)
        } else {
            super.bind(holder, position, payloads)
        }
    }

    private fun setBadge(viewHolder: ViewHolder, count: Int) {
        badgeCount = count
        viewHolder.tvBadge.apply {
            isInvisible = count <= 0
            text = count.toString()
        }
    }

    override fun setExpanded(viewHolder: ViewHolder, isUpdate: Boolean) {
        viewHolder.ivArrow.apply {
            val toRotation = if (isExpanded) 0f else 180f
            if (isUpdate) {
                animate().rotation(toRotation).duration = EXPAND_CHANGE_ANIMATION_DURATION.toLong()
            } else {
                rotation = toRotation
            }
        }
    }

    override fun getTitleTextView(viewHolder: ViewHolder): TextView = viewHolder.tvTitle

    override fun getLayout() = R.layout.item_profile_expandable_subtitle

    companion object {
        private const val EXPAND_CHANGE_ANIMATION_DURATION = 200
    }
}