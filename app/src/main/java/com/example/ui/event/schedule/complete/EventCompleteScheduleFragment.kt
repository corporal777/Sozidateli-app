package com.example.ui.event.schedule.complete

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.events.OnDayChangeFromCompleteSchedule
import com.example.events.OnDayChangeFromMySchedule
import com.example.ui.event.schedule.EventScheduleFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import javax.inject.Provider

class EventCompleteScheduleFragment : EventScheduleFragment<EventCompleteSchedulePresenter>() {

    @InjectPresenter
    override lateinit var presenter: EventCompleteSchedulePresenter

    @Inject
    lateinit var presenterProvider: Provider<EventCompleteSchedulePresenter>

    @ProvidePresenter
    fun providePresenter(): EventCompleteSchedulePresenter = presenterProvider.get()

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(event: OnDayChangeFromMySchedule) {
        EventBus.getDefault().removeStickyEvent(event)
        presenter.onDayChanged(event.dayDate)
    }

    override fun sendDayChangeEvent(date: Long) {
        EventBus.getDefault().postSticky(OnDayChangeFromCompleteSchedule(date))
    }
}