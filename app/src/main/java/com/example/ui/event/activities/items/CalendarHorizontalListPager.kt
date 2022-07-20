package com.example.ui.event.activities.items

import android.os.Handler
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.example.R
import com.example.data.models.EventScheduleCalendarDay
import com.example.databinding.ItemCalendarListPagerBinding
import com.example.extensions.findItemBy
import com.example.extensions.forEachItems
import com.example.holders.CalendarHorizontalListItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import com.xwray.groupie.databinding.BindableItem

class CalendarHorizontalListPager(
    private val days: List<EventScheduleCalendarDay>,
    private val onDaySelect: (date: EventScheduleCalendarDay) -> Unit
) : BindableItem<ItemCalendarListPagerBinding>() {

    val groupAdapter = GroupAdapter<GroupieViewHolder>()
    val listItems = arrayListOf<CalendarHorizontalListItem>()
    var mCount = 0
    var mCountDays = 0
    private val listDays = arrayListOf<EventScheduleCalendarDay>()

    val section = Section()

    private var mViewPager: ViewPager2? = null

    override fun bind(viewBinding: ItemCalendarListPagerBinding, position: Int) {
        viewBinding.apply {
            mViewPager = viewPager
            days.map { day ->
                mCount += 1
                mCountDays += 1
                listDays.add(day)
                if (mCount == 7) {
                    mCount = 0
                    section.add(CalendarHorizontalListItem(listDays, onDaySelect))
                    //listItems.add(CalendarHorizontalListItem(listDays, onDaySelect))
                    listDays.clear()
                }
            }

            //groupAdapter.update(listItems)
            groupAdapter.update(listOf(section))
            viewPager.adapter = groupAdapter
        }
    }

    fun selectDay(day: EventScheduleCalendarDay) {
        for (i in 0 until section.itemCount) {
            val item = section.getItem(i) as CalendarHorizontalListItem
            item.selectDay(day)
        }
        deselectAllExcept(day)
    }

    fun scrollToDay(day: EventScheduleCalendarDay) {
//        var mPosition = 0
//        if (day.hasEvents){
//            Handler().post(Runnable {
//                selectDay(day)
//                val item = section.findItemBy<CalendarHorizontalListItem> { it.scrollToDay(day) }
//                if (item != null){
//                    mPosition = section.getPosition(item!!)
//                    Log.e("Pos", mPosition.toString())
//                    mViewPager?.setCurrentItem(mPosition, true)
//                }
//            })
//        }

    }

    private fun deselectAllExcept(except: EventScheduleCalendarDay) {
        for (i in 0 until section.itemCount) {
            val item = section.getItem(i) as CalendarHorizontalListItem
            item.deselectAllExcept(except)
        }
    }

    override fun getLayout(): Int = R.layout.item_calendar_list_pager
}