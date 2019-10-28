package com.example.ui.request

import android.Manifest
import android.content.ContentResolver
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.models.*
import com.example.extensions.getFileNameAndExtension
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.rxtakephoto.PermissionNotGrantedException
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.tbruyelle.rxpermissions2.RxPermissions
import fileName
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.BiFunction
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
class RequestPresenter
@Inject constructor(
        private val eventRepository: EventRepository,
        private val rxPermissions: RxPermissions,
        private val contentResolver: ContentResolver
) : BasePresenter<RequestContract.View>(), RequestContract.Presenter {

    lateinit var eventId: String

    private var selectedGroup: String? = null
    private var fieldsData: List<RegisterEventFieldData<*>> = emptyList()
    private var invalidFieldsData: MutableSet<RegisterEventFieldData<*>> = mutableSetOf()
    private var takeFileMaybe: MaybeSubject<Uri>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.enableActionButton(true)

        val loadFields = eventRepository.getEventRegisterField(eventId)
        val loadRegister = eventRepository.getEventRegister(eventId)

        compositeDisposable += Single.zip(loadFields, loadRegister, BiFunction<RegisterFieldsData, EventRegisterResponse, Pair<RegistrationEvent, List<EventGroup>>> { fields, registration ->
            val findRegistrationDataValue: (RegisterEventField) -> JsonElement? = { field -> registration.fields?.find { field.id == it?.id }?.value }

            val fieldsData = fields.fields?.map { field ->
                when (field.type) {
                    RegisterEventField.Type.STRING,
                    RegisterEventField.Type.TEXT_AREA,
                    RegisterEventField.Type.NUMBER -> RegisterEventFieldData.String(field, parseRegistrationData<String>(findRegistrationDataValue(field)))
                    RegisterEventField.Type.DATE,
                    RegisterEventField.Type.DATETIME -> RegisterEventFieldData.Date(field, parseRegistrationData<String>(findRegistrationDataValue(field)))
                    RegisterEventField.Type.CHECKBOX -> RegisterEventFieldData.Checkbox(field, parseRegistrationData<Set<String>>(findRegistrationDataValue(field)))
                    RegisterEventField.Type.SELECT_BOX -> RegisterEventFieldData.SelectBox(field, parseRegistrationData<String>(findRegistrationDataValue(field)))
                    RegisterEventField.Type.RADIO_BOX -> RegisterEventFieldData.RadioBox(field, parseRegistrationData<String>(findRegistrationDataValue(field)))
                    RegisterEventField.Type.FILE -> RegisterEventFieldData.File(field, parseRegistrationData(findRegistrationDataValue(field), EventFile.Deserializer()))
                    RegisterEventField.Type.BOOLEAN -> RegisterEventFieldData.Boolean(field, parseRegistrationData<Boolean>(findRegistrationDataValue(field)))
                    RegisterEventField.Type.PASSPORT -> RegisterEventFieldData.Passport(field, parseRegistrationData<EventPassport>(findRegistrationDataValue(field)))
                }
            } ?: emptyList()

            selectedGroup = registration.group_id
            this.fieldsData = fieldsData
            this.invalidFieldsData = fieldsData.filter { field -> !field.isValid() }.toMutableSet()

            registration.event to (fields.groups ?: emptyList())
        })
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple({
                    it.printStackTrace()
                }) {
                    viewState.apply {
                        setFields(it.first, selectedGroup, it.second, fieldsData)
                        checkDataValid()
                    }
                }
    }

    private inline fun <reified T> parseRegistrationData(value: JsonElement?, deserializer: JsonDeserializer<T>? = null): T? {
        if (value == null) return null
        return GsonBuilder()
                .apply {
                    if (deserializer != null) registerTypeAdapter(T::class.java, deserializer)
                }
                .create()
                .fromJson(value, T::class.java)
    }

    override fun onDataChange(field: RegisterEventFieldData<*>) {
        if (field.isValid()) invalidFieldsData.remove(field) else invalidFieldsData.add(field)
        checkDataValid()
    }

    override fun onRegisterClick() {
        compositeDisposable += Single.fromCallable {
            MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .apply {
                        val group = selectedGroup
                        if (group != null) addFormDataPart("category_id", group)

                        fieldsData.forEach { fieldData ->
                            val key = fieldData.field.id
                            val value = fieldData.value ?: return@forEach

                            when (fieldData) {
                                is RegisterEventFieldData.File -> fieldData.value?.let {
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
                    }.build()
        }
                .flatMap { eventRepository.eventRegister(eventId, it) }
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onError = {
                            it.printStackTrace()
                        },
                        onApiError = { apiError ->
                            val toast = when {
                                apiError.errors.contains(API_ERROR_ALREADY_APPROVED) -> R.string.event_register_already_approved_error
                                else -> R.string.event_register_form_request_error
                            }

                            viewState.showToast(toast)
                        },
                        onSuccess = {

                        })
    }

    override fun onSelectedGroupChange(groupId: String?) {
        selectedGroup = groupId
        checkDataValid()
    }

    private fun checkDataValid() {
        viewState.enableActionButton(selectedGroup != null && invalidFieldsData.isEmpty())
    }

    override fun onPersonalDataFileClick(url: String) {
        viewState.openUrl(url)
    }

    override fun onAddFileClick(field: RegisterEventFieldData<EventFile?>) {
        takeFileMaybe?.onComplete()
        compositeDisposable += rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .flatMapMaybe {
                    if (it) {
                        viewState.openFileSelector()
                        MaybeSubject.create<Uri>().apply { takeFileMaybe = this }
                    } else Maybe.error<Uri>(PermissionNotGrantedException())
                }
                .subscribeSimple { path ->
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
                }
    }

    override fun onFileSelected(path: Uri) {
        takeFileMaybe?.onSuccess(path)
    }

    override fun onFileSelectionCancel() {
        takeFileMaybe?.onComplete()
    }

    override fun onReceiveError(error: Throwable) {
        super.onReceiveError(error)
        if (error is PermissionNotGrantedException) {
            viewState.showToast(R.string.event_register_file_no_permission)
        }
    }

    companion object {
        private const val API_ERROR_ALREADY_APPROVED = "Registration is approved before"
    }
}
