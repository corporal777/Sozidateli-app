package com.example.holders.redesign

import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.Tag
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.formatToIntervalNew
import com.example.ui.views.TagChipNew
import com.example.ui.views.dialogs_new.MessageDialogWithGreenButton
import com.example.util.weak
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_lecture.*
import kotlinx.android.synthetic.main.item_lecture.tagGroup
import setOnClickListener


class EventActivityItem(
    val subEvent: EventActivityModel,
    private val selectedTags: List<Tag>?,
    clickListener: OnEventActivityClickListener?,
) : Item(subEvent.id?.toLong() ?: 0) {

    private val clickListener by weak(clickListener)
    private var isExpanded = false


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {

            tvLectureTime.text = subEvent.holdingDate?.from
                .formatToIntervalNew(subEvent.holdingDate?.to, defaultServerDateTimeFormatter, true)

            val fullDescription = subEvent.description
            if (!fullDescription.isNullOrEmpty()) {
                if (fullDescription.length > 185) {
                    val shortDescription = StringBuilder(
                        fullDescription.substring(0, 180).replace("\n", " ")
                    ).append("...")
                        .toString()

                    if (isExpanded) {
                        tvShowMore.isVisible = false
                        tvLectureDesc.text = fullDescription
                    } else {
                        tvShowMore.isVisible = true
                        tvLectureDesc.text = shortDescription
                    }

                    tvShowMore.setOnClickListener {
                        tvLectureDesc.apply {
                            isExpanded = true
                            alpha = 0F
                            animate().setDuration(500).alpha(1.0f)
                            text = fullDescription
                            tvShowMore.isVisible = false
                        }
                    }


                } else {
                    tvShowMore.isVisible = false
                    tvLectureDesc.text = fullDescription
                }

            }

            tvLectureName.text = subEvent.title

            auditoryContainer.apply {
                isVisible = !subEvent.binds?.auditorium?.name.isNullOrEmpty()
                tvLectureAuditory.text = subEvent.binds?.auditorium?.name
            }

            decorActionButton(btnAddToTimetable, subEvent)

            tagGroup.apply {
                val createChip: (Tag) -> CompoundButton = {
                    TagChipNew(context).apply {
                        id = it.id.toInt()
                        text = it.name
                        isChecked = true
                        isClickable = false
                    }
                }

                removeAllViews()
                if (!selectedTags.isNullOrEmpty() && !subEvent.tag.isNullOrEmpty()) {
                    listTags.isVisible = true
                    val tags = subEvent.tag
                    tags.forEach { tag ->
                        selectedTags.forEach { selectedTag ->
                            if (tag == selectedTag.id.toInt()) {
                                addView(createChip(selectedTag), 0)
                            }
                        }
                    }
                }
            }

            cardActivity.setOnClickListener {
                clickListener?.onActivityClick(subEvent)
            }
        }
    }

    private fun decorActionButton(button: AppCompatButton, mSubEvent: EventActivityModel) {

        button.apply {
            if (mSubEvent.binds?.userCalendar != null) {
                text = context.getString(R.string.sub_event_remove_from_schedule)
                background =
                    ContextCompat.getDrawable(context, R.drawable.custom_btn_gray_selectable)
                setOnClickListener {
                    clickListener?.onRemoveFromScheduleClick(mSubEvent)
                }
            } else {
                text = context.getString(R.string.sub_event_add_to_schedule)
                background =
                    ContextCompat.getDrawable(context, R.drawable.custom_btn_green_selectable)
                setOnClickListener {
                    clickListener?.onAddToScheduleClick(mSubEvent)
                }
            }

        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is EventActivityItem) return false
        if (subEvent != other.subEvent) return false
        if (subEvent.description != other.subEvent.description) return false
        if (subEvent.mNoEvent != other.subEvent.mNoEvent) return false
        if (isExpanded != other.isExpanded) return false
        if (selectedTags != other.selectedTags) return false
        return true
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull()
        if (payload == null) super.bind(viewHolder, position, payloads)
        else {
            if (payload is EventActivityModel) {
                decorActionButton(viewHolder.btnAddToTimetable, payload)
            }
        }

    }


    override fun getLayout(): Int = R.layout.item_lecture

    interface OnEventActivityClickListener {
        fun onActivityClick(subEvent: EventActivityModel)
        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveFromScheduleClick(subEvent: EventActivityModel)
        fun onUpdateScheduleState(subEvent: EventActivityModel)
    }
}