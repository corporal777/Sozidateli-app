package com.example.holders

import android.content.Context
import android.util.Log
import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.Tags
import com.example.databinding.ItemSubeventInfoBinding
import com.example.extensions.*
import com.example.ui.views.TagChipNew
import com.example.util.DATE_FORMAT_SHORT_MONTH_NO_YEAR
import com.example.util.getDrawable
import com.xwray.groupie.databinding.BindableItem
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class SubEventInfoItem(
    private val isApproved: Boolean,
    private val subEvent: EventActivityModel,
    private val onAddClickListener: (subEvent: EventActivityModel) -> Unit,
    private val onRemoveClickListener: (subEvent: EventActivityModel) -> Unit,
    private val onTagCLick: (id: Int) -> Unit
) : BindableItem<ItemSubeventInfoBinding>(subEvent.id?.toLong() ?: 0) {

    private val activityTime = getDayAndMonth()

    init {

    }

    override fun bind(viewBinding: ItemSubeventInfoBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = subEvent.title
            tvTime.text = activityTime
            tvDescription.apply {
                isVisible = !subEvent.description.isNullOrEmpty()
                markWon(context).setMarkdown(this, subEvent.description ?: "")
            }

            auditoryLn.apply {
                isVisible = !subEvent.binds?.auditorium?.name.isNullOrEmpty()
                tvLocation.text = subEvent.binds?.auditorium?.name
            }

            decorActionButton(btnAddToTimetable, subEvent)
            tagsGroup.apply {
                removeAllViews()
                val listTags = subEvent.binds?.tag
                if (!listTags.isNullOrEmpty() && !subEvent.tag.isNullOrEmpty()) {
                    listTags.forEach { tag ->
                        if (subEvent.tag.any { x -> x.id == tag.id }) addView(
                            createChip(
                                context,
                                tag
                            )
                        )
                    }
                }
            }
        }
    }


    override fun bind(
        viewBinding: ItemSubeventInfoBinding,
        position: Int,
        payloads: MutableList<Any>?
    ) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is EventActivityModel) {
                decorActionButton(viewBinding.btnAddToTimetable, payload)
            }
        }
    }


    private fun decorActionButton(button: AppCompatButton, subEvent: EventActivityModel) {
        button.apply {
            isVisible = isApproved
            if (subEvent.binds?.userCalendar != null) {
                text = context.getString(R.string.sub_event_remove_from_schedule)
                background = getDrawable(R.drawable.btn_background_gray)
                setOnClickListener {
                    onRemoveClickListener(subEvent)
                }
            } else {
                text = context.getString(R.string.sub_event_add_to_schedule)
                background = getDrawable(R.drawable.btn_background_green)
                setOnClickListener {
                    onAddClickListener(subEvent)
                }
            }

        }
    }


    private fun createChip(context: Context, tag: Tags): CompoundButton {
        return TagChipNew(context).apply {
            id = tag.id ?: 0
            text = tag.name
            isChecked = false
            isClickable = false
            setOnClickListener { onTagCLick.invoke(tag.id ?: 0) }
        }
    }

    private fun getDayAndMonth(): String? {
        var date: String
        val startCalendar = subEvent.holdingDate?.from?.parseToDate(defaultServerDateTimeFormatter)?.calendar()
        val endCalendar = subEvent.holdingDate?.to?.parseToDate(defaultServerDateTimeFormatter)?.calendar()

        if (startCalendar == null || endCalendar == null) return null

        if (startCalendar.isSameMonth(endCalendar)) {
            if (startCalendar.isSameDay(endCalendar)) {
                date = startCalendar.getCalendarDay(false) +
                        "." + startCalendar.getCalendarMonth(false)
                date = date + " (" + startCalendar.getCalendarDayOfWeek() + ")"
            } else {
                date = startCalendar.getCalendarDay(false) +
                        " - " + endCalendar.getCalendarDay(false) +
                        "." + endCalendar.getCalendarMonth(false)
                date = date + " (" + endCalendar.getCalendarDayOfWeek() + ")"
            }

        } else {
            date = startCalendar.getCalendarDay(false) +
                        "." + startCalendar.getCalendarMonth(false) +
                        " - " + endCalendar.getCalendarDay(false) +
                        "." + endCalendar.getCalendarMonth(false)
            date = date + " (" + endCalendar.getCalendarDayOfWeek() + ")"

        }

        val formatter = if (startCalendar.isSameYear(endCalendar)) {
            if (startCalendar.isSameDay(endCalendar)) defaultTimeFormatter
            else defaultDateTimeFormatterNoYear
        } else defaultDateFormatter

        val time = "${formatter.format(startCalendar.time)} - ${formatter.format(endCalendar.time)}"

        return "$date, $time"
    }


    override fun getLayout() = R.layout.item_subevent_info

}