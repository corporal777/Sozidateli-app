package com.example.ui.event.schedule.my

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.data.models.Tag
import com.example.events.OnDayChangeFromCompleteSchedule
import com.example.events.OnDayChangeFromMySchedule
import com.example.ui.event.schedule.EventScheduleFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(event: OnDayChangeFromCompleteSchedule) {
        EventBus.getDefault().removeStickyEvent(event)
        presenter.onDayChanged(event.dayDate)
    }

    override fun sendDayChangeEvent(date: Long) {
        EventBus.getDefault().postSticky(OnDayChangeFromMySchedule(date))
    }
}