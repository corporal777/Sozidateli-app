package com.example.holders

import com.example.R
import com.example.data.models.EventActivityModel
import com.example.ui.subevent.items.SubEventItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_activitys.*
import kotlinx.android.synthetic.main.item_day_header.*
import java.text.SimpleDateFormat
import java.util.*

open class DayAllHeaderItem (
        val date: Long,
        val isDay: Boolean = true,
        val activities: List<EventActivityModel>,
        val canDoActions: Boolean,
        val clickListener: SubEventItem.OnSubEventClickListener
) : Item(date) {

    private val dateFormat = SimpleDateFormat("EE d.MM.yyyy", Locale.getDefault())
    private val dateFormatWithoutDay = SimpleDateFormat("d.MM.yyyy", Locale.getDefault())

    private val eventsSection = Section()
    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(eventsSection)
        }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            if (isDay) tvDate.text = dateFormat.format(date).capitalize()
            else tvDate.text = dateFormatWithoutDay.format(date).capitalize()
            recyclerView.apply {
                adapter = groupAdapter
            }
            eventsSection.update(activities.map { subEvent ->
                SubEventItem(subEvent, SubEventItem.Mode.SCHEDULE, clickListener, canDoActions)
            })
        }
    }

    override fun getLayout() = R.layout.item_day_all_header
}