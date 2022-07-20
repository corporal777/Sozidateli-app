package com.example.holders

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.example.data.models.EventScheduleCalendarDay
import com.example.ui.event.activities.items.HorizontalListItemNew
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain

class CalendarHorizontalListItem(
    days: List<EventScheduleCalendarDay>,
    private val onDaySelect: (date: EventScheduleCalendarDay) -> Unit
) : HorizontalListItemNew<GroupieViewHolder>() {


    val compositeDisposable = CompositeDisposable()

    private val items = days.map { day ->
        DayItem(day, onDaySelect)
    }


    init {
        adapter = GroupAdapter<GroupieViewHolder>().apply { addAll(items) }
        backgroundColor = Color.WHITE
    }

    fun selectDay(day: EventScheduleCalendarDay) {
        items.find { it.day == day }?.let {
            if (!it.isSelected) {
                it.isSelected = true
                it.notifyChanged()
            }
        }
        deselectAllExcept(day)
    }

    fun selectFirstDay(){
        val day = items.first()
        if (!day.isSelected){
            day.isSelected = true
            day.notifyChanged()
        }
        deselectAllExcept(day.day)
    }
    fun deselectAllExcept(except: EventScheduleCalendarDay) {
        items.find { it.day != except && it.isSelected }?.let {
            if (it.isSelected) {
                it.isSelected = false
                it.notifyChanged()
            }
        }
    }


    fun changeDay(day: EventScheduleCalendarDay): Boolean {
        var isDay = false
        items.find { it.day.millis == day.millis }?.let {
            isDay = true
        }
        return isDay
    }

    fun scrollToDay(day: EventScheduleCalendarDay){
        var position = 0
        compositeDisposable += Completable.fromAction {
            position = items.indexOfFirst { it.day == day }
        }
            .performOnBackgroundOutOnMain()
            .subscribe {
                val item = items.get(position)
                deselectAllExcept(day)
                if (!item.isSelected) {
                    item.isSelected = true
                    item.notifyChanged()
                }
                smoothScrollToPosition(position)
            }
    }




    fun selectDayNew(day: EventScheduleCalendarDay): Boolean {
        var isItem = false
        items.find { it.day.millis == day.millis }?.let {
            isItem = true
            if (!it.isSelected) {
                it.isSelected = true
                it.notifyChanged()
            }
        }

        deselectAllExceptNew(day)
        return isItem
    }


    private fun deselectAllExceptNew(except: EventScheduleCalendarDay) {
        items.forEach {
            if (it.day.millis != except.millis && it.isSelected) {
                it.isSelected = false
                it.notifyChanged()
            }
        }
    }
}