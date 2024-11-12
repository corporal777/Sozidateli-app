package com.example.ui.subevent.items

import android.widget.CompoundButton
import androidx.core.view.isInvisible
import com.example.app.R
import com.example.data.models.EventActivityModel
import com.example.data.models.Tags
import com.example.app.databinding.ItemSubEventBinding
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.formatTimeIntervalFromTo
import com.example.ui.views.TagChip
import com.example.ui.views.UserSubscribeButton
import com.example.util.weak
import com.xwray.groupie.databinding.BindableItem

open class SubEventItem(
    private val subEvent: EventActivityModel,
    private val mode: Mode,
    clickListener: OnSubEventClickListener,
    private val canDoActions: Boolean = true
) : BindableItem<ItemSubEventBinding>(subEvent.id?.toLong()?: 0) {

    private val clickListener by weak(clickListener)

    override fun bind(viewBinding: ItemSubEventBinding, position: Int) {
        viewBinding.apply {
            tvTime.text = subEvent.holdingDate?.from.formatTimeIntervalFromTo(subEvent.holdingDate?.to, defaultServerDateTimeFormatter, true)
            tvStatus.text = subEvent.title

            when (mode) {
                Mode.SCHEDULE -> {
                    btnAction.apply {
                        text = (if (subEvent.binds?.userCalendar != null) context.getString(R.string.sub_event_remove_from_schedule)
                        else context.getString(R.string.sub_event_add_to_schedule))

                        //isEnabled = subEvent.isInCalendar || subEvent.canAddToCalendar

                        setOnClickListener {
                            clickListener?.apply {
                                if (subEvent.binds?.userCalendar != null) onRemoveFromScheduleClick(subEvent)
                                else onAddToScheduleClick(subEvent)
                            }
                        }
                        isInvisible = !canDoActions
                    }

                    btnSubscribe.isInvisible = true
                }
                Mode.FAVORITE -> {
                    btnSubscribe.apply {
                        btnAction.isInvisible = false
                        setAction(if (subEvent.binds?.userFavorite != null) UserSubscribeButton.Action.UNFAVORITE else UserSubscribeButton.Action.FAVORITE)
                        setOnClickListener { clickListener?.onChangeFavoriteClick(subEvent) }
                    }

                    btnAction.isInvisible = true
                }
            }

            root.setOnClickListener {
                clickListener?.onSubEventClick(subEvent)
            }

            tagGroup.apply {
                val createChip: (Tags) -> CompoundButton = {
                    TagChip(context).apply {
                        text = it.name
                        isCompactTag = true
                        isChecked = true
                        isClickable = false
                    }
                }

                removeAllViews()
                val tags = subEvent.binds?.tag ?: emptyList()
                tags.forEach {
                    if (subEvent.tag?.any { x -> x.id == it.id } == true) addView(createChip(it))
                }
            }
        }
    }

    override fun getLayout() = R.layout.item_sub_event

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is SubEventItem) return false
        if (subEvent != other.subEvent) return false
        if (mode != other.mode) return false
        return true
    }

    enum class Mode {
        SCHEDULE, FAVORITE
    }

    interface OnSubEventClickListener {
        fun onSubEventClick(subEvent: EventActivityModel)
        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveFromScheduleClick(subEvent: EventActivityModel)
        fun onChangeFavoriteClick(subEvent: EventActivityModel)
    }
}