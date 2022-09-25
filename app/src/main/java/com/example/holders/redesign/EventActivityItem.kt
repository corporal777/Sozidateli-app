package com.example.holders.redesign

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.Tag
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.formatToIntervalNew
import com.example.ui.views.TagChipNew
import com.example.ui.views.expandableTextView.CustomExpandableTextView
import com.example.util.weak
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_lecture.*


class EventActivityItem(
    val eventId: String,
    val subEvent: EventActivityModel,
    private val selectedTags: List<Tag>?,
    clickListener: OnEventActivityClickListener?,
    private val canShow: Boolean,
) : Item(subEvent.id?.toLong() ?: 0) {

    private val mClickListener by weak(clickListener)
    private var mIsCollapsed = true
    private val mTime = subEvent.holdingDate?.from
        .formatToIntervalNew(subEvent.holdingDate?.to, defaultServerDateTimeFormatter, true)
    private var fullDescription = ""

    private var canShowButton = false

    private val listener by lazy {
        object : CustomExpandableTextView.TextStateListener {
            override fun onChangeState(isCollapsed: Boolean) {
                mIsCollapsed = isCollapsed
            }
        }
    }

    init {
        canShowButton = canShow
        val today = System.currentTimeMillis()
        val subEventDate = defaultServerDateTimeFormatter.parse(subEvent.holdingDate?.to).time
        if (today > subEventDate) {
            canShowButton = false
        }
        fullDescription = StringBuilder(subEvent.description).toString()
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvLectureTime.text = mTime
            tvLectureName.text = subEvent.title

            auditoryContainer.apply {
                isVisible = !subEvent.binds?.auditorium?.name.isNullOrEmpty()
                tvLectureAuditory.text = subEvent.binds?.auditorium?.name
            }

//            val textView = TextView(viewHolder.root.context)
//            markWon(textView.context).setMarkdown(textView, fullDescription)

            var isAdded = false
            if (!isAdded) {
                isAdded = true

                val expandableTextView =
                    CustomExpandableTextView(viewHolder.root.context, listener, mIsCollapsed)
                expandableTextView.apply {
                    firstText = fullDescription
                    //originalText = SpannableStringBuilder().append(textView.text)
                    maxLines = 100
                    limitedMaxLines = 5
                    typeface =
                        Typeface.createFromAsset(context.assets, "fonts/sf_pro_display_regular.OTF")
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    expandAction = SpannableStringBuilder(context.getString(R.string.yet_btn_text))
                }
                desc_container.apply {
                    removeAllViews()
                    addView(expandableTextView, 0)
                }
            }

//            if (!fullDescription.isNullOrEmpty()) {
////                if (fullDescription.length > 225) {
////                    val shortDescription = StringBuilder(
////                        fullDescription.substring(0, 224).replace("\n", " ")
////                    ).append("...")
////                        .toString()
////
////                    if (isExpanded) {
////                        tvShowMore.isVisible = false
////                        tvLectureDesc.text = fullDescription
////                    } else {
////                        tvShowMore.isVisible = true
////                        tvLectureDesc.text = shortDescription
////                    }
////
////                    tvShowMore.setOnClickListener {
////                        tvLectureDesc.apply {
////                            isExpanded = true
////                            alpha = 0F
////                            animate().setDuration(500).alpha(1.0f)
////                            text = fullDescription
////                            tvShowMore.isVisible = false
////                        }
////                    }
////
////
////                } else {
////                    tvShowMore.isVisible = false
////                    tvLectureDesc.text = fullDescription
////                }
//
//            }


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
                mClickListener?.onSubEventClick(eventId, subEvent)
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

    override fun bind(viewHolder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull()
        if (payload == null) super.bind(viewHolder, position, payloads)
        else {
            if (payload is EventActivityModel) {
                decorActionButton(canShowButton, viewHolder.btnAddToTimetable, payload)
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