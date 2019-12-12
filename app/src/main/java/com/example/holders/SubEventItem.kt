package com.example.holders

import android.widget.CompoundButton
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.SubEvent
import com.example.data.models.Tag
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.formatToInterval
import com.example.ui.views.TagChip
import com.example.ui.views.UserSubscribeButton
import com.example.util.weak
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_sub_event.*

open class SubEventItem(
        private val subEvent: SubEvent,
        private val mode: Mode,
        clickListener: OnSubEventClickListener
) : Item(subEvent.id.toLong()) {

    private val clickListener by weak(clickListener)

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvTime.text = subEvent.start.formatToInterval(subEvent.finish, defaultServerDateTimeFormatter, true)
            tvStatus.text = subEvent.title

            when (mode) {
                Mode.SCHEDULE -> {
                    btnAction.apply {
                        text = (if (subEvent.isInCalendar) context.getString(R.string.sub_event_remove_from_schedule)
                        else context.getString(R.string.sub_event_add_to_schedule))

                        isEnabled = subEvent.isInCalendar || subEvent.canAddToCalendar

                        setOnClickListener {
                            clickListener?.apply {
                                if (subEvent.isInCalendar) onRemoveFromScheduleClick(subEvent)
                                else onAddToScheduleClick(subEvent)
                            }
                        }
                        isInvisible = false
                    }

                    btnSubscribe.isInvisible = true
                }
                Mode.FAVORITE -> {
                    btnSubscribe.apply {
                        btnAction.isInvisible = false
                        setAction(if (subEvent.isInFavorites) UserSubscribeButton.Action.UNFAVORITE else UserSubscribeButton.Action.FAVORITE)
                        setOnClickListener { clickListener?.onChangeFavoriteClick(subEvent) }
                    }

                    btnAction.isInvisible = true
                }
            }

            root.setOnClickListener { clickListener?.onSubEventClick(subEvent) }

            tagGroup.apply {
                val createChip: (Tag) -> CompoundButton = {
                    TagChip(context).apply {
                        text = it.name
                        isCompactTag = true
                        isChecked = true
                        isClickable = false
                    }
                }

                removeAllViews()
                val categories = subEvent.groups ?: emptyList()
                val tags = subEvent.tags ?: emptyList()
                categories.forEach { addView(createChip(it)) }
                tags.forEach { addView(createChip(it)) }
            }
        }
    }

    override fun getLayout() = R.layout.item_sub_event

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is SubEventItem) return false
        if (subEvent != other.subEvent) return false
        if (mode != other.mode) return false
        return true
    }

    enum class Mode {
        SCHEDULE, FAVORITE
    }

    interface OnSubEventClickListener {
        fun onSubEventClick(subEvent: SubEvent)
        fun onAddToScheduleClick(subEvent: SubEvent)
        fun onRemoveFromScheduleClick(subEvent: SubEvent)
        fun onChangeFavoriteClick(subEvent: SubEvent)
    }
}