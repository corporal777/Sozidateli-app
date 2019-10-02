package com.example.holders

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.models.SubEvent
import com.example.data.models.SubEventCheckLast
import com.example.data.models.Tag
import com.example.extensions.formatDefaultServerTimeToDefaultTimeInterval
import com.example.ui.views.TagChip
import com.example.util.weak
import com.google.android.material.chip.Chip
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_sub_event.*

open class SubEventItem(
        subEventCheckLast: SubEventCheckLast,
        private val selectedTags: List<Tag>,
        clickListener: OnSubEventClickListener
) : Item(subEventCheckLast.subEvent.id.toLong()) {

    private val subEvent = subEventCheckLast.subEvent
    private val isLastInList = subEventCheckLast.isLastInList

    private val clickListener by weak(clickListener)

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvTime.text = subEvent.start.formatDefaultServerTimeToDefaultTimeInterval(subEvent.finish)
            tvStatus.text = subEvent.title

            val visibilityIsSpeaker: Int = if (subEvent.isSpeaker) View.VISIBLE else View.GONE
            tvIsSpeaker.visibility = visibilityIsSpeaker
            ivStar.visibility = visibilityIsSpeaker

            btnAdd.apply {
                if (subEvent.isInCalendar) {
                    setBackgroundResource(R.drawable.background_corners_border)
                    setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                    text = context.getString(R.string.remove)
                    isAllCaps = false
                } else {
                    setBackgroundResource(R.drawable.background_corners)
                    setTextColor(Color.WHITE)
                    text = context.getString(R.string.sub_event_add_to_schedule)
                    isAllCaps = true
                }

                setOnClickListener {
                    clickListener?.apply {
                        if (subEvent.isInCalendar) onRemoveFromScheduleClick(subEvent)
                        else onAddToScheduleClick(subEvent)
                    }
                }
            }

            root.setOnClickListener { clickListener?.onSubEventClick(subEvent) }

            tagGroup.apply {
                val createChip: (Tag) -> Chip = {
                    TagChip(context).apply {
                        text = it.getTagName()
                        isCheckable = true
                        isChecked = selectedTags.contains(it)
                        isClickable = false
                        isEnabled = false
                    }
                }

                removeAllViews()
                val categories = subEvent.categories
                val tags = subEvent.tags
                categories.forEach { addView(createChip(it)) }
                tags.forEach { addView(createChip(it)) }
            }

            divider.visibility = if (isLastInList) View.GONE else View.VISIBLE
        }
    }

    override fun getLayout() = R.layout.item_sub_event

    interface OnSubEventClickListener {
        fun onSubEventClick(subEvent: SubEvent)
        fun onAddToScheduleClick(subEvent: SubEvent)
        fun onRemoveFromScheduleClick(subEvent: SubEvent)
    }
}