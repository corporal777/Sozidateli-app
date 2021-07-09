package com.example.ui.userprofile.edit

import android.graphics.Bitmap
import android.system.Os.remove
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.BuildConfig
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.data.models.user.User.Companion.FIELD_ATTACHED_FILES
import com.example.repository.CommonRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.AuthValidateUtil
import com.example.util.IMAGE_MAX_SIZE_AVATAR
import com.example.util.PHONE_PERSONAL
import com.example.util.loadBitmap
import com.example.util.rxtakephoto.ResultRotation
import com.example.util.rxtakephoto.RxTakePhoto
import com.google.gson.Gson
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.util.HalfSerializer.onComplete
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
class UserEditPresenter
@Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository,
        private val commonRepository: CommonRepository,
        private val takePhoto: RxTakePhoto
) : BasePresenter<UserEditContract.View>(), UserEditContract.Presenter {

    private val compositeFilesDisposable = CompositeDisposable()
    lateinit var editType: UserEditDataType

    private var isFileEdit = false
    private var isInterestsLoaded = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userNewChangeSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val user = it.value ?: throw RuntimeException("Edit null user")
                    when (editType) {
                        UserEditDataType.MAIN -> viewState.apply {
                            setMainTitle()
                            saveOnClick(true)
                            setMainData(user)
                        }
                        UserEditDataType.PERSONAL -> viewState.apply {
                            setPersonalTitle()
                            compositeDisposable += userRepository.searchAddress(user.address?.getShortAddress()?: "")
                                    .performOnBackgroundOutOnMain()
                                    .subscribe({ add ->
                                        if (add.data?.isNotEmpty() == true)
                                            user.address?.shortAddres = add.data[0].region
                                        //if (BuildConfig.NEW_PROFILE_EDIT) {
                                            setPersonalDataNew(user)
                                        /*} else {
                                            setPersonalData(user)
                                        }*/
                                    }, {
                                        //if (BuildConfig.NEW_PROFILE_EDIT) {
                                            setPersonalDataNew(user)
                                        /*} else {
                                            setPersonalData(user)
                                        }*/
                                    })
                            /*if (BuildConfig.NEW_PROFILE_EDIT) {
                                setPersonalDataNew(user)
                            } else {
                                setPersonalData(user)
                            }*/
                            saveOnClick(true)
                        }
                        UserEditDataType.CONTACTS -> viewState.apply {
                            setContactsTitle()
                            setContactsData(user)
                            saveOnClick(true)
                        }
                        UserEditDataType.PHONE -> viewState.apply {
                            setPhoneTitle()
                            setPhoneData(user)
                            saveOnClick(true)
                        }
                        UserEditDataType.EDUCATION -> viewState.apply {
                            setEducationTitle()
                            setEducationData(user)
                            saveOnClick(true)
                        }
                        UserEditDataType.WORK -> viewState.apply {
                            setWorkTitle()
                            setWorkData(user)
                            saveOnClick(true)
                        }
                        UserEditDataType.INTERESTS -> viewState.apply {
                            setInterestsTitle()
                            getInterests(user)
                            saveOnClick(true)
                        }
                        UserEditDataType.ADDITIONAL_NOTES -> viewState.apply {
                            setAdditionalNotesTitle()
                            setAdditionalNotesData(user)
                            saveOnClick(true)
                        }
                        UserEditDataType.ADDITIONAL_FILES -> viewState.apply {
                            setAdditionalFilesTitle()
                            setAdditionalFilesData(user)
                            saveOnClick(false)
                        }
                    }
                }, {
                    it.printStackTrace()
                    viewState.navigateUp()
                })
    }

    private fun setMainData(user: UserDetail) {
        compositeDisposable += user.image?.uri.loadBitmap()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setMainData(user, it.value)
                }, {
                    it.printStackTrace()
                    viewState.setMainData(user, null)
                })
    }

    override fun onCancelClick() {
        viewState.navigateUp()
    }

    override fun onSaveMainClick(data: MutableMap<String, Any?>) {
        onEditSave(data) {
            appData.userChangeSubject.onNext(appData.getUser().apply {
                user_avatar = it.user_avatar
                user_name = it.user_name
                user_last_name = it.user_last_name
                user_middle_name = it.user_middle_name
                user_name_edited = it.user_name_edited
            }.asOptional())
            true
        }
    }

    override fun onSavePersonalClick(data: MutableMap<String, Any?>) {
        onEditSave(data) {
            compositeFilesDisposable.clear()
            appData.updateUser {
                user_email = it.user_email
                user_email_show = it.user_email_show
                user_phone_work = it.user_phone_work
                user_phone_work_show = it.user_phone_work_show
                user_phone = it.user_phone
                user_phone_show = it.user_phone_show
                user_name = it.user_name
                user_last_name = it.user_last_name
                user_middle_name = it.user_middle_name
                user_gender = it.user_gender
                user_birthday = it.user_birthday
                user_birthday_show = it.user_birthday_show
                user_address = it.user_address
                social_links = it.social_links
                user_short_address = it.user_short_address
                user_address_index = it.user_address_index
                user_address_country = it.user_address_country
                user_address_region = it.user_address_region
                user_address_area = it.user_address_area
                user_address_city = it.user_address_city
                user_address_district = it.user_address_district
                user_address_settlement = it.user_address_settlement
                user_address_street = it.user_address_street
                user_address_house = it.user_address_house
                user_address_flat = it.user_address_flat
                user_notes = it.user_notes
                attached_recomendation_files = it.attached_recomendation_files
                user_social_links_absent = it.user_social_links_absent
                user_phone_work_additional = it.user_phone_work_additional
            }
            true
        }
    }

    override fun onSaveContactsClick(data: MutableMap<String, Any?>) {
        onEditSaveNew(data) {
            appData.userNewChangeSubject.onNext(appData.getUserNew().apply {
                phone = it.phone
                socialLinks = it.socialLinks
                email = it.email
                site = it.site
            }.asOptional())
            true
        }
    }

    override fun onSaveFileClick(data: MutableMap<String, Any?>) {
        onEditSave(data) {
            appData.userChangeSubject.onNext(appData.getUser().apply {
                compositeFilesDisposable.clear()
                attached_recomendation_files = it.attached_recomendation_files
            }.asOptional())
            true
        }
    }

    override fun onSaveEducationClick(educationLevel: Int?, educationsList: List<EducationModel>?, degree: List<AcademicDegreeModel>?) {
        compositeDisposable += userRepository.updateUserEducationScreen(educationLevel, educationsList, degree)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.navigateUp()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })
    }

    override fun onSaveWorkClick(data: WorkExperienceServerModel) {
        compositeDisposable += userRepository.updateWorkExperience(data)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({

                    viewState.navigateUp()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })
    }

    fun getBaseUserState() = appData.hasBaseState
    fun getMaxUserState() = appData.hasMaxState

    override fun onSaveInterestsClick(data: List<InterestNew>) {
        viewState.showLoadingDialog()
        updateUserNew(userRepository.updateProfile(appData.getId(), mapOf(UserDetail.USER_INTERESTS to data.map { item -> item.id }))) {
            it.interests = data.map { item -> item.id?: 0 }
            viewState.hideAllLoadingDialogs()
            false
        }
    }

    override fun onSaveAdditionalNotesClick(notes: String?) {
        onEditSave(mutableMapOf(User.FIELD_USER_NOTES to notes)) {
            appData.userChangeSubject.onNext(appData.getUser().apply {
                user_notes = it.user_notes
            }.asOptional())
            true
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
                false
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
                                }
                                false
                            }
                        }
                    }, {
                        it.printStackTrace()
                        viewState.showUpdateError()
                    })
        }
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
                .flatMapSingle { takePhoto.crop(resultRotation = it, outputMaxWidth = IMAGE_MAX_SIZE_AVATAR, outputMaxHeight = IMAGE_MAX_SIZE_AVATAR, cropMode = CropImageView.CropMode.SQUARE) }
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

    override fun onConfirmPhoneClick(phone: String) {
        viewState.showPhoneConfirm(phone)
    }

    override fun onAddFileClick() {
        viewState.showFileSelector()
    }

    override fun onEditFileClick(file: FileModel) {
        isFileEdit = true
        viewState.setFileEditData(file)
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
                    /*if (BuildConfig.NEW_PROFILE_EDIT) {
                        //viewState.updateFilesList(it.attached_recomendation_files)
                        appData.updateUser {
                            attached_recomendation_files = it.attached_recomendation_files
                        }
                    } else {
                        appData.updateUser {
                            attached_recomendation_files = it.attached_recomendation_files
                        }
                    }*/
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError()
                })
    }

    override fun onFileEditSaveClick() {
        onEditSave(mutableMapOf(
                User.FIELD_ATTACHED_FILES to (appData.getUser().attached_recomendation_files
                        ?: emptyList())
        )) {
            appData.userChangeSubject.onNext(appData.getUser().apply {
                attached_recomendation_files = it.attached_recomendation_files
            }.asOptional())
            true
        }
    }

    override fun onFileEditCancelClick() {
        isFileEdit = false
        viewState.setAdditionalFilesData(appData.getUserNew())
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

    private fun groupUserInterests(user: UserDetail, interests: List<InterestNew>?): Map<InterestNew, List<UserInterest>> {
        val userInterests = user.interests?.map { it } ?: emptyList()
        val groups = mutableMapOf<InterestNew, MutableList<UserInterest>>()
        interests?.forEach { interest ->
            val parent = interests.find { parent -> parent.id == interest.parent }
            parent?.let {
                val isUserInterest = userInterests.find { userInterest -> userInterest == interest.id } != null
                groups.getOrPut(parent) { mutableListOf() }.add(UserInterest(interest, isUserInterest))
            }
        }
        return groups
    }

    private fun onEditSaveNew(data: MutableMap<String, Any?>, onComplete: (UserDetail) -> Boolean) {
        if (data.isEmpty()) {
            viewState.navigateUp()
            return
        }

        updateUserNew(userRepository.updateProfile(appData.getId(), data), onComplete)
    }

    private fun updateUserNew(request: Single<UserDetail>, onComplete: (UserDetail) -> Boolean) {
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    appData.getUserNew().apply {
                        phone = it.phone
                        /*user_phone_confirmed = it.user_phone_confirmed
                        it.user_status?.let { status -> user_status = status }
                        it.user_status_detail?.let { details -> user_status_detail = details }*/
                    }
                    if (onComplete(it))
                        viewState.navigateUp()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })
    }

    private fun onEditSave(data: MutableMap<String, Any?>, onComplete: (User) -> Boolean) {
        if (data.isEmpty()) {
            viewState.navigateUp()
            return
        }

        val avatar = data[User.FIELD_USER_AVATAR] as? Bitmap
        if (avatar != null) {
            if (data.size == 1) {
                compositeDisposable += userRepository.changeUserImage(avatar)
                        .performOnBackgroundOutOnMain()
                        .withLoadingDialog(viewState)
                        .subscribe({
                            viewState.navigateUp()
                        }, {
                            it.printStackTrace()
                            viewState.showUpdateError(it.message)
                        })
                //updateUser(userRepository.changeUserImage(avatar), onComplete)
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
            val updateFiles = data[FIELD_ATTACHED_FILES]
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
                            data.remove(FIELD_ATTACHED_FILES)
                            data.put(FIELD_ATTACHED_FILES, update)
                            updateUser(userRepository.updateUser(data), onComplete)
                        }
            } else {
                updateUser(userRepository.updateUser(data), onComplete)
            }
        }
    }

    private fun updateUser(request: Single<User>, onComplete: (User) -> Boolean) {
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    appData.getUser().apply {
                        user_phone_confirmed = it.user_phone_confirmed
                        it.user_status?.let { status -> user_status = status }
                        it.user_status_detail?.let { details -> user_status_detail = details }
                    }
                    if (onComplete(it))
                        viewState.navigateUp()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })
    }

    private fun fileRequestBody(file: File, fieldName: String, mimeType: String): MultipartBody.Part?{
        val body = RequestBody.create(mimeType.toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(fieldName, file.name, body)
    }

    private fun textRequestBody(text: String?, fieldName: String): MultipartBody.Part? =
            MultipartBody.Part.createFormData(fieldName, text?: "")
}
