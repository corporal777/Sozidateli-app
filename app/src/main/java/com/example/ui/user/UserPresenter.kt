package com.example.ui.user

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.data.models.user.UserData
import com.example.repository.ChatRepository
import com.example.repository.CommonRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.loadBitmap
import io.reactivex.Maybe
import io.reactivex.Single
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
        private val commonRepository: CommonRepository,
        private val haChat: HAChat
) : BasePresenter<UserContract.View>(), UserContract.Presenter {

    lateinit var userId: String
    private lateinit var profileUserData: ProfileUserData

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadUserData(true)

        viewState.apply {
            if (isCurrentUser()) setProfileTitle()
            else setNoTitle()
        }

        compositeDisposable += haChat.subscribeToExcludeFlagChange()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    if (::profileUserData.isInitialized && it.roomKey == profileUserData.user.chat?.id.toString()) {
                        viewState.apply {
                            profileUserData.user.chat?.isBannedByYou = it.exclude
                            viewState.setSubscribeAction(profileUserData.user.getUserSubscribeAction())
                        }
                    }
                }, { it.printStackTrace() })
    }

    private fun loadUserData(withLoading: Boolean) {
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

        compositeDisposable += commonRepository.getInterests()
                .flatMapObservable { interests ->
                    getUser.map {
                        val user = it.first
                        val avatar = it.second.value
                        UserData(user, avatar, groupUserInterests(user, interests))
                    }
                }
                .performOnBackgroundOutOnMain()
                .let {
                    if (withLoading) it.withLoadingDialog(viewState)
                    else it
                }
                .subscribe({
                    profileUserData = ProfileUserData(
                            it,
                            false
                    )
                    viewState.apply {
                        setUser(profileUserData)
                        if (!isCurrentUser()) setSubscribeAction(profileUserData.user.getUserSubscribeAction())
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
                .subscribe({
                    profileUserData.user.is_in_favorite = true
                    viewState.setSubscribeAction(profileUserData.user.getUserSubscribeAction())
                }, { it.printStackTrace() })
    }

    override fun onUnsubscribeClick() {
        compositeDisposable += userRepository.removeFromFavorite(userId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    profileUserData.user.is_in_favorite = false
                    viewState.setSubscribeAction(profileUserData.user.getUserSubscribeAction())
                }, { it.printStackTrace() })
    }

    override fun onUnblockClick() {
        compositeDisposable += chatRepository.startChat(userId)
                .flatMapCompletable { chatRepository.chatUnban(it.chat_id.toString()) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    profileUserData.user.chat?.isBannedByYou = false
                    profileUserData.user.user_banned = false
                    viewState.setSubscribeAction(profileUserData.user.getUserSubscribeAction())
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
                    profileUserData.user.user_banned = true
                    viewState.setSubscribeAction(profileUserData.user.getUserSubscribeAction())
                }, { it.printStackTrace() })
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

    override fun onEditAdditionalNotesDataClick() {
        viewState.showDataEditor(UserEditDataType.ADDITIONAL_NOTES)
    }

    override fun onEditAdditionalFilesDataClick() {
        viewState.showDataEditor(UserEditDataType.ADDITIONAL_FILES)
    }

    override fun onStatusClick() {
        viewState.showStatus()
    }

    override fun onChangePasswordClick() {
        viewState.showChangePassword()
    }

    override fun onChangePasswordClickConfirm(oldPassword: String, newPassword: String, newPasswordConfirm: String) {
        onEditSave(mapOf(User.FIELD_USER_OLD_PASSWORD to oldPassword, User.FIELD_USER_NEW_PASSWORD to newPassword)) {
            viewState.showPasswordChangeComplete()
            false
        }
    }

    private fun onEditSave(data: Map<String, Any?>, onComplete: (User) -> Boolean) {
        if (data.isEmpty()) {
            viewState.navigateUp()
            return
        }

        val avatar = data[User.FIELD_USER_AVATAR] as? Bitmap
        if (avatar != null) {
            if (data.size == 1) {
                updateUser(userRepository.uploadAvatar(avatar), onComplete)
            } else {
                updateUser(userRepository.uploadAvatar(avatar)
                        .flatMap { userRepository.updateUser(data.minus(User.FIELD_USER_AVATAR)) }, onComplete)
            }
        } else {
            updateUser(userRepository.updateUser(data), onComplete)
        }
    }

    private fun updateUser(request: Single<User>, onComplete: (User) -> Boolean) {
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    appData.getUser().apply {
                        it.user_status?.let { status -> user_status = status }
                        it.user_status_detail?.let { details -> user_status_detail = details }
                    }
                    if (onComplete(it)) viewState.navigateUp()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })
    }

    private fun String?.loadAvatar(): Maybe<Optional<Bitmap>> {
        return loadBitmap()
    }

    private fun isCurrentUser() = userId == appData.getUser().user_id.toString()

    override fun onRefreshRequest() {
        loadUserData(false)
    }
}
