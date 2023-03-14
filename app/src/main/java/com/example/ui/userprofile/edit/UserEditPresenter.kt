package com.example.ui.userprofile.edit

import android.Manifest
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.BuildConfig
import com.example.R
import com.example.data.AppData
import com.example.data.models.*
import com.example.repository.AuthRepository
import com.example.repository.CommonRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withLoadingDialog
import withProgressBarLoadingDialog
import java.io.File
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class UserEditPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val rxPermissions: RxPermissions,
    private val commonRepository: CommonRepository,
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
                                .withProgressBarLoadingDialog(viewState)
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
            compositeFilesDisposable += updatedFilesRequestBody(data)
                .flatMap { userRepository.changeRecommendedFiles(it) }
                .doOnSuccess {
                    it.forEach { res ->
                        appData.updateUserFiles(res)
                    }
                }
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple {
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
        }
    }

    override fun onDeleteFilesClick(data: FileModel) {
        compositeDisposable += userRepository.deleteRecommendedFile(data.id ?: 0)
            .doOnComplete {
                val userFiles = mutableListOf<FileModel>()
                userFiles.addAll(appData.getUserNew().binds?.recommendationFile ?: mutableListOf())
                val file = userFiles.find { x -> x.id == data.id }
                if (file != null) {
                    userFiles.remove(file)
                }
                appData.getUserNew().binds?.recommendationFile = userFiles
            }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.deleteUserFile(data)
            }
    }


    override fun onChangeEmailClick() {
        viewState.showChangeEmail()
    }

    override fun checkPassword(password: String, phone: String) {
        compositeDisposable += userRepository.checkPasswordNew(password)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    viewState.hideEnterPassword()
                }, onComplete = {
                    viewState.hideEnterPassword()
                    checkPhoneIsUnique(phone)
                })
    }

    override fun checkPhoneIsUnique(phone: String) {
        compositeDisposable += userRepository.checkEmailPhone(null, phone)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    viewState.showPhoneNotUnique(phone)
                }, onComplete = {
                    onShowPhoneConfirm(phone)
                })
    }

    override fun onShowPhoneConfirm(phone: String) {
        compositeDisposable += authRepository.registerPhoneResend("personal", phone)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.showPhoneConfirm(phone)
            }
    }

    override fun onAddFileClick() {
        compositeDisposable += rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = {
                    if (it) viewState.showFileSelector()
                    else viewState.showToast(R.string.event_register_file_no_permission)
                })
    }

    override fun onFilePicked(path: String, mimeType: String) {
        compositeDisposable += Single.fromCallable {
            val file = File(path)
            mutableListOf<MultipartBody.Part?>().apply {
                add(textRequestBody(appData.getId().toString(), "user"))
                add(fileRequestBody(file, "file", mimeType))
                add(textRequestBody(file.name, "name"))
            }
        }
            .flatMap { f -> userRepository.uploadRecommendedFile(f).map { it.toFileModel() } }
            .doOnSuccess { newFile ->
                val userFiles = mutableListOf<FileModel>()
                userFiles.addAll(appData.getUserNew().binds?.recommendationFile ?: mutableListOf())
                userFiles.add(newFile)
                appData.getUserNew().binds?.recommendationFile = userFiles
            }
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribe({ newFile ->
                viewState.addNewUserFile(newFile)
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
            .withProgressBarLoadingDialog(viewState)
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
        val userInterests = user.getUserInterests()
        val groups = mutableMapOf<InterestNew, MutableList<UserInterest>>()
        interests?.forEach { interest ->
            val parent = interests.find { parent -> parent.id == interest.parent }
            parent?.let {
                val isUserInterest = userInterests.find { userInterest -> userInterest == interest.id } != null

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
        file.asRequestBody(mimeType.toMediaTypeOrNull())
        // val body = RequestBody.create(mimeType.toMediaTypeOrNull(), file)
        val body = file.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(fieldName, file.name, body)
    }

    private fun textRequestBody(text: String?, fieldName: String): MultipartBody.Part? =
        MultipartBody.Part.createFormData(fieldName, text ?: "")

    private fun updatedFilesRequestBody(data: MutableList<FileModel>): Single<RequestBody> {
        return Single.fromCallable {
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .apply {
                    data.forEachIndexed { index, file ->
                        addFormDataPart("data[$index][id]", file.id.toString())
                        addFormDataPart("data[$index][name]", file.name ?: "")
                        addFormDataPart(
                            "data[$index][showInProfile]",
                            file.showInProfile.toString()
                        )
                    }

                }.build()
        }
    }


    fun canUpdate(can: Boolean) {
        this.withUpdate = can
    }

    fun isWithUpdate(): Boolean {
        return withUpdate
    }
}
