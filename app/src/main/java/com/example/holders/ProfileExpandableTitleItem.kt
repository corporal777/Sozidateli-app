package com.example.holders

import android.graphics.drawable.TransitionDrawable
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_expandable_title.*

class ProfileExpandableTitleItem(
        title: String,
        private val editClickListener: OnEditClickListener?
) : ExpandableTitleItem(title) {

    var hideDividerOnExpand = true

    override fun setExpanded(viewHolder: ViewHolder, isUpdate: Boolean) {
        viewHolder.container.apply {
            (background as TransitionDrawable).apply {
                isCrossFadeEnabled = true
                if (isUpdate) {
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
            if (isUpdate) {
                animate().rotation(toRotation).duration = EXPAND_CHANGE_ANIMATION_DURATION.toLong()
            } else {
                rotation = toRotation
            }
        }

        viewHolder.divider.apply {
            isInvisible = hideDividerOnExpand && isExpanded
        }

        viewHolder.btnEdit.apply {
            if (editClickListener != null) {
                setOnClickListener { editClickListener.invoke() }
            } else {
                setOnClickListener(null)
                isVisible = false
            }
        }
    }

    override fun getTitleTextView(viewHolder: ViewHolder): TextView = viewHolder.tvTitle

    override fun getLayout() = R.layout.item_profile_expandable_title

    companion object {
        private const val EXPAND_CHANGE_ANIMATION_DURATION = 200
    }
}

typealias OnEditClickListener = () -> Unit