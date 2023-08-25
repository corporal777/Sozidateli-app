package com.example.ui.userprofile.edit

import android.Manifest
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.data.models.*
import com.example.repository.AuthRepository
import com.example.repository.CommonRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Maybe
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
import withProgressBarLoadingDialog
import java.io.File
import javax.inject.Inject

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
    private var withUpdate = true


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setPlaceholder(editType)

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
                            saveOnClick(true)
                            setPersonalData(user, appData.getStateValue())
                        }
                        UserEditDataType.CONTACTS -> viewState.apply {
                            setContactsTitle()
                            setContactsData(user)
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
        compositeDisposable += userRepository.updateProfile(appData.getId(), data)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onSuccess = {
                    viewState.navigateUp()
                })
    }

    override fun onSaveInterestsClick(data: List<InterestNew>) {
        compositeDisposable += userRepository.updateProfile(
            appData.getId(),
            mapOf(UserDetail.USER_INTERESTS to data.map { item -> item.id })
        )
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onSuccess = {
                    viewState.navigateUp()
                })
    }


    override fun onSavePersonalDataClick(data: MutableList<FileModel>, d: MutableMap<String, Any?>) {
        compositeDisposable += Completable.defer {
            if (data.isEmpty()) Completable.complete()
            else {
                updatedFilesRequestBody(data)
                    .flatMap { userRepository.changeRecommendedFiles(it) }
                    .doOnSuccess { it.forEach { res -> appData.updateUserFiles(res) } }
                    .ignoreElement()
            }
        }
            .andThen(userRepository.updateProfile(appData.getId(), d))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onSuccess = {
                    viewState.navigateUp()
                })
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
            .map { newFile -> appData.updateFilesWithAdd(newFile) }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.addUserFile(it, appData.getUserNew().filesCount) }
            )
    }

    override fun onDeleteFilesClick(file: FileModel) {
        compositeDisposable += userRepository.deleteRecommendedFile(file.id?.toInt()?:0)
            .andThen(Maybe.just(appData.updateFilesWithDelete(file)))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.deleteUserFile(it, appData.getUserNew().filesCount ) }
            )
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
        compositeDisposable += commonRepository.getInterests()
            .map { groupUserInterests(user, it) }
            .performOnBackgroundOutOnMain()
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
                val isUserInterest =
                    userInterests.find { userInterest -> userInterest == interest.id } != null

                groups.getOrPut(parent) { mutableListOf() }
                    .add(UserInterest(interest, isUserInterest))
            }
        }
        return groups
    }


    private fun fileRequestBody(file: File, field: String, mimeType: String): MultipartBody.Part? {
        file.asRequestBody(mimeType.toMediaTypeOrNull())
        // val body = RequestBody.create(mimeType.toMediaTypeOrNull(), file)
        val body = file.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(field, file.name, body)
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

    fun getBaseUserState() = appData.hasBaseState
    fun getMaxUserState() = appData.hasMaxState

    fun canUpdate(can: Boolean) {
        this.withUpdate = can
    }

    fun isWithUpdate(): Boolean {
        return withUpdate
    }
}
