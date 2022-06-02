package com.example.ui.subevent

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.*
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import withProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class SubeventPresenter @Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : BasePresenter<SubeventContract.View>(appData), SubeventContract.Presenter {

    lateinit var event: String
    lateinit var subevent: String

    private var firstLoading = true


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getSubEventData()
    }

    private fun getSubEventData(){
        val eventId = event
        val subeventId = subevent
        compositeDisposable += eventRepository.getEventActivityDetail(subeventId)
            .withProgressBarLoadingDialog(viewState)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                Log.e("TAG", it.binds?.tag.toString())
                if (firstLoading) viewState.setData(it)
                firstLoading = false

                val uid = appData.getId()
                it.binds?.member?.forEach { speaker ->
                    speaker.binds?.user?.isCurrentUser = speaker.user == uid
                }
                viewState.setSpeakers(it.binds?.member ?: emptyList())
            }
    }

    override fun onSpeakerClick(speaker: MemberModel) {
        viewState.showSpeakerProfile(speaker)
    }

    override fun onSpeakerChangeSubscriptionClick(speaker: MemberModel) {
        val id = speaker.user.toString()
        if (speaker.binds?.user?.binds?.userFavorite == null)
            compositeDisposable += eventRepository.addToFavorites(
                AddToFavoriteModel(
                    appData.getId(),
                    AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_SPEAKER, id.toInt())
                )
            )
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple({
                    viewState.showRequestErrorMessage()
                }) {
                    speaker.binds?.user?.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                    viewState.updateSpeaker(speaker)
                }
        else
            compositeDisposable += eventRepository.deleteFromFavorite(speaker.binds.user.binds?.userFavorite?.id.toString())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple({
                    viewState.showRequestErrorMessage()
                }) {
                    speaker.binds.user.binds?.userFavorite = null
                    viewState.updateSpeaker(speaker)
                }
    }

    override fun onSubeventChangeSubscriptionClick(subevent: EventActivityModel) {
        val id = subevent.id.toString()
        if (subevent.binds?.userFavorite == null)
            compositeDisposable += eventRepository.addToFavorites(
                AddToFavoriteModel(
                    appData.getId(),
                    AddToFavoriteEntityModel(
                        AddToFavoriteEntityModel.FAVORITE_SUB_EVENT,
                        id.toInt()
                    )
                )
            )
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple({
                    viewState.showRequestErrorMessage()
                }) {
                    subevent.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                    viewState.setData(subevent)
                }
        else
            compositeDisposable += eventRepository.deleteFromFavorite(subevent.binds.userFavorite?.id.toString())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple({
                    viewState.showRequestErrorMessage()
                }) {
                    subevent.binds.userFavorite = null
                    viewState.setData(subevent)
                }
    }

    override fun onAddToScheduleClick(subEvent: EventActivityModel) {
        processChangeEventInCalendarStatusRequest(
            subEvent,
            eventRepository.addEventToCalendarWithResult(
                EventCalendarBody(
                    appData.getId(),
                    EventCalendarBodyEntity(
                        EventCalendarBody.CALENDAR_EVENT_ACTIVITY, subEvent.id
                            ?: 0
                    )
                )
            )
                .flatMapCompletable { subEv ->
                    Completable.fromAction {
                        subEvent.binds?.userCalendar = subEv
                    }
                }
        )
    }

    override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) {
        processChangeEventInCalendarStatusRequest(
            subEvent,
            eventRepository.deleteCalendarEvent(subEvent.binds?.userCalendar?.id.toString())
                .andThen(Completable.fromAction { subEvent.binds?.apply { userCalendar = null } })
        )
    }

    protected open fun processChangeEventInCalendarStatusRequest(
        subEvent: EventActivityModel,
        request: Completable
    ) {
        compositeDisposable += request
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            //.withLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                },
                onComplete = {
                    viewState.updateSubevent(subEvent)
                })


    }
}
