package com.example.ui.subevent

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.*
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.zipWith
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withProgressBarLoadingDialog
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class SubEventPresenter @Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : BasePresenter<SubEventContract.View>(appData), SubEventContract.Presenter {

    lateinit var subEventId: String
    lateinit var eventId: String

    private var firstLoading = true
    private var mDy = 0


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(0f)
        getSubEventData()
    }

    private fun getSubEventData() {
        val subEventId = subEventId
        val speakersList = arrayListOf<MemberModel>()
        compositeDisposable += eventRepository.getEventActivityDetail(subEventId)
            .zipWith(eventRepository.getEventDetailForRegister(eventId).toSingle())
            .doOnSuccess {
                speakersList.addAll(it.first.binds?.member?.filter { x -> x.isLead == true }
                    ?.sortedBy { x -> x.binds?.user?.fullName } ?: emptyList())
                speakersList.addAll(it.first.binds?.member?.filter { x -> x.isLead == false }
                    ?.sortedBy { x -> x.binds?.user?.fullName } ?: emptyList())
            }
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    catchSubEventError(it)
                    it.printStackTrace()
                },
                onSuccess = {
                    val subEvent = it.first
                    val isApproved =
                        it.second.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED
                    if (firstLoading) viewState.setData(isApproved, subEvent)
                    firstLoading = false

                    val uid = appData.getId()
                    subEvent.binds?.member?.forEach { speaker ->
                        speaker.binds?.user?.isCurrentUser = speaker.user == uid
                    }
                    viewState.setSpeakers(speakersList)
                })

    }

    override fun attachView(view: SubEventContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(abs(mDy / 10f))
    }

    override fun changeAppBarElevation(value: Int) {
        mDy += value
        viewState.setAppBarElevation(abs(mDy / 10f))
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
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onComplete = {
                    viewState.updateSubEvent(subEvent)
                })
    }

    private fun catchSubEventError(t: Throwable) {
        if (t is HttpException) {
            when (t.code()) {
                403 -> {
                    try {
                        val error = Gson().fromJson(
                            t.response()?.errorBody()?.string(),
                            NewErrors::class.java
                        )
                        when (error.errors[0].message) {
                            "The event has been banned" -> {
                                val eventName = error.errors[0].additionalData?.name
                                val eventId = error.errors[0].additionalData?.id.toString()
                                val message =
                                    "Мероприятие «$eventName» заблокировано."
                                viewState.showEventErrorMessageDialog(true, eventId, message)
                            }
                            "The event has been cancelled" -> {
                                val eventName = error.errors[0].additionalData?.name
                                val eventId = error.errors[0].additionalData?.id.toString()
                                val message =
                                    "Мероприятие «$eventName» было отменено организатором."
                                viewState.showEventErrorMessageDialog(true, eventId, message)
                            }
                            else -> {
                                onReceiveError(t)
                            }
                        }
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }

}
