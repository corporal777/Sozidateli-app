package com.example.holders.redesign

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.view.isVisible
import com.example.app.R
import com.example.data.models.EventActivityModel
import com.example.data.models.Tag
import com.example.app.databinding.ItemLectureBinding
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.formatTimeIntervalFromTo
import com.example.extensions.markWon
import com.example.ui.views.TagChipNew
import com.example.util.URLSpanNoUnderline
import com.example.util.getDrawable
import com.example.util.weak
import com.xwray.groupie.databinding.BindableItem


class EventActivityItem(
    val eventId: String,
    val subEvent: EventActivityModel,
    private val selectedTags: List<Tag>?,
    clickListener: OnEventActivityClickListener?,
    private val canShow: Boolean,
) : BindableItem<ItemLectureBinding>(subEvent.id?.toLong() ?: 0) {

    private val onClickListener by weak(clickListener)
    private val date = subEvent.holdingDate?.from.formatTimeIntervalFromTo(subEvent.holdingDate?.to, defaultServerDateTimeFormatter, true)
    private var canShowButton = canShow
    private var isCollapsed = true


    init {
        val today = System.currentTimeMillis()
        val subEventDate = subEvent.holdingDate?.to?.let { defaultServerDateTimeFormatter.parse(it)?.time }
            ?: 0
        if (today > subEventDate) canShowButton = false
    }

    override fun bind(viewBinding: ItemLectureBinding, position: Int) {
        viewBinding.apply {
            clActivity.setOnClickListener { onClickListener?.onSubEventClick(eventId, subEvent) }
            tvLectureTime.text = date
            tvLectureName.text = subEvent.title

            auditoryContainer.apply {
                if (!subEvent.binds?.auditorium?.name.isNullOrEmpty())
                    tvLectureAuditory.text = subEvent.binds?.auditorium?.name
                else if (!subEvent.auditorium.isNullOrEmpty())
                    tvLectureAuditory.text = subEvent.auditorium
                else isVisible = false
            }


            tvLectureDesc.apply {
                isVisible = !subEvent.description.isNullOrEmpty()
                originalText = fullMarkdownText(context, subEvent.description)
                isTextCollapsed = isCollapsed
            }

            tagGroup.apply {
                removeAllViews()
                if (!selectedTags.isNullOrEmpty() && !subEvent.tag.isNullOrEmpty()) {
                    listTags.isVisible = true
                    selectedTags.forEach { tag ->
                        if (subEvent.tag.any { x -> x.id == tag.id.toInt() })
                            addView(createChip(tag), 0)
                    }
                }
            }

            decorActionButton(canShowButton, btnAddToTimetable, subEvent)
        }
    }


    private fun decorActionButton(show: Boolean, button: AppCompatButton, event: EventActivityModel) {
        button.apply {
            isVisible = show
            if (event.binds?.userCalendar != null) {
                text = context.getString(R.string.sub_event_remove_from_schedule)
                background = getDrawable(R.drawable.btn_background_gray)
                setOnClickListener { onClickListener?.onRemoveFromScheduleClick(event) }
            } else {
                text = context.getString(R.string.sub_event_add_to_schedule)
                background = getDrawable(R.drawable.btn_background_green)
                setOnClickListener { onClickListener?.onAddToScheduleClick(event) }
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is EventActivityItem) return false
        if (subEvent != other.subEvent) return false
        if (subEvent.description != other.subEvent.description) return false
        if (subEvent.mNoEvent != other.subEvent.mNoEvent) return false
        if (selectedTags != other.selectedTags) return false
        return true
    }

    override fun bind(viewBinding: ItemLectureBinding, position: Int, payloads: MutableList<Any>?) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is EventActivityModel) {
                decorActionButton(canShowButton, viewBinding.btnAddToTimetable, payload)
            }
        }
    }

    private fun fullMarkdownText(context: Context, message: String?): CharSequence? {
        if (message.isNullOrBlank()) return null
        else {
            val spanned = markWon(context).toMarkdown(message)
            return SpannableStringBuilder(spanned).apply {
                val urls = getSpans<URLSpan>()
                urls.forEach {
                    val start = getSpanStart(it)
                    val end = getSpanEnd(it)
                    removeSpan(it)
                    set(start..end, URLSpanNoUnderline(it.url))
                }
            }
        }
    }

    private fun View.createChip(tag :Tag) : CompoundButton {
        return TagChipNew(context).apply {
            id = tag.id.toInt()
            text = tag.name
            isChecked = true
            isClickable = false
        }
    }


    override fun getLayout(): Int = R.layout.item_lecture

    interface OnEventActivityClickListener {
        fun onSubEventClick(eventId: String, subEvent: EventActivityModel)
        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveFromScheduleClick(subEvent: EventActivityModel)
    }
}