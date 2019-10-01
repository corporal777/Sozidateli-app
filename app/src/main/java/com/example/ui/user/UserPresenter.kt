package com.example.ui.user

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.data.models.user.UserData
import com.example.repository.ChatRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.CropCircleTransformation
import com.example.util.loadBitmap
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
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
    private lateinit var profileUserData: ProfileUserData

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            showUserMenuButton(!isCurrentUser())
        }

        val getUser = if (isCurrentUser()) {
            userRepository.getUserFull()
                    .flatMapObservable { appData.userChangeSubject }
                    .map { it.value!! }
        } else {
            userRepository.getUserById(userId).toObservable()
        }
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapMaybe { user -> user.user_avatar.loadAvatar().map { user to it } }
                .observeOn(Schedulers.io())

        compositeDisposable += userRepository.getInterests()
                .flatMapObservable { interests ->
                    getUser.map {
                        val user = it.first
                        val avatar = it.second.value
                        UserData(user, avatar, groupUserInterests(user, interests))
                    }
                }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    profileUserData = ProfileUserData(
                            it,
                            isCurrentUser()
                    )
                    viewState.apply {
                        setUser(profileUserData)
                    }
                }, { it.printStackTrace() })

        compositeDisposable += haChat.subscribeToExcludeFlagChange()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    if (::profileUserData.isInitialized && it.roomKey == profileUserData.user.chat?.id.toString()) {
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
        val user = profileUserData.user
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
                    profileUserData.user.chat?.isBannedByYou = false
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
                    profileUserData.user.chat?.isBannedByYou = true
                    viewState.setActionUnblock()
                }, { it.printStackTrace() })
    }

    override fun onMenuButtonUserClick() {
        if (::profileUserData.isInitialized) {
            viewState.showUserMenu(profileUserData.user.chat?.isBannedByYou == true)
        }
    }

    override fun onEditMainDataClick() {
        viewState.showDataEditor(UserEditDataType.MAIN)
    }

    override fun onEditPersonalDataClick() {
        viewState.showDataEditor(UserEditDataType.PERSONAL)
    }

    override fun onEditEducationClick() {
        viewState.showDataEditor(UserEditDataType.EDUCATION)
    }

    override fun onEditWorkClick() {
        viewState.showDataEditor(UserEditDataType.WORK)
    }

    override fun onEditInterestsClick() {
        viewState.showDataEditor(UserEditDataType.INTERESTS)
    }

    override fun onEditAdditionalDataClick() {
        viewState.showDataEditor(UserEditDataType.ADDITIONAL)
    }

    override fun onStatusClick() {
        viewState.showStatus()
    }

    private fun String?.loadAvatar(): Maybe<Optional<Bitmap>> {
        return loadBitmap(listOf(CropCircleTransformation()))
    }

    private fun isCurrentUser() = userId == appData.getUser().user_id.toString()
}
