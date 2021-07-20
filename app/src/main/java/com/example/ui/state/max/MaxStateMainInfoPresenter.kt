package com.example.ui.state.max

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.BuildConfig
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.AuthValidateUtil
import com.example.util.IMAGE_MAX_SIZE_AVATAR
import com.example.util.rxtakephoto.ResultRotation
import com.example.util.rxtakephoto.RxTakePhoto
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.io.File
import javax.inject.Inject

@InjectViewState
class MaxStateMainInfoPresenter
@Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository,
        private val takePhoto: RxTakePhoto
): BasePresenter<MaxStateMainInfoContract.View>(), MaxStateMainInfoContract.Presenter {

    var screen: Int = 1
    private var isFileEdit = false
    private val compositeFilesDisposable = CompositeDisposable()
    var isUpdatePhoto = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userNewChangeSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val user = it.value ?: throw RuntimeException("Edit null user")
                    viewState.apply {
                        setPersonalData(user)
                    }
                }, {
                    it.printStackTrace()
                    viewState.navigateUp()
                })
    }

    override fun onClickClose() {
        viewState.navigateUp()
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
            compositeDisposable += userRepository.changeRecommendedFile(it.id ?: 0, mp)
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
                                    site = it.site
                                    socialLinks = it.socialLinks
                                    phone = it.phone
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

    fun getUserData() = appData.getUserNew()

    private fun onEditSaveNew(data: MutableMap<String, Any?>, onComplete: (UserDetail) -> Boolean) {
        if (data.isEmpty()) {
            viewState.navigateUp()
            return
        }

        updateUserNew(userRepository.updateProfile(appData.getId(), data), onComplete)
    }

    override fun onChangeEmailClick() {
        viewState.showChangeEmail()
    }

    override fun onChangeEmailConfirm(email: String) {
        if (AuthValidateUtil.isValidEmail(email)) {
            updateUserNew(userRepository.updateProfile(appData.getId(), mapOf(UserDetail.USER_EMAIL to FieldDetails(value = email)))) {
                it.email?.value = email
                viewState.showChangeEmailComplete(email)
                false
            }
        } else {
            viewState.showUpdateError()
        }
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
                        rFiles.addAll(binds?.recommendationFile?: mutableListOf())
                        rFiles.add(FileModel(id = it.id, user = it.user, mimeType = it.mimeType,
                                size = it.size, name = it.name, uri = it.uri))
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

    override fun updateFiles(data: MutableMap<String, Any?>) {
        onEditSaveNew(data) {
            appData.updateUserNew {
                name = it.name
                middleName = it.middleName
                lastName = it.lastName
                birthday = it.birthday
                gender = it.gender
                notes = it.notes
                address = it.address
                site = it.site
                socialLinks = it.socialLinks
                phone = it.phone
            }
            true
        }
    }

    override fun onAddFileClick() {
        viewState.showFileSelector()
    }

    override fun onFileClick(file: FileModel) {
        file.uri?.let { viewState.downloadFile(it) }
    }

    override fun onEditFileClick(file: FileModel) {
        isFileEdit = true
        viewState.setFileEditData(file)
        viewState.saveOnClick(true)
    }

    override fun onSaveFileClick(data: MutableMap<String, Any?>) {
        onEditSave(data) {
            appData.userNewChangeSubject.onNext(appData.getUserNew().apply {
                compositeFilesDisposable.clear()
                binds?.recommendationFile = it.binds?.recommendationFile
            }.asOptional())
            true
        }
    }

    private fun onEditSave(data: MutableMap<String, Any?>, onComplete: (UserDetail) -> Boolean) {
        /*if (data.isEmpty()) {
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
            val updateFiles = data[User.FIELD_ATTACHED_FILES]
            if (updateFiles != null) {
                val uFiles = if (updateFiles is List<*>)
                    updateFiles as List<RecommendationFile>
                else
                    arrayListOf(updateFiles as RecommendationFile)
                compositeFilesDisposable += appData.userChangeSubject
                        .performOnBackgroundOutOnMain()
                        .subscribeBy {
                            val files = it.value?.attached_recomendation_files
                            val update = arrayListOf<RecommendationFile>()
                            files?.forEach { file ->
                                val up = uFiles.firstOrNull { f -> f.id == file.id }
                                if (up != null) {
                                    update.add(RecommendationFile(id = file.id, name = if (up.newName.isNullOrEmpty()) up.name else up.newName))
                                } else {
                                    if (updateFiles !is List<*>)
                                        update.add(RecommendationFile(id = file.id, name = file.name))
                                }
                            }
                            data.remove(User.FIELD_ATTACHED_FILES)
                            data.put(User.FIELD_ATTACHED_FILES, update)
                            updateUser(userRepository.updateUser(data), onComplete)
                        }
            } else {
                updateUser(userRepository.updateUser(data), onComplete)
            }
        }*/
    }

    private fun updateUser(request: Single<UserDetail>, onComplete: (UserDetail) -> Boolean) {
        /*compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    appData.getUser().apply {
                        user_phone_confirmed = it.user_phone_confirmed
                        it.user_status?.let { status -> user_status = status }
                        it.user_status_detail?.let { details -> user_status_detail = details }
                    }
                    if (onComplete(it))
                        viewState.goToNext()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })*/
    }

    override fun onSaveAdditionalFilesClick(data: MutableMap<String, Any?>) {
        /*onEditSave(data) {
            if (BuildConfig.NEW_PROFILE_EDIT) {
                viewState.updateFilesList(it.attached_recomendation_files)
                appData.updateUser {
                    attached_recomendation_files = it.attached_recomendation_files
                }
            } else {
                appData.updateUser {
                    attached_recomendation_files = it.attached_recomendation_files
                }
            }
            compositeFilesDisposable.clear()
            false
        }*/
    }

    override fun onDeleteFilesClick(data: FileModel) {
        compositeDisposable += userRepository.deleteRecommendedFile(data.id?: 0)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe {
                    appData.userNewChangeSubject.onNext(appData.getUserNew().apply {
                        this.binds?.recommendationFile = this.binds?.recommendationFile?.filter { file -> file.id != data.id }
                    }.asOptional())
                }
    }

    private fun updateUserNew(request: Single<UserDetail>, onComplete: (UserDetail) -> Boolean) {
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    appData.getUserNew().apply {
                        phone = it.phone
                    }
                    if (onComplete(it))
                        viewState.goToNext()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })
    }

    override fun onTakePhotoFromGalleryClick() = takePhoto(takePhoto.takeGalleryImage())
    override fun onTakePhotoFromCameraClick() = takePhoto(takePhoto.takeCameraImage())

    private fun takePhoto(takePhotoRequest: Observable<ResultRotation>) {
        isUpdatePhoto = true
        compositeDisposable += takePhotoRequest
                .firstOrError()
                .flatMap {
                    takePhoto.crop(
                            resultRotation = it,
                            outputMaxWidth = IMAGE_MAX_SIZE_AVATAR,
                            outputMaxHeight = IMAGE_MAX_SIZE_AVATAR,
                            cropMode = CropImageView.CropMode.SQUARE
                    )
                }
                .flatMap { userRepository.changeUserImage(it) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onSuccess = {
                            compositeDisposable += userRepository.checkUserProfileSingle()
                                    .performOnBackgroundOutOnMain()
                                    .subscribeSimple(onSuccess = {})
                            updateUserInternal {
                                image = it
                            }
                            viewState.photoUpdated(it)
                        }
                )
    }

    override fun onRemovePhotoClick() {
        isUpdatePhoto = true
        compositeDisposable += userRepository.deleteImage()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onComplete = {
                            compositeDisposable += userRepository.checkUserProfileSingle()
                                    .performOnBackgroundOutOnMain()
                                    .subscribeSimple(onSuccess = {})
                            updateUserInternal {
                                image = ImageModel(null, null, null, null, null, null)
                            }
                            viewState.photoUpdated(ImageModel(null, null, null, null, null, null))
                        }
                )
    }

    private fun updateUserInternal(update: UserDetail.() -> Unit) = appData.updateUserNew(update)

    private fun fileRequestBody(file: File, fieldName: String, mimeType: String): MultipartBody.Part?{
        val body = RequestBody.create(mimeType.toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(fieldName, file.name, body)
    }

    private fun textRequestBody(text: String?, fieldName: String): MultipartBody.Part? =
            MultipartBody.Part.createFormData(fieldName, text?: "")
}