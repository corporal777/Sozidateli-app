package com.example.ui.speakers.event

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.data.models.Speaker
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.speakers.base.BaseSpeakersPresenter
import com.example.util.pagination.PaginationDataSourceFactory
import javax.inject.Inject

@InjectViewState
class EventSpeakersPresenter
@Inject constructor(
        userRepository: UserRepository,
        private val eventRepository: EventRepository
) : BaseSpeakersPresenter<EventSpeakersContract.View>(userRepository), EventSpeakersContract.Presenter {

    lateinit var event: Event

    override val pagination: PaginationDataSourceFactory<Speaker>
        get() = PaginationDataSourceFactory { limit, offset -> eventRepository.getEventSpeakers(event.id, limit, offset) }
}
