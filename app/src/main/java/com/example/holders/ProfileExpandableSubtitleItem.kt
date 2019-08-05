package com.example.holders

import android.widget.TextView
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_expandable_subtitle.*

class ProfileExpandableSubtitleItem(
        title: String
) : ExpandableTitleItem(title) {

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