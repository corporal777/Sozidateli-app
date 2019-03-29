package com.example.ui.event.schedule.complete

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.repository.EventRepository
import com.example.ui.event.schedule.EventSchedulePresenter
import javax.inject.Inject

@InjectViewState
class EventCompleteSchedulePresenter
@Inject constructor(
        eventRepository: EventRepository,
        userEventData: UserEventData
) : EventSchedulePresenter(eventRepository, userEventData) {

    override fun createRequestFilter(): Map<String, Any> = emptyMap()
}