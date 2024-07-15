package com.example.ui.userprofile.edit.maindata

import android.Manifest
import android.os.Build
import com.example.R
import com.example.data.AppData
import com.example.data.models.*
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import performOnBackgroundOutOnMain
import withCustomLoading
import java.io.File
import javax.inject.Inject

@InjectViewState
class EditMainDataPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val rxPermissions: RxPermissions,
) : BasePresenter<EditMainDataContract.View>(appData), EditMainDataContract.Presenter {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setPlaceholder()

        compositeDisposable += Maybe.defer { Maybe.just(appData.getUser()) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { viewState.navigateUp() },
                onSuccess = { user ->
                    viewState.setPersonalData(user, appData.getStateValue())
                })
    }


    override fun onSavePersonalDataClick(data: List<FileModel>, d: Map<String, Any?>) {
        compositeDisposable += Completable.defer {
            if (data.isEmpty()) Completable.complete()
            else updatedFilesRequestBody(data)
                .flatMap { userRepository.changeRecommendedFiles(it) }
                .doOnSuccess { it.forEach { res -> appData.updateUserFiles(res) } }
                .ignoreElement()
        }
            .andThen(userRepository.updateUserProfile(appData.getId(), d))
            .flatMap { userRepository.checkUserProfileSingle() }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onSuccess = { viewState.navigateUp() }
            )
    }


    override fun onAddFileClick() {
        compositeDisposable += Observable.defer {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                rxPermissions.request(Manifest.permission.READ_MEDIA_IMAGES)
            else rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
        }.subscribeSimple(
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
            .withUploadFileLoading()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.addUserFile(it, appData.getUser().filesCount) }
            )
    }

    override fun onDeleteFilesClick(file: FileModel) {
        compositeDisposable += userRepository.deleteRecommendedFile(file.id?.toInt() ?: 0)
            .andThen(Maybe.just(appData.updateFilesWithDelete(file)))
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.deleteUserFile(it, appData.getUser().filesCount) }
            )
    }


    override fun onFileClick(file: FileModel) {
        file.uri?.let { viewState.downloadFile(it) }
    }


    private fun fileRequestBody(file: File, field: String, mimeType: String): MultipartBody.Part? {
        // val body = RequestBody.create(mimeType.toMediaTypeOrNull(), file)
        val body = file.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(field, file.name, body)
    }

    private fun textRequestBody(text: String?, fieldName: String): MultipartBody.Part? =
        MultipartBody.Part.createFormData(fieldName, text ?: "")

    private fun updatedFilesRequestBody(data: List<FileModel>): Single<RequestBody> {
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


    private fun <T> Single<T>.withUploadFileLoading(): Single<T> {
        val loadingDisposable = Completable.complete()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { viewState.showFileUploadLoading() }
            .doOnDispose { viewState.hideFileUploadLoading() }
            .subscribe()
        val actionHide = Action {
            if (loadingDisposable.isDisposed) viewState.hideFileUploadLoading()
            else loadingDisposable.dispose()
        }

        fun <T> actionConsumer() = Consumer<T> {
            if (loadingDisposable.isDisposed) viewState.hideFileUploadLoading()
            else loadingDisposable.dispose()
        }
        return this.doFinally(actionHide)
            .doOnDispose(actionHide)
            .doOnSuccess(actionConsumer())
            .doOnError(actionConsumer())
    }
}
