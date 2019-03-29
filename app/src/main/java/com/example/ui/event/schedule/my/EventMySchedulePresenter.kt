package com.example.ui.event.schedule.my

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.repository.EventRepository
import com.example.ui.event.schedule.EventSchedulePresenter
import io.reactivex.Completable
import javax.inject.Inject

@InjectViewState
class EventMySchedulePresenter
@Inject constructor(
        eventRepository: EventRepository,
        userEventData: UserEventData
) : EventSchedulePresenter(eventRepository, userEventData) {

    override fun createRequestFilter(): Map<String, Any> = mapOf(
            "only_in_my_calendar" to "Y"
    )

    override fun processChangeEventInCalendarStatusRequest(request: Completable) {
        request.doOnComplete {  }
        super.processChangeEventInCalendarStatusRequest(request)
    }
}