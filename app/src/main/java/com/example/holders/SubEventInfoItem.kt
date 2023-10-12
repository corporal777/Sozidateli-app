package com.example.holders

import android.content.Context
import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.*
import com.example.databinding.ItemSubeventInfoBinding
import com.example.extensions.*
import com.example.ui.views.TagChipNew
import com.example.util.DATE_FORMAT_SHORT_MONTH_NO_YEAR
import com.example.util.markWon
import com.xwray.groupie.databinding.BindableItem
import java.text.SimpleDateFormat
import java.util.*

class SubEventInfoItem(
    private val isApproved: Boolean,
    private val subEvent: EventActivityModel,
    private val onAddClickListener: (subEvent: EventActivityModel) -> Unit,
    private val onRemoveClickListener: (subEvent: EventActivityModel) -> Unit,
    private val onTagCLick: (id: Int) -> Unit
) : BindableItem<ItemSubeventInfoBinding>(subEvent.id?.toLong() ?: 0) {

    private var activityTime = ""

    init {
        val time = subEvent.holdingDate?.from
            .formatToIntervalNew(subEvent.holdingDate?.to, defaultServerDateTimeFormatter, true)
            ?.let {
                StringBuilder(it)
                    .append(" ")
                //.append(tvTitle.context.getString(R.string.sub_event_time_msk))
            }
        val dayAndMonth =
            subEvent.holdingDate?.from.formatToSubEventDatesInterval(subEvent.holdingDate?.to)
        activityTime = "$dayAndMonth $time"
    }

    override fun bind(viewBinding: ItemSubeventInfoBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = subEvent.title
            tvTime.text = activityTime
            tvDescription.apply {
                isVisible = !subEvent.description.isNullOrEmpty()
                markWon(context).setMarkdown(this, subEvent.description ?: "")
            }

            val location = subEvent.binds?.auditorium?.name
            if (!location.isNullOrEmpty()) {
                auditoryLn.isVisible = true
                tvLocation.text = location
            } else auditoryLn.isVisible = false

            decorActionButton(btnAddToTimetable, subEvent)

            tagsGroup.apply {
                removeAllViews()
                val listTags = subEvent.binds?.tag
                if (!listTags.isNullOrEmpty() && !subEvent.tag.isNullOrEmpty()) {
                    listTags.forEach { tag ->
                        if (subEvent.tag.any { x -> x.id == tag.id }) addView(createChip(context, tag))
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
            //if (payload is Boolean) setAction(viewHolder.btnSubscribe, payload)
            //if (payload is Boolean) setAction(viewHolder.btnAddToTimetable, payload)
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
                background =
                    ContextCompat.getDrawable(context, R.drawable.custom_btn_gray_selectable)
                setOnClickListener {
                    onRemoveClickListener(subEvent)
                }
            } else {
                text = context.getString(R.string.sub_event_add_to_schedule)
                background =
                    ContextCompat.getDrawable(context, R.drawable.custom_btn_green_selectable)
                setOnClickListener {
                    onAddClickListener(subEvent)
                }
            }

        }
    }


    private fun createChip(context: Context, tag : Tags): CompoundButton{
        return TagChipNew(context).apply {
            id = tag.id ?: 0
            text = tag.name
            isChecked = false
            isClickable = false
            setOnClickListener { onTagCLick.invoke(tag.id?:0) }
        }
    }

    private fun String?.formatToSubEventDatesInterval(finish: String?): String? {


        val start = this

        val startDate = start?.parseToDate(defaultServerDateTimeFormatter)
        val endDate = finish?.parseToDate(defaultServerDateTimeFormatter)
        val startCalendar = startDate?.calendar()
        val endCalendar = endDate?.calendar()?.takeIf { startCalendar?.isSameDay(it) != true }
        val startMonth = startCalendar?.get(Calendar.MONTH)
        val endMonth = endCalendar?.get(Calendar.MONTH)
        val startYear = startCalendar?.get(Calendar.YEAR)
        val endYear = endCalendar?.get(Calendar.YEAR)

        val startDay = startCalendar?.get(Calendar.DAY_OF_MONTH)
        val endDay = startCalendar?.get(Calendar.DAY_OF_MONTH)


        val startFormatter = if (startCalendar != null) {
            SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_NO_YEAR, Locale.getDefault())
        } else {
            null
        }

        val endFormatter = if (endCalendar != null) {
            SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_NO_YEAR, Locale.getDefault())
        } else {
            null
        }

        val startDayFormatter = if (startCalendar != null) {
            SimpleDateFormat("EEE", Locale.getDefault())
        } else {
            null
        }
        val endDayFormatter = if (endCalendar != null) {
            SimpleDateFormat("EEE", Locale.getDefault())
        } else {
            null
        }

        val dayAndMonth = StringBuilder().apply {
            if (startFormatter != null) {
                if (startFormatter != null && endFormatter != null && startYear == endYear) {
                    if (startFormatter != null && endFormatter != null && startMonth == endMonth) {
                        append(startCalendar?.get(Calendar.MONTH))
                        append(" - ")
                    } else {
                        append(
                            startFormatter.format(startDate) + " (" + startDayFormatter?.format(
                                startDate
                            ) + "),"
                        )
                        if (endFormatter != null) append(" - ")
                    }
                } else {
                    //append(startFormatter.format(startDate))
                    append(
                        startFormatter.format(startDate) + " (" + startDayFormatter?.format(
                            startDate
                        ) + "),"
                    )
                    if (endFormatter != null) append(" - ")
                }
            }
            if (endFormatter != null && startFormatter != null)
                if (startDay == endDay) {
                    if (endFormatter != null)
                        append(endFormatter.format(endDate) + " (" + endDayFormatter?.format(endDate) + "),")
                }

        }.toString()

        return dayAndMonth
    }


    override fun getLayout() = R.layout.item_subevent_info

}