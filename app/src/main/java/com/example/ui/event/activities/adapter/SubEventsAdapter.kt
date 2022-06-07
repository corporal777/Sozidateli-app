package com.example.ui.event.activities.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.data.models.Tag
import com.example.databinding.ItemEventTimetableBinding
import com.example.databinding.ItemLectureBinding
import com.example.databinding.ItemNoActivityBinding
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.formatToIntervalNew
import com.example.extensions.parseAndFormat
import com.example.ui.event.activities.items.SubEventsData
import com.example.ui.views.TagChipNew
import com.example.util.firstLetterToUppercase
import kotlinx.android.synthetic.main.item_lecture.*
import java.text.SimpleDateFormat
import java.util.*

class SubEventsAdapter() :
    ListAdapter<SubEventsData, RecyclerView.ViewHolder>(SubEventsDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var binding : ViewBinding? = null
        return when (viewType) {
            1 -> {
                binding = ItemEventTimetableBinding.inflate(LayoutInflater.from(parent.context))
                DateViewHolder(binding)
            }
            2 -> {
                binding = ItemLectureBinding.inflate(LayoutInflater.from(parent.context))
                SubEventViewHolder(binding)
            }
            else -> {
                binding = ItemNoActivityBinding.inflate(LayoutInflater.from(parent.context))
                EmptySubEventViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        when (holder.itemViewType) {
            1 -> {
                (holder as DateViewHolder).bind(data.date)
                data.data.forEach {
                    (holder as SubEventViewHolder).bind(it)
                }
            }
            2 -> {
                data.data.forEach {
                    (holder as SubEventViewHolder).bind(it)
                }
            }
            3 -> {
                (holder as EmptySubEventViewHolder).bind(data.isEmpty, data.noParam)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        var type = 0
        when {
            getItem(position).hasDate -> {
                type = 1
            }
            !getItem(position).isEmpty -> {
               type = 2
            }
            getItem(position).isEmpty -> {
                type = 3
            }
        }
        return type
    }

    class DateViewHolder(val binding: ItemEventTimetableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
        fun bind(date: String) = with(binding) {
            tvTimetableDate.text = date.parseAndFormat(defaultServerDateFormatter, dateFormat)
                ?.firstLetterToUppercase()
        }
    }

    class SubEventViewHolder(val binding: ItemLectureBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subEvent: EventActivityModel) = with(binding) {
            var isExpanded = false

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

        }
    }

    class EmptySubEventViewHolder(val binding: ItemNoActivityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(isEmpty: Boolean, isNoParam: Boolean) = with(binding) {
            if (isEmpty) {
                title.text = binding.root.context.getString(R.string.no_activity_title)
            }
            if (isNoParam) {
                title.text = binding.root.context.getString(R.string.no_activity_with_params_title)
            }

        }
    }


}

class SubEventsDiffCallback : DiffUtil.ItemCallback<SubEventsData>() {
    override fun areItemsTheSame(
        oldItem: SubEventsData,
        newItem: SubEventsData
    ): Boolean =
        oldItem.isEmpty == newItem.isEmpty

    override fun areContentsTheSame(
        oldItem: SubEventsData,
        newItem: SubEventsData
    ): Boolean =
        oldItem.data == newItem.data


}