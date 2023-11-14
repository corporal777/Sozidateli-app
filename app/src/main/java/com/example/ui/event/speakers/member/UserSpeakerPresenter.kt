package com.example.ui.event.speakers.member

import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.AddToFavoriteEntityModel.Companion.FAVORITE_SPEAKER
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.bodies.CreateChatBody
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.Event
import com.example.data.models.EventActivityModel
import com.example.data.models.EventUserFavorite
import com.example.data.models.UserDetail
import com.example.repository.ChatRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.zipWith
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class UserSpeakerPresenter
@Inject constructor(
    private val appData: AppData,
    private val userEventData: UserEventData,
    private val chatRepository: ChatRepository,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : BasePresenter<UserSpeakerContract.View>(appData), UserSpeakerContract.Presenter {

    lateinit var eventId: String
    lateinit var memberId: String

    lateinit var user: UserDetail
    private var isFirstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setEmptyMainDataPlaceholder()
        loadSpeakerData()
    }

    override fun attachView(view: UserSpeakerContract.View?) {
        super.attachView(view)
        if (isFirstLaunch) isFirstLaunch = false
        else loadSpeakerData()
    }

    private fun loadSpeakerData() {
        compositeDisposable += eventRepository.getEventMember(memberId)
            .zipWith(eventRepository.getEventDetailForRegister(eventId))
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    val member = it.first
                    val registerStatus =
                        it.second.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED

                    if (member.binds?.user != null) {
                        user = member.binds.user
                        viewState.updateSpeaker(member.binds.user)
                    }
                    val listSubEvents = member.binds?.activities?.groupBy { event ->
                        event.holdingDate?.from?.split(" ")?.get(0)
                    }

                    viewState.setSpeakersMainInfo(member, isCurrentUser())
                    viewState.setSpeakerActivities(registerStatus, listSubEvents)
                })

    }

    override fun onWriteMessageClick() {
        if (user.binds?.chatRoomWithMe == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(user.id))
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
                .subscribeSimple(
                    onError = { onReceiveError(it) },
                    onSuccess = {
                        viewState.openChat(user.fullName, user.loadUserImage(), it.id.toString())
                    }
                )
        } else {
            viewState.openChat(user.fullName, user.loadUserImage(), user.binds?.chatRoomWithMe?.id.toString())
        }
    }

    override fun onAddSpeakerToFavoriteClick() {
        if (user.binds?.userFavorite == null)
            compositeDisposable += eventRepository.addToFavorites(
                AddToFavoriteModel.toBody(
                    appData.getId(),
                    FAVORITE_SPEAKER,
                    getUserDetailId().toInt()
                )
            )
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                    onError = { onReceiveError(it) },
                    onSuccess = {
                        user.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                        viewState.apply {
                            updateSpeaker(user)
                            showEventAddedToFavoriteDialog()
                        }
                    })
        else compositeDisposable += eventRepository.deleteFromFavorite(user.binds?.userFavorite?.id.toString())
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = {
                    user.binds?.userFavorite = null
                    viewState.apply {
                        updateSpeaker(user)
                        showEventRemovedFromFavoriteDialog()
                    }
                })
    }


    override fun onSubEventClick(subEvent: EventActivityModel) {
        checkInternetAndRun {
            viewState.showSubEvent(eventId, subEvent.id.toString())
        }
    }

    override fun onGoToProfileClick() {
        if (appData.isCurrentUser(user.id.toString())) {
            viewState.showCurrentUserProfile()
        } else viewState.showUserProfile(user.id.toString())
    }

    override fun onItemTake(position: Int) {
    }

    override fun onRemoveFromScheduleClick(subEvent: EventActivityModel) {
        processChangeEventInCalendarStatusRequest(
            subEvent,
            eventRepository.deleteCalendarEvent(subEvent.binds?.userCalendar?.id.toString())
                .andThen(Completable.fromAction { subEvent.binds?.apply { userCalendar = null } })
        )
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

    private fun processChangeEventInCalendarStatusRequest(
        subEvent: EventActivityModel,
        request: Completable
    ) {
        compositeDisposable += request
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onComplete = {
                    viewState.updateSubEvent(subEvent)
                })


    }

    fun isCurrentUser() = getUserDetailId() == appData.getId().toString()
    fun isUserRegistered(): Boolean {
        if (this::user.isInitialized) return user.state?.isRegistered ?: false
        else return false
    }

    private fun getUserDetailId(): String = user.id.toString()
}
