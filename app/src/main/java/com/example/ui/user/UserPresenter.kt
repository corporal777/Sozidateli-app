package com.example.ui.user

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Interest
import com.example.data.models.Optional
import com.example.data.models.Organization
import com.example.data.models.ProfileUserData
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.data.models.user.UserData
import com.example.repository.ChatRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.AuthValidateUtil
import com.example.util.CropCircleTransformation
import com.example.util.IMAGE_MAX_SIZE_AVATAR
import com.example.util.loadBitmap
import com.example.util.rxtakephoto.ResultRotation
import com.example.util.rxtakephoto.RxTakePhoto
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
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
        private val haChat: HAChat,
        private val takePhoto: RxTakePhoto
) : BasePresenter<UserContract.View>(), UserContract.Presenter {

    lateinit var userId: String
    private lateinit var profileUserData: ProfileUserData

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            showUserMenuButton(!isCurrentUser())
        }

        val getUser = (if (isCurrentUser()) userRepository.getUserFull() else userRepository.getUserById(userId))
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { user -> user.user_avatar.loadAvatar().map { user to it } }
                .observeOn(Schedulers.io())

        compositeDisposable += Maybe.zip(
                getUser,
                userRepository.getInterests(),
                BiFunction<Pair<User, Optional<Bitmap>>, List<Interest>, UserData> { userBitmapPair, interests ->
                    val user = userBitmapPair.first
                    val avatar = userBitmapPair.second.value
                    return@BiFunction UserData(user, avatar, groupUserInterests(user, interests))
                }
        )
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    profileUserData = ProfileUserData(
                            it,
                            isCurrentUser()
                    )
                    viewState.setUser(profileUserData)
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
        profileUserData.isEditMainData = true
        viewState.setMainDataEditMode(profileUserData.user, profileUserData.avatar, true)
    }

    override fun onEditMainDataCancelClick() {
        profileUserData.isEditMainData = false
        viewState.setMainDataEditMode(profileUserData.user, profileUserData.avatar, false)
    }

    override fun onEditMainSaveClick(data: Map<String, Any?>) {
        onEditSave(data) {
            profileUserData.isEditMainData = false
            viewState.setMainDataEditMode(profileUserData.user, profileUserData.avatar, false)
        }
    }

    override fun onDisabledMainInputInfoClick() {
        viewState.showDisabledMainInputInfo()
    }

    private fun String?.loadAvatar(): Maybe<Optional<Bitmap>> {
        return loadBitmap(listOf(CropCircleTransformation()))
    }

    override fun onEditAvatarClick() {
        viewState.showTakePictureChooser()
    }

    override fun onRemoveAvatarClick() {
        viewState.changeUserAvatar(null)
    }

    override fun onTakePhotoFromCameraRequest() = takePhoto(takePhoto.takeCameraImage())
    override fun onTakePhotoFromGalleryRequest() = takePhoto(takePhoto.takeGalleryImage())

    private fun takePhoto(takePhotoRequest: Observable<ResultRotation>) {
        compositeDisposable += takePhotoRequest
                .flatMapSingle { takePhoto.crop(resultRotation = it, outputMaxWidth = IMAGE_MAX_SIZE_AVATAR, outputMaxHeight = IMAGE_MAX_SIZE_AVATAR) }
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.changeUserAvatar(it)
                }, {
                    it.printStackTrace()
                })
    }

    override fun onEditPersonalDataClick() {
        profileUserData.isEditPersonalData = true
        viewState.setPersonalDataDataEditMode(profileUserData.user, true)
    }

    override fun onEditPersonalDataCancelClick() {
        profileUserData.isEditPersonalData = false
        viewState.setPersonalDataDataEditMode(profileUserData.user, false)
    }

    override fun onEditPersonalDataSaveClick(data: Map<String, Any?>) {
        onEditSave(data) {
            profileUserData.isEditPersonalData = false
            viewState.setPersonalDataDataEditMode(profileUserData.user, false)
        }
    }

    override fun onChangeEmailClick() {
        viewState.showChangeEmail()
    }

    override fun onChangeEmailConfirm(email: String) {
        if (AuthValidateUtil.isValidEmail(email)) {
            updateUser(userRepository.updateUser(mapOf(User.FIELD_USER_EMAIL to email))) {
                viewState.showChangeEmailComplete(email)
            }
        } else {
            viewState.showUpdateError()
        }
    }

    override fun onChangePasswordClick() {
        viewState.showChangePassword()
    }

    override fun onChangePasswordClickConfirm(oldPassword: String, newPassword: String, newPasswordConfirm: String) {
        onEditSave(mapOf(User.FIELD_USER_OLD_PASSWORD to oldPassword, User.FIELD_USER_NEW_PASSWORD to newPassword)) {
            viewState.showPasswordChangeComplete()
        }
    }

    override fun onEditEducationClick() {
        profileUserData.isEditEducationData = true
        viewState.setEducationDataDataEditMode(profileUserData.user, true)
    }

    private fun onEditSave(data: Map<String, Any?>, onComplete: () -> Unit) {
        if (data.isEmpty()) {
            onComplete()
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

    private fun updateUser(request: Single<User>, onComplete: () -> Unit) {
        compositeDisposable += request.performOnBackgroundOutOnMain()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapMaybe { user -> user.user_avatar.loadAvatar().map { user to it } }
                .observeOn(Schedulers.io())
                .withLoadingDialog(viewState)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    profileUserData.user = it.first
                    profileUserData.avatar = it.second.value
                    onComplete()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })
    }

    private fun isCurrentUser() = userId == appData.getUser().user_id.toString()
}
