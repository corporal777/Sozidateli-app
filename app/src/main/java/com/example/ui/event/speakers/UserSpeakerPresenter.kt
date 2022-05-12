package com.example.ui.event.speakers

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.bodies.CreateChatBody
import com.example.data.models.*
import com.example.repository.ChatRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
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
    lateinit var userId: String

    lateinit var mUser: UserDetail
    private lateinit var mProfileUserData: ProfileUserData

    private var firstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadUserData()
    }

    private fun loadUserData() {
        viewState.setEmptyMainDataPlaceholder()
        viewState.setEmptyEventsPlaceholder()
        userRepository.getUserByIdNew(userId).toObservable()
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            //.withLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.setEmptyMainDataPlaceholder()
                }, onNext = {
                    mUser = it
                    viewState.showSpeakerMainInfo(it)
                    loadEventActivities()
                })

    }

    private fun loadEventActivities() {
        eventRepository.getEventActivities(eventId.toInt())
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.setEmptyEventsPlaceholder()
                },
                onSuccess = {
                    viewState.setSpeakerActivities(it)
                }
            )
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

            /*.subscribe({
                viewState.openChat(user.fullName, user.image.uri, it.id.toString())
            }, { it.printStackTrace() })*/
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

    override fun onItemTake(position: Int) {
        TODO("Not yet implemented")
    }

    private fun isCurrentUser() = userId == appData.getId().toString()

}
