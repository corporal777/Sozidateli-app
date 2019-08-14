package com.example.ui.speakers.event

import com.arellomobile.mvp.InjectViewState
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.speakers.base.BaseSpeakersPresenter
import javax.inject.Inject

@InjectViewState
class EventSpeakersPresenter
@Inject constructor(
        userRepository: UserRepository,
        private val eventRepository: EventRepository
) : BaseSpeakersPresenter<EventSpeakersContract.View>(userRepository), EventSpeakersContract.Presenter
