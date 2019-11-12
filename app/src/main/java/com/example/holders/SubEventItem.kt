package com.example.holders

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.models.SubEvent
import com.example.data.models.SubEventCheckLast
import com.example.data.models.Tag
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.formatToInterval
import com.example.ui.views.TagChip
import com.example.util.weak
import com.google.android.material.chip.Chip
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_sub_event.*

open class SubEventItem(
        subEventCheckLast: SubEventCheckLast,
        private val selectedTags: List<Tag>,
        clickListener: OnSubEventClickListener
) : Item(subEventCheckLast.subEvent.id.toLong()) {

    private val subEvent = subEventCheckLast.subEvent
    private val isLastInList = subEventCheckLast.isLastInList

    private val clickListener by weak(clickListener)

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvTime.text = subEvent.start.formatToInterval(subEvent.finish, defaultServerDateTimeFormatter, true)
            tvStatus.text = subEvent.title

            btnAdd.apply {
                if (subEvent.isInCalendar) {
                    setBackgroundResource(R.drawable.background_corners_border)
                    setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                    text = context.getString(R.string.sub_event_remove_from_schedule)
                    isAllCaps = false
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                } else {
                    setBackgroundResource(R.drawable.background_corners)
                    setTextColor(Color.WHITE)
                    text = context.getString(R.string.sub_event_add_to_schedule)
                    isAllCaps = true
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_plus, 0, 0, 0)
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
                        text = it.name
                        isCheckable = true
                        isChecked = selectedTags.any { selectedTag -> selectedTag.id == it.id }
                        isClickable = false
                        isEnabled = false
                    }
                }

                removeAllViews()
                val categories = subEvent.groups
                val tags = subEvent.groups.plus(subEvent.tags)
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