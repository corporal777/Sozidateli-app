package com.example.ui.user

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteEntityModel.Companion.FAVORITE_SPEAKER
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.bodies.CreateChatBody
import com.example.data.models.*
import com.example.repository.ChatRepository
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.loadBitmap
import com.example.util.loadBitmapNew
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserPresenter
@Inject constructor(
    private val appData: AppData,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val commonRepository: CommonRepository,
    private val eventRepository: EventRepository,
) : BasePresenter<UserContract.View>(appData), UserContract.Presenter {

    lateinit var userId: String
    private lateinit var profileUserData: ProfileUserData
    lateinit var context: Context


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadUserData()
    }

    private fun loadUserData() {
        compositeDisposable += userLoadRequest()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showUserHiddenDialog()
                },
                onSuccess = {
                    compositeDisposable += userAddressRequest()
                        .performOnBackgroundOutOnMain()
                        .subscribeSimple {
                            viewState.apply {
                                setUser(profileUserData)
                                if (profileUserData.user.state?.isRegistered == true) {
                                    setSubscribeFavoriteAction(profileUserData.user.getUserSubscribeAction())
                                    setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                                }
                            }
                        }
                })
    }


    override fun onWriteMessageClick() {
        val user = profileUserData.user
        if (user.binds?.chatRoomWithMe == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(user.id))
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple {
                    viewState.openChat(user.fullName, user.loadUserImage(), it.id.toString())
                }
        } else {
            viewState.openChat(
                user.fullName,
                user.loadUserImage(),
                user.binds?.chatRoomWithMe?.id.toString()
            )
        }
    }

    override fun onOrganizationClick(organization: OrganizationNew) {
        viewState.showOrganization(organization)
    }

    override fun onFileClick(file: FileModel) {
        file.uri?.let { viewState.downloadFile(it) }
    }

    override fun onSubscribeClick() {
        compositeDisposable += eventRepository.addToFavorites(AddToFavoriteModel.toBody(appData.getId(), FAVORITE_SPEAKER, userId.toInt()))
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                profileUserData.user.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                viewState.setSubscribeFavoriteAction(profileUserData.user.getUserSubscribeAction())
                viewState.showEventAddedToFavoriteDialog()
            }
    }

    override fun onUnsubscribeClick() {
        compositeDisposable += eventRepository.deleteFromFavorite(profileUserData.user.binds?.userFavorite?.id.toString())
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                profileUserData.user.binds?.userFavorite = null
                viewState.setSubscribeFavoriteAction(profileUserData.user.getUserSubscribeAction())
                viewState.showEventRemovedFromFavoriteDialog()
            }
    }

    override fun onUnblockClick() {
        val user = profileUserData.user
        if (user.binds?.chatRoomWithMe == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(userId.toInt()))
                .flatMapCompletable { chatRepository.deleteBan(user.binds?.isUserInBan?.id ?: 1) }
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribe({
                    profileUserData.user.binds?.isUserInBan = null
                    viewState.setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                    viewState.setEnableAddToFavoriteButton(true)
                }, { it.printStackTrace() })
        } else {
            compositeDisposable += chatRepository.deleteBan(user.binds?.isUserInBan?.id ?: 1)
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribe({
                    profileUserData.user.binds?.isUserInBan = null
                    viewState.setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                    viewState.setEnableAddToFavoriteButton(true)
                }, { it.printStackTrace() })
        }
    }

    override fun onBlockClick() {
        viewState.showBlockConfirmation()
    }

    override fun onBlockConfirm() {
        val user = profileUserData.user
        if (user.binds?.chatRoomWithMe == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(userId.toInt()))
                .flatMap { chatRepository.chatBann(CreateChatBody(userId.toInt())) }
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribe({
                    profileUserData.user.binds?.isUserInBan = it
                    viewState.setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                    viewState.setEnableAddToFavoriteButton(false)
                }, { it.printStackTrace() })
        } else {
            compositeDisposable += chatRepository.chatBann(CreateChatBody(userId.toInt()))
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribe({
                    profileUserData.user.binds?.isUserInBan = it
                    viewState.setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                    viewState.setEnableAddToFavoriteButton(false)
                }, { it.printStackTrace() })
        }
    }


    private fun String?.loadAvatarNew(): Bitmap? {
        return loadBitmapNew(context)
    }

    private fun isCurrentUser() = userId == appData.getId().toString()

    override fun onRefreshRequest() = loadUserData()

    private fun userAddressRequest(): Completable {
        return userRepository.searchAddress(
            profileUserData.user.address?.getShortAddress() ?: ""
        ).doOnSuccess { profileUserData.setUserShortAddress(it) }
            .ignoreElement().onErrorResumeNext { Completable.complete() }
    }

    private fun userLoadRequest(): Maybe<ProfileUserData> {
        return Maybe.zip(
            userRepository.getUserByIdNew(userId),
            commonRepository.getInterests()
        ) { user, interests ->
            val userInterests = if (user.isHasInterests() && !interests.isNullOrEmpty()) {
                mutableMapOf<InterestNew, MutableList<InterestNew>>().apply {
                    interests.filter { it.parent == 0 }.forEach {
                        val parent = interests.filter { parent -> parent.parent == it.id }
                        parent.let { it1 ->
                            user.interests?.forEach { usIn ->
                                val isUserInterest = it1.find { it2 -> it2.id == usIn }
                                if (isUserInterest != null)
                                    getOrPut(it) { mutableListOf() }.add(isUserInterest)
                            }
                        }
                    }
                }
            } else mutableMapOf()

            ProfileUserData(
                user,
                user.loadUserImage().loadAvatarNew(),
                userInterests
            ).apply { profileUserData = this }
        }
    }

}
