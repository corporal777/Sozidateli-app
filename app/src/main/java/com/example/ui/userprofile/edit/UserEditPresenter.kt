package com.example.ui.userprofile.edit

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.BuildConfig
import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.models.*
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.data.models.user.User.Companion.FIELD_ATTACHED_FILES
import com.example.repository.AuthRepository
import com.example.repository.CommonRepository
import com.example.repository.UserRepository
import com.example.ui.auth.register.email.finishregister.FinishRegisterPresenter
import com.example.ui.base.BasePresenter
import com.example.ui.userSessions.UserSessionsContract
import com.example.util.AuthValidateUtil
import com.example.util.IMAGE_MAX_SIZE_AVATAR
import com.example.util.loadBitmap
import com.example.util.phoneToServer
import com.example.util.rxtakephoto.ResultRotation
import com.example.util.rxtakephoto.RxTakePhoto
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withLoadingDialog
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class UserEditPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val commonRepository: CommonRepository,
    private val takePhoto: RxTakePhoto
) : BasePresenter<UserEditContract.View>(appData), UserEditContract.Presenter {

    private val compositeFilesDisposable = CompositeDisposable()
    private val timerCompositeDisposable = CompositeDisposable()
    lateinit var editType: UserEditDataType

    private var isFileEdit = false
    private var isInterestsLoaded = false
    private var mDy = 0f
    private var withUpdate = true

    override fun attachView(view: UserEditContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(mDy)
    }


    override fun changeAppBarElevation(value: Int) {
        mDy = abs(value / 10f)
        viewState.setAppBarElevation(mDy)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(mDy)
        compositeDisposable += appData.userNewChangeSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.navigateUp()
                },
                onNext = {
                    val user = it.value ?: throw RuntimeException("Edit null user")
                    when (editType) {
                        UserEditDataType.PERSONAL -> viewState.apply {
                            setPersonalTitle()
                            compositeDisposable += userRepository.searchAddress(
                                user.address?.getShortAddress() ?: ""
                            )
                                .performOnBackgroundOutOnMain()
                                .subscribeSimple(
                                    onError = { t ->
                                        t.printStackTrace()
                                        setPersonalData(
                                            user,
                                            if (appData.hasMaxState && appData.hasBaseState) "Максимальный" else "Минимальный"
                                        )
                                    },
                                    onSuccess = { add ->
                                        if (add.data?.isNotEmpty() == true)
                                            user.address?.shortAddres = add.data[0].region
                                        setPersonalData(
                                            user,
                                            if (appData.hasMaxState && appData.hasBaseState) "Максимальный" else "Минимальный"
                                        )

                                    })
                            saveOnClick(true)
                        }
                        UserEditDataType.CONTACTS -> viewState.apply {
                            setContactsTitle()
                            setContactsData(user)
                            saveOnClick(true)
                        }
                        UserEditDataType.EDUCATION -> viewState.apply {
                            setEducationTitle()
                            saveOnClick(true)
                        }
                        UserEditDataType.WORK -> viewState.apply {
                            setWorkTitle()
                            saveOnClick(true)
                        }
                        UserEditDataType.INTERESTS -> viewState.apply {
                            setInterestsTitle()
                            getInterests(user)
                            saveOnClick(true)
                        }
                    }
                })
    }


    override fun onSaveContactsClick(data: MutableMap<String, Any?>) {
        onEditSaveNew(data) {
            appData.userNewChangeSubject.onNext(appData.getUserNew().apply {
                phone = it.phone
                contactInformation.socialLinks = it.contactInformation.socialLinks
                email = it.email
                contactInformation.site = it.contactInformation.site
            }.asOptional())
            true
        }
    }

    fun getBaseUserState() = appData.hasBaseState
    fun getMaxUserState() = appData.hasMaxState

    override fun onSaveInterestsClick(data: List<InterestNew>) {
        updateUserNew(
            userRepository.updateProfile(
                appData.getId(),
                mapOf(UserDetail.USER_INTERESTS to data.map { item -> item.id })
            )
        ) {
            it.interests = data.map { item -> item.id ?: 0 }
            false
        }
    }


    override fun updateFiles(data: MutableList<FileModel>, d: MutableMap<String, Any?>) {
        if (data.isEmpty()) {
            onEditSaveNew(d) {
                appData.updateUserNew {
                    name = it.name
                    middleName = it.middleName
                    lastName = it.lastName
                    birthday = it.birthday
                    gender = it.gender
                    notes = it.notes
                }
                true
            }
        } else {
            val files = data
            val it = files.first()
            val mp = mutableListOf<MultipartBody.Part?>()
            mp.add(textRequestBody(it.name, "name"))
            mp.add(textRequestBody(it.showInProfile.toString(), "showInProfile"))
            compositeDisposable += userRepository.changeRecommendedFile(it.id ?: 0, mp)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ res ->
                    appData.updateUserNew {
                        binds?.recommendationFile?.forEach { file ->
                            if (file.id == res.id)
                                file.name = res.name
                        }
                    }
                    if (files.size > 1) {
                        files.remove(it)
                        updateFiles(files, d)
                    } else {
                        onEditSaveNew(d) {
                            appData.updateUserNew {
                                name = it.name
                                middleName = it.middleName
                                lastName = it.lastName
                                birthday = it.birthday
                                gender = it.gender
                                notes = it.notes
                            }
                            true
                        }
                    }
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError()
                })
        }
    }

    override fun onDeleteFilesClick(data: FileModel) {
        compositeDisposable += userRepository.deleteRecommendedFile(data.id ?: 0)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribe {
                appData.userNewChangeSubject.onNext(appData.getUserNew().apply {
                    this.binds?.recommendationFile =
                        this.binds?.recommendationFile?.filter { file -> file.id != data.id }
                }.asOptional())
            }
    }

    override fun onDisabledMainInputInfoClick() {
        viewState.showDisabledMainInputInfo()
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
            .flatMapSingle {
                takePhoto.crop(
                    resultRotation = it,
                    outputMaxWidth = IMAGE_MAX_SIZE_AVATAR,
                    outputMaxHeight = IMAGE_MAX_SIZE_AVATAR,
                    cropMode = CropImageView.CropMode.SQUARE
                )
            }
            .performOnBackgroundOutOnMain()
            .subscribe({
                viewState.changeUserAvatar(it)
            }, {
                it.printStackTrace()
            })
    }

    override fun onChangeEmailClick() {
        viewState.showChangeEmail()
    }

    override fun checkPassword(password: String, phone: String) {
        compositeDisposable += userRepository.checkPasswordNew(password)
            .andThen(Completable.defer { userRepository.checkEmailPhone(null, phone) })
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    viewState.apply {
                        hideEnterPassword()
                        showPhoneNotUnique(phone)
                    }

                }, onComplete = {
                    viewState.apply {
                        hideEnterPassword()
                        showPhoneConfirm(phone)
                    }
                })
    }

    override fun onConfirmPhoneClick(phone: String) {
        compositeDisposable += userRepository.checkEmailPhone(null, phone)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    viewState.showPhoneNotUnique(phone)
                }, onComplete = {
                    viewState.showPhoneConfirm(phone)
                })
    }

    override fun onAddFileClick() {
        viewState.showFileSelector()
    }

    override fun onEditFileClick(file: FileModel) {
        isFileEdit = true
        viewState.saveOnClick(true)
    }

    override fun onFilePicked(path: String, mimeType: String) {
        val file = File(path)
        val mp = mutableListOf<MultipartBody.Part?>()
        mp.add(textRequestBody(appData.getId().toString(), "user"))
        mp.add(fileRequestBody(file, "file", mimeType))
        mp.add(textRequestBody(file.name, "name"))
        compositeDisposable += userRepository.uploadRecommendedFile(mp)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribe({
                appData.userNewChangeSubject.onNext(appData.getUserNew().apply {
                    val rFiles = mutableListOf<FileModel>()
                    rFiles.addAll(binds?.recommendationFile ?: mutableListOf())
                    rFiles.add(
                        FileModel(
                            id = it.id, user = it.user, mimeType = it.mimeType,
                            size = it.size, name = it.name, uri = it.uri
                        )
                    )
                    if (BuildConfig.NEW_PROFILE_EDIT) {
                        viewState.updateFilesList(rFiles)
                        appData.updateUserNew {
                            binds?.recommendationFile = rFiles
                        }
                    } else {
                        appData.updateUserNew {
                            binds?.recommendationFile = rFiles
                        }
                    }
                }.asOptional())
            }, {
                it.printStackTrace()
                viewState.showUpdateError()
            })
    }


    override fun onFileEditCancelClick() {
        isFileEdit = false
    }

    override fun onNavigateUpRequest() {
        when {
            isFileEdit -> onFileEditCancelClick()
            else -> viewState.navigateUpChecked()
        }
    }

    override fun onFileClick(file: FileModel) {
        file.uri?.let { viewState.downloadFile(it) }
    }

    private fun getInterests(user: UserDetail) {
        if (isInterestsLoaded) return
        compositeDisposable += userRepository.getInterestsList(null)
            .map { groupUserInterests(user, it.data) }
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribe({
                viewState.setInterestsData(it)
                isInterestsLoaded = true
            }, {
                it.printStackTrace()
            })
    }

    private fun groupUserInterests(
        user: UserDetail,
        interests: List<InterestNew>?
    ): Map<InterestNew, List<UserInterest>> {
        val userInterests = user.interests?.map { it } ?: emptyList()
        val groups = mutableMapOf<InterestNew, MutableList<UserInterest>>()
        interests?.forEach { interest ->
            val parent = interests.find { parent -> parent.id == interest.parent }
            parent?.let {
                val isUserInterest =
                    userInterests.find { userInterest -> userInterest == interest.id } != null
                groups.getOrPut(parent) { mutableListOf() }
                    .add(UserInterest(interest, isUserInterest))
            }
        }
        return groups
    }

    private fun onEditSaveNew(data: MutableMap<String, Any?>, onComplete: (UserDetail) -> Boolean) {
        if (data.isEmpty()) {
            viewState.navigateUp()
            return
        }

        updateUserNew(userRepository.updateUserProfile(appData.getId(), data), onComplete)
    }

    private fun updateUserNew(request: Single<UserDetail>, onComplete: (UserDetail) -> Boolean) {
        compositeDisposable += request
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribe({
                appData.getUserNew().apply {
                    phone = it.phone
                }
                compositeDisposable += userRepository.checkUserProfileSingle()
                    .performOnBackgroundOutOnMain()
                    .subscribe({ state ->
                        if (onComplete(it))
                            viewState.navigateUp()
                    }, { error ->
                        error.printStackTrace()
                        if (onComplete(it))
                            viewState.navigateUp()
                    })
            }, {
                it.printStackTrace()
                viewState.showUpdateError(it.message)
            })
    }

    private fun fileRequestBody(
        file: File,
        fieldName: String,
        mimeType: String
    ): MultipartBody.Part? {
        val body = RequestBody.create(mimeType.toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(fieldName, file.name, body)
    }

    private fun textRequestBody(text: String?, fieldName: String): MultipartBody.Part? =
        MultipartBody.Part.createFormData(fieldName, text ?: "")

    fun canUpdate(can: Boolean) {
        this.withUpdate = can
    }

    fun isWithUpdate(): Boolean {
        return withUpdate
    }
}
