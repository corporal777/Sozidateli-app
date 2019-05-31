package com.example.ui.event.schedule.complete

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.data.database.Db
import com.example.data.models.SubEvent
import com.example.di.Connectivity
import com.example.events.OnDayChangeFromCompleteSchedule
import com.example.events.OnDayChangeFromMySchedule
import com.example.repository.EventRepository
import com.example.ui.event.schedule.EventSchedulePresenter
import io.reactivex.Completable
import io.reactivex.Observable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@InjectViewState
class EventCompleteSchedulePresenter
@Inject constructor(
        eventRepository: EventRepository,
        userEventData: UserEventData,
        db: Db,
        @Connectivity connectivity: Observable<Boolean>
) : EventSchedulePresenter(eventRepository, userEventData, db, connectivity) {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        EventBus.getDefault().register(this)
    }

    override fun filterSubEvent(subEvent: SubEvent) = true

    override fun processChangeEventInCalendarStatusRequest(request: Completable) {
        super.processChangeEventInCalendarStatusRequest(request.doFinally {
            val millis = currentDay?.millis ?: return@doFinally
            EventBus.getDefault().post(OnDayChangeFromCompleteSchedule(millis))
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OnDayChangeFromMySchedule) {
        onDayChanged(event.dayDate)
    }
}