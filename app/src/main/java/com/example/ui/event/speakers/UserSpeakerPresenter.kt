package com.example.ui.event.speakers

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
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarLoadingDialog
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
    private lateinit var mProfileUserData: ProfileUserData

    private var firstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadUserData()
    }

    private fun loadUserData() {
        viewState.setEmptyMainDataPlaceholder()
        // userRepository.getUserByIdNew(userId).toObservable()
        compositeDisposable += eventRepository.getEventMember(memberId)
            .performOnBackgroundOutOnMain()
            //.withProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    if (it.binds?.user != null) {
                        mUser = it.binds.user
                    }
                    viewState.showSpeakerMainInfo(it, isCurrentUser())
                    viewState.setSpeakerActivities(it.binds?.activities ?: emptyList())
                })


    }

    override fun onWriteMessageClick(speaker: UserDetail) {
        if (speaker.binds?.chatRoomWithMe == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(speaker?.id))
                .performOnBackgroundOutOnMain()
                .withProgressBarLoadingDialog(viewState)
                //.withLoadingDialog(viewState)
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
                //.withLoadingDialog(viewState)
                .withProgressBarLoadingDialog(viewState)
                .subscribeSimple({
                    viewState.showRequestErrorMessage()
                }) {
                    Log.e("ADDED TO FAVORITE", it.user.toString())
                    mUser.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                    viewState.updateSpeaker(mUser)
                }
        else
            compositeDisposable += eventRepository.deleteFromFavorite(mUser.binds?.userFavorite?.id.toString())
                .performOnBackgroundOutOnMain()
                .withProgressBarLoadingDialog(viewState)
                //.withLoadingDialog(viewState)
                .subscribeSimple({
                    viewState.showRequestErrorMessage()
                }) {
                    Log.e("DELETED FROM FAVORITE", mUser.binds?.userFavorite?.id.toString())
                    mUser.binds?.userFavorite = null
                    viewState.updateSpeaker(mUser)
                }
    }



    override fun onSubEventClick(subEvent: EventActivityModel) {
        //viewState.showSubEvent(userEvent.eventId, subEvent.id.toString())
        checkInternetAndRun {
            viewState.showSubEvent(eventId, subEvent.id.toString())
        }
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

    fun isCurrentUser() = getUserDetailId() == appData.getId().toString()

    private fun processChangeEventInCalendarStatusRequest(
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
                    Log.e("ID", subEvent.binds?.userCalendar?.id.toString())
                    viewState.updateSubEvent(subEvent)
                })


    }

    fun getUserDetailId(): String {
        return mUser.id.toString()
    }
}
