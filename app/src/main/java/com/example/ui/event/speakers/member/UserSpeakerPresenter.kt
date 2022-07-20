package com.example.ui.event.speakers.member

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.*
import com.example.data.models.*
import com.example.repository.ChatRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.zipWith
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarLoadingDialog
import java.lang.Math.abs
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

    lateinit var mUser: UserDetail
    private var mDy: Int = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadUserData()
    }

    override fun attachView(view: UserSpeakerContract.View?) {
        super.attachView(view)
        viewState.changeAppbarElevation(abs(mDy / 10f))
    }

    override fun changeAppBarElevation(value: Int) {
        mDy += value
        viewState.changeAppbarElevation(abs(mDy / 10f))
    }

    private fun loadUserData() {
        viewState.setEmptyMainDataPlaceholder()
        compositeDisposable += eventRepository.getEventMember(memberId)
            .zipWith(eventRepository.getEventDetailForRegister(eventId))
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    val member = it.first
                    val registerStatus =
                        it.second.binds?.currentUserRegistration?.status?.value == Event.Status.APPROVED

                    if (member.binds?.user != null) {
                        mUser = member.binds.user
                        viewState.updateSpeaker(member.binds.user)
                    }
                    val listSubEvents = member.binds?.activities?.groupBy { event ->
                        event.holdingDate?.from?.split(" ")?.get(0)
                    }

                    viewState.setSpeakersMainInfo(member, isCurrentUser())
                    viewState.setSpeakerActivities(registerStatus, listSubEvents)
                })


    }

    override fun onWriteMessageClick(speaker: UserDetail) {
        if (speaker.binds?.chatRoomWithMe == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(speaker?.id))
                .performOnBackgroundOutOnMain()
                .withProgressBarLoadingDialog(viewState)
                .subscribeSimple(
                    onError = {
                        onReceiveError(it)
                    },
                    onSuccess = {
                        viewState.openChat(
                            speaker.fullName ?: "",
                            speaker.image?.uri, it.id.toString()
                        )
                    })
        } else {
            viewState.openChat(
                speaker.fullName,
                speaker.image.uri,
                speaker.binds?.chatRoomWithMe?.id.toString()
            )
        }
    }

    override fun onAddSpeakerToFavoriteClick(id: String) {
        if (mUser.binds?.userFavorite == null)
            compositeDisposable += eventRepository.addToFavorites(
                AddToFavoriteModel(
                    appData.getId(),
                    AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_SPEAKER, id.toInt())
                )
            )
                .performOnBackgroundOutOnMain()
                .withProgressBarLoadingDialog(viewState)
                .subscribeSimple({
                    viewState.showRequestErrorMessage()
                }) {
                    Log.e("ADDED TO FAVORITE", it.user.toString())
                    mUser.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                    viewState.updateSpeaker(mUser)
                    viewState.showSpeakerAddedToFavoriteMessage()
                }
        else
            compositeDisposable += eventRepository.deleteFromFavorite(mUser.binds?.userFavorite?.id.toString())
                .performOnBackgroundOutOnMain()
                .withProgressBarLoadingDialog(viewState)
                .subscribeSimple({
                    viewState.showRequestErrorMessage()
                }) {
                    Log.e("DELETED FROM FAVORITE", mUser.binds?.userFavorite?.id.toString())
                    mUser.binds?.userFavorite = null
                    viewState.updateSpeaker(mUser)
                }
    }


    override fun onSubEventClick(subEvent: EventActivityModel) {
        checkInternetAndRun {
            viewState.showSubEvent(eventId, subEvent.id.toString())
        }
    }

    override fun onGoToProfileClick() = viewState.showUserProfile(mUser.id.toString())

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
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                },
                onComplete = {
                    Log.e("ID", subEvent.binds?.userCalendar?.id.toString())
                    viewState.updateSubEvent(subEvent)
                })


    }

    fun isCurrentUser() = getUserDetailId() == appData.getId().toString()
    fun getUserDetailId(): String = mUser.id.toString()
}
