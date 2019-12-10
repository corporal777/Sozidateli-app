package com.example.ui.event.schedule.complete

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.event.schedule.EventScheduleFragment
import javax.inject.Inject
import javax.inject.Provider

class EventCompleteScheduleFragment : EventScheduleFragment<EventCompleteSchedulePresenter>() {

    @InjectPresenter
    override lateinit var presenter: EventCompleteSchedulePresenter

    @Inject
    lateinit var presenterProvider: Provider<EventCompleteSchedulePresenter>

    @ProvidePresenter
    fun providePresenter(): EventCompleteSchedulePresenter = presenterProvider.get()


    override fun getTitle() = getString(R.string.schedule_events_title)
    override fun getEmptyDayPlaceholderText() = getString(R.string.schedule_complete_empty_day_placeholder)
    override fun getEmptyDayPlaceholderDescription(): String? = null
}