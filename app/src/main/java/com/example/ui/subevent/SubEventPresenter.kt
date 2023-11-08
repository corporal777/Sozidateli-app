package com.example.ui.subevent

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.Event
import com.example.data.models.EventActivityModel
import com.example.data.models.EventNew
import com.example.data.models.MemberModel
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.data.models.AboutSubEventData
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCheckInternetConnectivity
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class SubEventPresenter @Inject constructor(
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : BasePresenter<SubEventContract.View>(appData), SubEventContract.Presenter {

    lateinit var subEventId: String
    lateinit var eventId: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setSubEventPlaceholder()
        compositeDisposable += loadData()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { catchSubEventError(it) },
                onSuccess = {
                    viewState.setData(it.isApproved, it.subEvent)
                    viewState.setSpeakers(it.members)
                })
    }


    override fun onSpeakerClick(speaker: Int) = viewState.showSpeakerProfile(speaker)


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

    private fun processChangeEventInCalendarStatusRequest(
        subEvent: EventActivityModel,
        request: Completable
    ) {
        compositeDisposable += request
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = { viewState.updateSubEvent(subEvent) }
            )
    }

    private fun loadData(): Single<AboutSubEventData> {
        return Single.zip(eventRepository.getEventActivityDetail(subEventId),
            eventRepository.getEventDetailForRegister(eventId).toSingle(),
            BiFunction<EventActivityModel, EventNew, AboutSubEventData> { subEvent, event ->
                val speakersList = arrayListOf<MemberModel>()
                speakersList.addAll(subEvent.binds?.member?.filter { x -> x.isLead == true }?.sortedBy { x -> x.binds?.user?.fullName } ?: emptyList())
                speakersList.addAll(subEvent.binds?.member?.filter { x -> x.isLead == false }?.sortedBy { x -> x.binds?.user?.fullName } ?: emptyList())

                subEvent.binds?.member?.forEach { speaker ->
                    speaker.binds?.user?.isCurrentUser = speaker.user == appData.getId()
                }
                val isApproved = event.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED
                AboutSubEventData(subEvent, isApproved, speakersList)
            })
    }

    private fun catchSubEventError(t: Throwable) {
        t.printStackTrace()
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
