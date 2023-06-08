package com.example.ui.event.speakers.list

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class EventSpeakersPresenter
@Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userEventData: UserEventData,
    private val userRepository: UserRepository
) : BasePresenter<EventSpeakersContract.View>(appData), EventSpeakersContract.Presenter {

    lateinit var eventId: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(List(10) { null })
        loadData()
    }

    private fun loadData() {
        compositeDisposable += userEventData.loadSpeakers(eventId)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.setData(it) }
            )
    }


    override fun onSpeakerClick(id: Int) = viewState.showSpeaker(eventId, id)
    override fun onRefreshRequest() = loadData()
}
