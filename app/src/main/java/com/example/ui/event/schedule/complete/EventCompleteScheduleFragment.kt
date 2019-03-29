package com.example.ui.event.schedule.complete

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
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
}