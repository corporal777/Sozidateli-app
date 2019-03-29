package com.example.ui.event.schedule.my

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
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

    override fun setTags(tags: List<Tag>?) {
        super.setTags(null)
    }
}