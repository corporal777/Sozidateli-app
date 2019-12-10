package com.example.ui.event.schedule.my

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Tag
import com.example.ui.event.schedule.EventScheduleFragment
import javax.inject.Inject
import javax.inject.Provider

class EventMyScheduleFragment : EventScheduleFragment<EventMySchedulePresenter>() {

    @InjectPresenter
    override lateinit var presenter: EventMySchedulePresenter

    @Inject
    lateinit var presenterProvider: Provider<EventMySchedulePresenter>

    @ProvidePresenter
    fun providePresenter(): EventMySchedulePresenter = presenterProvider.get()

    override fun setTags(tags: List<Tag>?) {}

    override fun getTitle() = getString(R.string.schedule_title)
    override fun getEmptyDayPlaceholderText() = getString(R.string.schedule_my_empty_day_placeholder_title)
    override fun getEmptyDayPlaceholderDescription() = getString(R.string.schedule_my_empty_day_placeholder_description)
}