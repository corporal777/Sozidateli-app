package com.example.ui.user

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.BuildConfig
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.bodies.CreateChatBody
import com.example.data.models.*
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.data.models.user.UserData
import com.example.repository.ChatRepository
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
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
        private val haChat: HAChat,
        private val eventRepository: EventRepository
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
                    /*if (::profileUserData.isInitialized && it.roomKey == profileUserData.user.chat?.id.toString()) {
                        viewState.apply {
                            profileUserData.user.chat?.isBannedByYou = it.exclude
                            viewState.setSubscribeAction(profileUserData.user.getUserSubscribeAction())
                        }
                    }*/
                }, { it.printStackTrace() })
    }

    private fun loadUserData(withLoading: Boolean) {
        val getUser = if (isCurrentUser()) {
            userRepository.getUserShortNew()
                    .flatMapObservable { appData.userNewChangeSubject }
                    .map { it.value!! }
        } else {
            userRepository.getUserByIdNew(userId).toObservable()
        }
                .performOnBackgroundOutOnMain()
                .flatMapMaybe { user -> user.image?.uri.loadAvatar().map { user to it } }
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
                    profileUserData = if (BuildConfig.NEW_PROFILE_EDIT) {
                        ProfileUserData(
                                it,
                                !BuildConfig.NEW_PROFILE_EDIT
                        )
                    } else {
                        ProfileUserData(
                                it,
                                isCurrentUser()
                        )
                    }
                    compositeDisposable += userRepository.searchAddress(profileUserData.userData.user.address?.getShortAddress()?: "")
                            .performOnBackgroundOutOnMain()
                            .subscribe({ add ->
                                viewState.apply {
                                    if (add.data?.isNotEmpty() == true)
                                        profileUserData.userData.user.address?.shortAddres = add.data[0].region
                                    setUser(profileUserData)
                                    if (!isCurrentUser()) setSubscribeAction(profileUserData.user.getUserSubscribeAction())
                                }
                            }, {
                                viewState.apply {
                                    setUser(profileUserData)
                                    if (!isCurrentUser()) setSubscribeAction(profileUserData.user.getUserSubscribeAction())
                                }
                            })
                }, { it.printStackTrace() })
    }

    private fun getAdditionalData() {
        compositeDisposable += userRepository.getEducationLevel()
                .performOnBackgroundOutOnMain()
                .subscribe({}, { it.printStackTrace() })

        compositeDisposable += userRepository.getSpeciality()
                .performOnBackgroundOutOnMain()
                .subscribe({}, { it.printStackTrace() })

        compositeDisposable += userRepository.getAcademicDegrees()
                .performOnBackgroundOutOnMain()
                .subscribe({}, { it.printStackTrace() })
    }

    private fun groupUserInterests(user: /*User*/UserDetail, interests: List<InterestNew>): MutableMap<InterestNew, MutableList<InterestNew>>? {
        val groups = mutableMapOf<InterestNew, MutableList<InterestNew>>()
        val headers = interests.filter { it.parent == 0 }
        headers.forEach {
            val parent = interests.filter { parent -> parent.parent == it.id }
            parent.let { it1 ->
                user.interests?.forEach { usIn ->
                    val isUserInterest = it1.find { it2 -> it2.id == usIn }
                    if (isUserInterest != null)
                        groups.getOrPut(it) { mutableListOf() }.add(isUserInterest)
                }
            }
        }
        return groups
    }

    override fun onWriteMessageClick() {
        val user = profileUserData.user
        compositeDisposable += chatRepository.createChat(CreateChatBody(user.id))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.openChat(user.fullName, user.image?.uri, it.id.toString())
                }, { it.printStackTrace() })
        /*compositeDisposable += chatRepository.startChat(user.id.toString())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.openChat(user.fullName, user.image?.uri, it.chat_id.toString())
                }, { it.printStackTrace() })*/
    }

    override fun onOrganizationClick(organization: /*Organization*/OrganizationNew) {
        viewState.showOrganization(organization)
    }

    override fun onFileClick(file: /*RecommendationFile*/FileModel) {
        file.uri?.let { viewState.downloadFile(it) }
    }

    override fun onSubscribeClick() {
        compositeDisposable += eventRepository.addToFavorites(AddToFavoriteModel(appData.getId(), AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_SPEAKER, userId.toInt())))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    profileUserData.user.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                    viewState.setSubscribeAction(profileUserData.user.getUserSubscribeAction())
                }
    }

    override fun onUnsubscribeClick() {
        compositeDisposable += eventRepository.deleteFromFavorite(profileUserData.user.binds?.userFavorite?.id.toString())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    profileUserData.user.binds?.userFavorite = null
                    viewState.setSubscribeAction(profileUserData.user.getUserSubscribeAction())
                }
    }

    override fun onUnblockClick() {
        compositeDisposable += chatRepository.createChat(CreateChatBody(userId.toInt()))
                //.flatMapCompletable { chatRepository.deleteBan(it.chat_id.toString()) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setSubscribeAction(profileUserData.user.getUserSubscribeAction())
                }, { it.printStackTrace() })

        /*compositeDisposable += chatRepository.startChat(userId)
                .flatMapCompletable { chatRepository.chatUnban(it.chat_id.toString()) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                   // profileUserData.user.chat?.isBannedByYou = false
                    //profileUserData.user.user_banned = false
                    viewState.setSubscribeAction(profileUserData.user.getUserSubscribeAction())
                }, { it.printStackTrace() })*/
    }

    override fun onBlockClick() {
        viewState.showBlockConfirmation()
    }

    override fun onBlockConfirm() {
        compositeDisposable += chatRepository.createChat(CreateChatBody(userId.toInt()))
                //.flatMapCompletable { chatRepository.chatBann(CreateChatBody(userId.toInt())) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setSubscribeAction(profileUserData.user.getUserSubscribeAction())
                }, { it.printStackTrace() })

        /*compositeDisposable += chatRepository.startChat(userId)
                .flatMapCompletable { chatRepository.chatBan(it.chat_id.toString()) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    //profileUserData.user.chat?.isBannedByYou = true
                   // profileUserData.user.user_banned = true
                    viewState.setSubscribeAction(profileUserData.user.getUserSubscribeAction())
                }, { it.printStackTrace() })*/
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
                //updateUser(userRepository.changeUserImage(avatar), onComplete)
                compositeDisposable += userRepository.changeUserImage(avatar)
                        .performOnBackgroundOutOnMain()
                        .withLoadingDialog(viewState)
                        .subscribe({
                            viewState.navigateUp()
                        }, {
                            it.printStackTrace()
                            viewState.showUpdateError(it.message)
                        })
            } else {
                compositeDisposable += userRepository.changeUserImage(avatar)
                        .performOnBackgroundOutOnMain()
                        .withLoadingDialog(viewState)
                        .subscribe({
                            viewState.navigateUp()
                        }, {
                            it.printStackTrace()
                            viewState.showUpdateError(it.message)
                        })
                /*updateUser(userRepository.changeUserImage(avatar)
                        .flatMap { userRepository.updateUser(data.minus(User.FIELD_USER_AVATAR)) }, onComplete)*/
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

    private fun isCurrentUser() = userId == appData.getId().toString()

    override fun onRefreshRequest() {
        loadUserData(false)
    }
}
