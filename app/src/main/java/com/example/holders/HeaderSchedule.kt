package com.example.holders

import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import com.example.R
import com.example.data.models.Subevent
import com.example.ui.mySchedule.MySchedulePresenter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_header_schedule.view.*
import java.util.*

open class HeaderSchedule(private val tags: List<String>?, private val dateStart: Long, private val dateEnd: Long, private val presenter: MySchedulePresenter) : Item() {

    private var groupAdapterTags = GroupAdapter<ViewHolder>()
    private var selectedTags = ArrayList<String>()

    private val groupAdapterDays = GroupAdapter<ViewHolder>()
    private var selectedPosition: Int = 0

    private var saveInstanceStateTags: Parcelable? = null
    private var saveInstanceStateDays: Parcelable? = null


    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {

            if (saveInstanceStateTags != null || saveInstanceStateDays != null) {
                recyclerViewTags.layoutManager?.onRestoreInstanceState(saveInstanceStateTags)
                recyclerViewDays.layoutManager?.onRestoreInstanceState(saveInstanceStateDays)
                return
            }

            tags?.let {
                groupAdapterTags.addAll(tags.map {
                    TagItem(it, true, selectedTags.contains(it)) { tag, isSelected ->
                        if (isSelected) {
                            selectedTags.add(tag)
                        } else {
                            selectedTags.remove(tag)
                        }
                        presenter.changeSelectedTags(selectedTags)
                    }
                })

                recyclerViewTags.apply {
                    adapter = groupAdapterTags
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                }
            }

            val daysMap = getMapDate(dateStart, dateEnd)
            groupAdapterDays.update(
                    daysMap.map {
                        DayItem(it.key, it.value.first, it.value.second, selectedPosition) { position, date ->
                            if (groupAdapterDays.getItem(selectedPosition) is DayItem) {
                                // (groupAdapterDays.getItem(selectedPosition) as DayItem).isSelected = false
                                (groupAdapterDays.getItem(selectedPosition) as DayItem).selectedPosition = position
                                groupAdapterDays.notifyItemChanged(selectedPosition)
                            }
                            selectedPosition = position
                            presenter.selectDate(date)
                        }
                    }
            )


            recyclerViewDays.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = groupAdapterDays
            }
        }
    }

    override fun unbind(holder: ViewHolder) {
        saveInstanceStateTags = holder.itemView.recyclerViewTags.layoutManager?.onSaveInstanceState()
        saveInstanceStateDays = holder.itemView.recyclerViewDays.layoutManager?.onSaveInstanceState()
        super.unbind(holder)
    }


    private fun getMapDate(dateStart: Long, dateEnd: Long): Map<Int, Pair<String, Long>> {
        val mapDate = hashMapOf<Int, Pair<String, Long>>()
        val calendarStart = Calendar.getInstance()
        calendarStart.timeInMillis = dateStart

        while (calendarStart.time.before(Date(dateEnd))) {
            mapDate.put(calendarStart.get(Calendar.DAY_OF_MONTH), Pair<String, Long>(calendarStart.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()), calendarStart.timeInMillis))
            calendarStart.add(Calendar.DAY_OF_MONTH, 1)
        }
        mapDate.put(calendarStart.get(Calendar.DAY_OF_MONTH), Pair<String, Long>(calendarStart.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()), calendarStart.timeInMillis))

        return mapDate
    }

    override fun getLayout() = R.layout.item_header_schedule
}