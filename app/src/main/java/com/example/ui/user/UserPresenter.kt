package com.example.ui.user

import com.example.data.AppData
import com.example.data.bodies.CreateChatBody
import com.example.data.models.*
import com.example.repository.ChatRepository
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
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

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadUserData()
    }

    private fun loadUserData() {
        val userRequest =
            if (userId.first() == '@')
                userRepository.getUserByShortName(userId.substring(1, userId.length), true)
            else userRepository.getUserById(userId)

        compositeDisposable += Maybe.zip(userRequest, commonRepository.getInterests()) { user, i ->
            ProfileUserData(user).apply {
                setUserInterests(i)
                profileUserData = this
            }
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showUserHiddenDialog()
                },
                onSuccess = {
                    viewState.apply {
                        setUser(profileUserData)
                        if (profileUserData.user.state?.isRegistered == true) {
                            setSubscribeFavoriteAction(profileUserData.user.getUserSubscribeAction())
                            setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                        }
                    }
                })
    }


    override fun onWriteMessageClick() {
        val user = profileUserData.user
        if (user.binds?.chatRoomWithMe == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(user.id))
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
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
        compositeDisposable += eventRepository.addUserToFavorites(userId)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                profileUserData.user.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                viewState.setSubscribeFavoriteAction(profileUserData.user.getUserSubscribeAction())
                viewState.showAddedToFavoriteDialog()
            }
    }

    override fun onUnsubscribeClick() {
        compositeDisposable += eventRepository.deleteFromFavorites(profileUserData.user.binds?.userFavorite?.id.toString())
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                profileUserData.user.binds?.userFavorite = null
                viewState.setSubscribeFavoriteAction(profileUserData.user.getUserSubscribeAction())
                viewState.showRemovedFromFavoriteDialog()
            }
    }

    override fun onUnblockClick() {
        val user = profileUserData.user
        if (user.binds?.chatRoomWithMe == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(userId.toInt()))
                .flatMapCompletable { chatRepository.deleteBan(user.binds?.isUserInBan?.id ?: 1) }
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
                .subscribe({
                    profileUserData.user.binds?.isUserInBan = null
                    viewState.setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                    viewState.setEnableAddToFavoriteButton(true)
                }, { it.printStackTrace() })
        } else {
            compositeDisposable += chatRepository.deleteBan(user.binds?.isUserInBan?.id ?: 1)
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
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
                .withProgressBarDialogLoading(viewState)
                .subscribe({
                    profileUserData.user.binds?.isUserInBan = it
                    viewState.setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                    viewState.setEnableAddToFavoriteButton(false)
                }, { it.printStackTrace() })
        } else {
            compositeDisposable += chatRepository.chatBann(CreateChatBody(userId.toInt()))
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
                .subscribe({
                    profileUserData.user.binds?.isUserInBan = it
                    viewState.setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                    viewState.setEnableAddToFavoriteButton(false)
                }, { it.printStackTrace() })
        }
    }


    override fun onRefreshRequest() = loadUserData()
}
