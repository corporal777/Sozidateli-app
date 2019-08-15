package com.example.ui.user

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Interest
import com.example.data.models.Organization
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.data.models.user.UserInterests
import com.example.repository.ChatRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.HAChat
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserPresenter
@Inject constructor(
        private val appData: AppData,
        private val chatRepository: ChatRepository,
        private val userRepository: UserRepository,
        private val haChat: HAChat
) : BasePresenter<UserContract.View>(), UserContract.Presenter {

    lateinit var userId: String
    private lateinit var user: User

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            showUserMenuButton(!isCurrentUser())
        }

        compositeDisposable += Maybe.zip(
                if (isCurrentUser()) userRepository.getUserFull() else userRepository.getUserById(userId),
                userRepository.getInterests(),
                BiFunction<User, List<Interest>, UserInterests> { user, interests ->
                    return@BiFunction UserInterests(user, groupUserInterests(user, interests))
                }
        )
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    user = it.user
                    if (isCurrentUser()) {
                        viewState.setCurrentUser(it.user, it.interests)
                    } else {
                        viewState.setAnotherUser(it.user, it.interests)
                    }
                }, { it.printStackTrace() })

        compositeDisposable += haChat.subscribeToExcludeFlagChange()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    if (::user.isInitialized && it.roomKey == user.chat?.id.toString()) {
                        viewState.apply {
                            if (it.exclude) setActionUnblock()
                            else setActionSubscribe()
                        }
                    }
                }, { it.printStackTrace() })
    }

    private fun groupUserInterests(user: User, interests: List<Interest>): MutableMap<Interest, MutableList<Interest>>? {
        return user.interests?.let { userInterests ->
            val groups = mutableMapOf<Interest, MutableList<Interest>>()
            userInterests.forEach {
                val key = interests.find { interest -> interest.id == it.parent }
                if (key != null) {
                    val list = groups.getOrPut(key) { mutableListOf() }
                    list.add(it)
                }
            }
            return@let groups
        }
    }

    override fun onWriteMessageClick() {
        compositeDisposable += chatRepository.startChat(user.user_id.toString())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.openChat(user.fullName, user.user_avatar, it.chat_id.toString())
                }, { it.printStackTrace() })
    }

    override fun onOrganizationClick(organization: Organization) {
        viewState.showOrganization(organization)
    }

    override fun onFileClick(file: RecommendationFile) {
        file.url?.let { viewState.downloadFile(it) }
    }

    override fun onSubscribeClick() {
        compositeDisposable += userRepository.addToFavorite(userId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.setActionUnsubscribe() }, { it.printStackTrace() })
    }

    override fun onUnsubscribeClick() {
        compositeDisposable += userRepository.removeFromFavorite(userId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.setActionSubscribe() }, { it.printStackTrace() })
    }

    override fun onUnblockClick() {
        compositeDisposable += chatRepository.startChat(userId)
                .flatMapCompletable { chatRepository.chatUnban(it.chat_id.toString()) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    user.chat?.isBannedByYou = false
                    viewState.setActionSubscribe()
                }, { it.printStackTrace() })
    }

    override fun onBlockClick() {
        viewState.showBlockConfirmation()
    }

    override fun onBlockConfirm() {
        compositeDisposable += chatRepository.startChat(userId)
                .flatMapCompletable { chatRepository.chatBan(it.chat_id.toString()) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    user.chat?.isBannedByYou = true
                    viewState.setActionUnblock()
                }, { it.printStackTrace() })
    }

    override fun onMenuButtonUserClick() {
        if (::user.isInitialized) {
            viewState.showUserMenu(user.chat?.isBannedByYou == true)
        }
    }

    private fun isCurrentUser() = userId == appData.getUser().user_id.toString()
}
