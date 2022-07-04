package com.example.ui.subevent

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.*
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.zipWith
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class SubEventPresenter @Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : BasePresenter<SubEventContract.View>(appData), SubEventContract.Presenter {

    lateinit var subEventId: String
    lateinit var eventId: String

    private var firstLoading = true


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getSubEventData()
    }

    private fun getSubEventData() {
        val subEventId = subEventId
        compositeDisposable += eventRepository.getEventActivityDetail(subEventId)
            .zipWith(eventRepository.getEventDetailForRegister(eventId).toSingle())
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                val subEvent = it.first
                val isApproved =
                    it.second.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED
                if (firstLoading) viewState.setData(isApproved, subEvent)
                firstLoading = false

                val uid = appData.getId()
                subEvent.binds?.member?.forEach { speaker ->
                    speaker.binds?.user?.isCurrentUser = speaker.user == uid
                }
                viewState.setSpeakers(subEvent.binds?.member ?: emptyList())
            }
    }

    override fun onSpeakerClick(speaker: MemberModel) {
        viewState.showSpeakerProfile(speaker)
    }

    /*override fun onSpeakerChangeSubscriptionClick(speaker: MemberModel) {
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
                    //viewState.updateSpeaker(speaker)
                }
        else
            compositeDisposable += eventRepository.deleteFromFavorite(speaker.binds.user.binds?.userFavorite?.id.toString())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple({
                    viewState.showRequestErrorMessage()
                }) {
                    speaker.binds.user.binds?.userFavorite = null
                    //viewState.updateSpeaker(speaker)
                }
    }
    */
    /* override fun onSubeventChangeSubscriptionClick(subevent: EventActivityModel) {
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
 */
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
            .subscribeSimple {
                viewState.updateSubEvent(subEvent)
            }
    }

}
