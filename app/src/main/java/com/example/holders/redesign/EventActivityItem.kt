package com.example.holders.redesign

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import android.util.Log
import android.util.TypedValue
import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.Tag
import com.example.databinding.ItemLectureBinding
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.formatToIntervalNew
import com.example.ui.views.TagChipNew
import com.example.ui.views.expandableTextView.CustomExpandableTextView
import com.example.util.URLSpanNoUnderline
import com.example.util.markWon
import com.example.util.weak
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_lecture.*


class EventActivityItem(
    val eventId: String,
    val subEvent: EventActivityModel,
    private val selectedTags: List<Tag>?,
    clickListener: OnEventActivityClickListener?,
    private val canShow: Boolean,
) : BindableItem<ItemLectureBinding>(subEvent.id?.toLong() ?: 0) {

    private val mClickListener by weak(clickListener)
    private val mTime = subEvent.holdingDate?.from.formatToIntervalNew(
        subEvent.holdingDate?.to,
        defaultServerDateTimeFormatter,
        true
    )

    private var canShowButton = false

    init {
        canShowButton = canShow
        val today = System.currentTimeMillis()
        val subEventDate = defaultServerDateTimeFormatter.parse(subEvent.holdingDate?.to).time
        if (today > subEventDate) {
            canShowButton = false
        }
    }

    override fun bind(viewBinding: ItemLectureBinding, position: Int) {
        viewBinding.apply {
            cardActivity.setOnClickListener { mClickListener?.onSubEventClick(eventId, subEvent) }
            tvLectureTime.text = mTime
            tvLectureName.text = subEvent.title

            auditoryContainer.apply {
                if (!subEvent.binds?.auditorium?.name.isNullOrEmpty())
                    tvLectureAuditory.text = subEvent.binds?.auditorium?.name
                else if (!subEvent.auditorium.isNullOrEmpty())
                    tvLectureAuditory.text = subEvent.auditorium
                else isVisible = false
            }

            tvLectureDesc.apply {
                originalText = getMarkdownFormattedText(root.context, subEvent.description)
                limitedMaxLines = 5
                expandAction = SpannableStringBuilder(context.getString(R.string.yet_btn_text))
            }

            decorActionButton(canShowButton, btnAddToTimetable, subEvent)

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
                    selectedTags.forEach { tag ->
                        if (tags.any { x -> x.id == tag.id.toInt() })
                            addView(createChip(tag), 0)
                    }
                }
            }
        }
    }


    private fun decorActionButton(
        canShow: Boolean,
        button: AppCompatButton,
        mSubEvent: EventActivityModel
    ) {
        button.apply {
            isVisible = canShow
            if (mSubEvent.binds?.userCalendar != null) {
                text = context.getString(R.string.sub_event_remove_from_schedule)
                background =
                    ContextCompat.getDrawable(context, R.drawable.custom_btn_gray_selectable)
                setOnClickListener {
                    mClickListener?.onRemoveFromScheduleClick(mSubEvent)
                }
            } else {
                text = context.getString(R.string.sub_event_add_to_schedule)
                background =
                    ContextCompat.getDrawable(context, R.drawable.custom_btn_green_selectable)
                setOnClickListener {
                    mClickListener?.onAddToScheduleClick(mSubEvent)
                }
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


    private fun getMarkdownFormattedText(
        context: Context,
        description: String?
    ): SpannableStringBuilder {
        val spanned = markWon(context).toMarkdown(description ?: "")
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

    override fun getLayout(): Int = R.layout.item_lecture

    interface OnEventActivityClickListener {
        fun onSubEventClick(eventId: String, subEvent: EventActivityModel)
        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveFromScheduleClick(subEvent: EventActivityModel)
    }
}