package com.example.ui.event.registration

import android.Manifest
import android.content.ContentResolver
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.models.ApiError
import com.example.data.models.EventFile
import com.example.data.models.EventPassport
import com.example.data.models.EventRegisterFieldData
import com.example.extensions.getFileNameAndExtension
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.rxtakephoto.PermissionNotGrantedException
import com.google.gson.GsonBuilder
import com.tbruyelle.rxpermissions2.RxPermissions
import fileName
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.MaybeSubject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import java.util.*
import javax.inject.Inject

@InjectViewState
class EventRegistrationPresenter
@Inject constructor(
        private val eventRepository: EventRepository,
        private val rxPermissions: RxPermissions,
        private val contentResolver: ContentResolver
) : BasePresenter<EventRegistrationContract.View>(), EventRegistrationContract.Presenter {

    lateinit var eventId: String

    private var hasGroup = false
    private var selectedGroup: String? = null
    private var fieldsData: List<EventRegisterFieldData<*>> = emptyList()
    private var invalidFieldsData: MutableSet<EventRegisterFieldData<*>> = mutableSetOf()
    private var takeFileMaybe: MaybeSubject<Uri>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.enableActionButton(true)

        compositeDisposable += eventRepository.loadEventRegistrationData(eventId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    viewState.apply {
                        val hasForm = it.groups.isNotEmpty() || it.fieldsData.isNotEmpty()
                        hasGroup = it.groupField != null
                        selectedGroup = it.selectedGroup
                        fieldsData = it.fieldsData
                        invalidFieldsData = fieldsData.filter { field -> !field.isValid() }.toMutableSet()
                        setFields(it.event, it.groupField, it.selectedGroup, it.groups, it.fieldsData, hasForm)

                        if (hasForm) {
                            checkDataValid()
                        } else {
                            viewState.showEventRegisterConfirmation()
                        }
                    }
                }
    }

    override fun onDataChange(field: EventRegisterFieldData<*>) {
        if (field.isValid()) invalidFieldsData.remove(field) else invalidFieldsData.add(field)
        checkDataValid()
    }

    override fun onRegisterClick() {
        compositeDisposable += Single.fromCallable {
            val group = selectedGroup
            val fieldsData = fieldsData
            if (group == null && fieldsData.isEmpty()) return@fromCallable "".toRequestBody()

            MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .apply {
                        if (group != null) addFormDataPart("category_id", group)

                        fieldsData.forEach { fieldData ->
                            val key = fieldData.field.id
                            val value = fieldData.value ?: return@forEach

                            when (fieldData) {
                                is EventRegisterFieldData.File -> fieldData.value?.let {
                                    val path = it.path
                                    if (path.scheme?.startsWith("http") != true) {
                                        val name = "${it.name}.${it.extension}"
                                        contentResolver.openInputStream(path)?.buffered()?.use { stream -> stream.readBytes() }?.let { bytes ->
                                            val body = bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                                            addFormDataPart("file[$key]", name, body)
                                        }
                                    }
                                }
                                else -> {
                                    if (value is Iterable<*>) value.forEachIndexed { index, any ->
                                        if (any != null) addFormDataPart("field[$key][$index]", any.toString())
                                    } else {
                                        val data = when (value) {
                                            is EventPassport ->
                                                if (value.isDataComplete()) GsonBuilder().serializeNulls().create().toJson(value)
                                                else null
                                            else -> value.toString()
                                        }
                                        if (data != null) addFormDataPart("field[$key]", data)
                                    }
                                }
                            }
                        }
                    }
                    .build()
        }
                .flatMap { eventRepository.eventRegister(eventId, it) }
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onError = {
                            val group = selectedGroup
                            val fieldsData = fieldsData
                            if (group == null && fieldsData.isEmpty()) viewState.showEventRegisterConfirmation()
                            onReceiveError(it)
                        },
                        onSuccess = {
                            viewState.showSuccessRegister(it.event.isRequireModerate == false)
                        })
    }

    override fun onSuccessCancel() {
        viewState.navigateUp()
    }

    override fun onSuccessGoToList() {
        viewState.showEventLists()
    }

    override fun onSuccessGoToEvent() {
        viewState.showEvent()
    }

    override fun onRegisterCancelClick() {
        viewState.navigateUp()
    }

    override fun onSelectedGroupChange(groupId: String?) {
        selectedGroup = groupId
        checkDataValid()
    }

    private fun checkDataValid() {
        viewState.enableActionButton((!hasGroup || selectedGroup != null) && invalidFieldsData.isEmpty())
    }

    override fun onPersonalDataFileClick(url: String) {
        viewState.openUrl(url)
    }

    override fun onAddFileClick(field: EventRegisterFieldData<EventFile?>) {
        takeFileMaybe?.onComplete()
        compositeDisposable += rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .flatMapMaybe {
                    if (it) {
                        viewState.openFileSelector()
                        MaybeSubject.create<Uri>().apply { takeFileMaybe = this }
                    } else Maybe.error<Uri>(PermissionNotGrantedException())
                }
                .subscribeSimple(
                        onError = {
                            if (it is PermissionNotGrantedException) {
                                viewState.showToast(R.string.event_register_file_no_permission)
                            }
                        }, onNext = { path ->
                    val nameAndExtension = (path.fileName(contentResolver)
                            ?: path.toString()).getFileNameAndExtension()

                    val fileName = nameAndExtension.first
                    val fileExtension = nameAndExtension.second

                    val availableExtensions = field.field.values ?: emptyList()
                    val contains = availableExtensions.find {
                        val availableExtension = it.toLowerCase(Locale.getDefault())
                        availableExtension == fileExtension || fileExtension == "jpg" && availableExtension == "jpeg"
                    } != null

                    if (contains) {
                        field.value = EventFile(path, fileName, fileExtension)
                        viewState.updateFileField(field.field.id)
                    } else {
                        viewState.showWrongFileExtensions(availableExtensions)
                    }
                })
    }

    override fun onFileSelected(path: Uri) {
        takeFileMaybe?.onSuccess(path)
    }

    override fun onFileSelectionCancel() {
        takeFileMaybe?.onComplete()
    }

    override fun onReceiveApiError(apiError: ApiError) {
        super.onReceiveApiError(apiError)
        val toast = when {
            apiError.errors.contains(API_ERROR_ALREADY_APPROVED) -> R.string.event_register_already_approved_error
            apiError.errors.contains(API_ERROR_REGISTRATION_CLOSED) -> R.string.event_register_closed_error
            else -> R.string.event_register_form_request_error
        }

        viewState.showToast(toast)
    }

    companion object {
        private const val API_ERROR_ALREADY_APPROVED = "Registration is approved before"
        private const val API_ERROR_REGISTRATION_CLOSED = "registration is not carried out"
    }
}
