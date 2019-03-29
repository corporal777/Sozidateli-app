package com.example.holders

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.models.SubEvent
import com.example.data.models.Tag
import com.example.ui.views.TagChip
import com.example.util.weak
import com.google.android.material.chip.Chip
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_sub_event.*
import java.text.SimpleDateFormat
import java.util.*

open class SubEventItem(
        private val subEvent: SubEvent,
        private val selectedTags: List<Tag>,
        clickListener: OnSubEventClickListener
) : Item() {

    private val clickListener by weak(clickListener)
    private val serverDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            val startDate = serverDateFormat.parse(subEvent.start)
            val finishDate = serverDateFormat.parse(subEvent.finish)
            val eventDatesDiapason = "${timeFormat.format(startDate)} - ${timeFormat.format(finishDate)}"
            tvTime.text = eventDatesDiapason
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
                        if (subEvent.isInCalendar) onRemoveToScheduleClick(subEvent)
                        else onAddToScheduleClick(subEvent)
                    }
                }
            }

            root.setOnClickListener { clickListener?.onSubEventClick(subEvent) }

            tagGroup.apply {
                val createChip: (Tag) -> Chip = {
                    TagChip(context).apply {
                        text = it.getTagName()
                        isCheckable = false
                        isChecked = selectedTags.contains(it)
                    }
                }

                removeAllViews()
                val categories = subEvent.categories
                val tags = subEvent.tags
                categories.forEach { addView(createChip(it)) }
                tags.forEach { addView(createChip(it)) }
            }
        }
    }

    override fun getLayout() = R.layout.item_sub_event

    interface OnSubEventClickListener {
        fun onSubEventClick(event: SubEvent)
        fun onAddToScheduleClick(event: SubEvent)
        fun onRemoveToScheduleClick(event: SubEvent)
    }
}