package com.example.ui.speakers.event

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Speaker
import com.example.repository.SpeakerRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.speakers.base.BaseSpeakersPresenter
import com.example.util.pagination.SimplePagination
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class EventSpeakersPresenter
@Inject constructor(
        private val userRepository: UserRepository,
        private val speakerRepository: SpeakerRepository
) : BaseSpeakersPresenter<EventSpeakersContract.View>(userRepository,speakerRepository), EventSpeakersContract.Presenter {


}
