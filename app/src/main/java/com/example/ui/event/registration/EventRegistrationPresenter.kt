package com.example.ui.event.registration

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.core.net.toFile
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.*
import com.example.data.models.EventFormResultModel.Companion.EVENT_FORM_RESULT_FORM_ID
import com.example.data.models.EventFormResultModel.Companion.EVENT_FORM_RESULT_USER_ID
import com.example.extensions.getFileNameAndExtension
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.FileUtils.getDataColumn
import com.example.util.rxtakephoto.PermissionNotGrantedException
import com.google.gson.JsonElement
import com.tbruyelle.rxpermissions2.RxPermissions
import fileName
import fromJson
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.MaybeSubject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withLoadingDialog
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject

@InjectViewState
class EventRegistrationPresenter
@Inject constructor(
    private val eventData: UserEventData,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val rxPermissions: RxPermissions,
    private val contentResolver: ContentResolver,
    private val context: Context,
    private val appData: AppData
) : BasePresenter<EventRegistrationContract.View>(appData), EventRegistrationContract.Presenter {

    lateinit var eventId: String
    private val oldFilesList = arrayListOf<EventRegisterFieldData<*>>()

    private var hasGroup = false
    private var selectedGroup: String? = null
    private var fieldsData: List<EventRegisterFieldData<*>> = emptyList()
    private var invalidFieldsData: MutableSet<EventRegisterFieldData<*>> = mutableSetOf()
    private var takeFileMaybe: MaybeSubject<Uri>? = null
    private var formId: Int = 0
    private var approvingMode: String? = null
    lateinit var cleanResult: EventRegisterData


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.enableActionButton(true)

        compositeDisposable += eventRepository.getEventDetailForRegister(eventId)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { event ->
                    viewState.showProgressBarLoadingDialog()
                    approvingMode = event.state?.registration?.approvingMode
                    val eventData = getEventData(event)
                    if (event.state?.registration?.formEnabled == true) {
                        val form = event.binds?.form?.firstOrNull { it.type == EventFormModel.Type.PARTICIPATION }
                        eventData.registrationHeadline = form?.title
                        eventData.registrationSubtitle = form?.subtitle
                        val fieldsList = mapFields(form?.fields)
                        formId = form?.id ?: 0

                        compositeDisposable += eventRepository.getEventFormResult(
                            mapOf(
                                EVENT_FORM_RESULT_FORM_ID to formId,
                                EVENT_FORM_RESULT_USER_ID to appData.getId()
                            )
                        )
                            .performOnBackgroundOutOnMain()
                            .subscribeSimple { fieldsResult ->
                                //set clean data
                                var fieldsResultList = mapFieldsResult(
                                    fieldsResult.data.firstOrNull { it.form == formId }?.fields,
                                    fieldsList
                                )
                                var fieldsDataApi = createFieldsData(
                                    fieldsList,
                                    fieldsResultList
                                )
                                cleanResult = EventRegisterData(
                                    eventData, null, null,
                                    listOf(EventGroup("", "")),
                                    fieldsDataApi ?: emptyList()
                                )

                                compositeDisposable += eventRepository.getEventFormResultDraft(
                                    formId,
                                    emptyMap()
//                                    mapOf(
//                                        EVENT_FORM_RESULT_FORM_ID to formId,
//                                        EVENT_FORM_RESULT_USER_ID to appData.getId()
//                                    )
                                )
                                    .performOnBackgroundOutOnMain()
                                    .subscribeSimple(
                                        onError = {
                                            it.printStackTrace()
                                            viewState.hideProgressBarLoadingDialog()
                                            initEventFormResultData(cleanResult)
                                        },
                                        onSuccess = { draft ->
                                            viewState.hideProgressBarLoadingDialog()
                                            if (draft.fields.isNullOrEmpty()) {
                                                initEventFormResultData(cleanResult)
                                            } else {
                                                fieldsResultList = mapFieldsResult(
                                                    draft.fields,
                                                    fieldsList
                                                )
                                                fieldsDataApi = createFieldsData(
                                                    fieldsList, /*registration.fields*/
                                                    fieldsResultList
                                                )
                                                val draftResult = EventRegisterData(
                                                    eventData, null, null,
                                                    listOf(EventGroup("", "")),
                                                    fieldsDataApi ?: emptyList()
                                                )
                                                viewState.showLoadSavedFormResultDraftDialog(draftResult)
                                            }
                                        })
                            }
                    } else {
                        viewState.hideProgressBarLoadingDialog()
                        registerToEvent()
                    }


                })


    }


    override fun initEventFormResultData(result: EventRegisterData) {
        val hasForm =
            result.groups.isNotEmpty() || result.fieldsData.isNotEmpty()
        hasGroup = result.groupField != null
        selectedGroup = result.selectedGroup
        fieldsData = result.fieldsData
        invalidFieldsData =
            fieldsData.filter { field -> !field.isValid() }
                .toMutableSet()

        viewState.setFields(
            result.event,
            result.groupField,
            result.selectedGroup,
            result.groups,
            result.fieldsData,
            hasForm
        )
        if (hasForm) {
            checkDataValid()
        } else {
            val agreement = result.event.userAgreement
            if (agreement.isNullOrEmpty()) {
                viewState.showEventRegisterConfirmation()
            } else {
                viewState.showAgreementRegisterDialog(agreement)
            }
        }
    }

    private fun registerToEvent() {
        compositeDisposable += eventRepository.registerToEvent(eventId.toInt())
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                }, onComplete = {
                    viewState.showSuccessRegister(approvingMode)
                })
    }

    private fun mapFields(it: List<EventRegisterFields>?): List<EventRegisterField> {
        val result = mutableListOf<EventRegisterField>()
        it?.forEach { field ->
            result.add(
                EventRegisterField(
                    field.id.toString(), field.name, field.sort ?: 0,
                    field.type ?: EventRegisterField.Type.STRING, field.isRequired,
                    field.description, field.parameters?.options, null, null, null
                )
            )
        }
        return result
    }

    private fun mapFieldsResult(
        it: List<EventFormResultFieldsModel>?,
        fields: List<EventRegisterField>?
    ): List<EventRegisterResponseField> {
        val result = mutableListOf<EventRegisterResponseField>()
        it?.forEach { field ->
            val type = fields?.firstOrNull { it.id == field.id.toString() }
            result.add(
                EventRegisterResponseField(
                    field.id?.toString() ?: "",
                    type?.type ?: EventRegisterField.Type.STRING,
                    field.value
                )
            )
        }
        return result
    }

    private fun createFieldsData(
        fields: List<EventRegisterField>?,
        responseField: List<EventRegisterResponseField?>?
    ): List<EventRegisterFieldData<*>>? {
        return fields?.mapNotNull { field ->
            when (field.type) {
                EventRegisterField.Type.STRING,
                EventRegisterField.Type.TEXT_AREA,
                EventRegisterField.Type.NUMBER -> EventRegisterFieldData.String(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<String>()
                )
                EventRegisterField.Type.DATE,
                EventRegisterField.Type.DATETIME,
                EventRegisterField.Type.DATETIMEPLANED -> EventRegisterFieldData.Date(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<String>()
                )
                EventRegisterField.Type.CHECKBOXES,
                EventRegisterField.Type.CHECKBOX -> EventRegisterFieldData.Checkbox(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<Set<String>>()
                )
                EventRegisterField.Type.SELECT_BOX -> EventRegisterFieldData.SelectBox(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<String>()
                )
                EventRegisterField.Type.RADIO_BOX -> EventRegisterFieldData.RadioBox(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<String>()
                )
                EventRegisterField.Type.FILE -> EventRegisterFieldData.File(
                    field,
                    findRegistrationDataValue(
                        field,
                        responseField
                    ).fromJson(EventFile.Deserializer())
                )
//                EventRegisterField.Type.FILE ->  {
//                    val file = EventRegisterFieldData.File(
//                        field,
//                        EventFile(
//                            Uri.parse(findRegistrationDataValueNew(
//                            field,
//                            responseField
//                        )), findRegistrationDataValueNew(
//                            field,
//                            responseField
//                        )?:"", ""))
//                    oldFilesList.add(file)
//                    file
//                }

                EventRegisterField.Type.BOOLEAN -> EventRegisterFieldData.Boolean(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<Boolean>()
                )
                EventRegisterField.Type.PASSPORT -> EventRegisterFieldData.Passport(
                    field,
                    findRegistrationDataValue(field, responseField).fromJson<EventPassport>()
                )
                else -> null
            }
        }
    }

    private fun findRegistrationDataValue(
        field: EventRegisterField,
        fields: List<EventRegisterResponseField?>?
    ): JsonElement? {
        return fields?.find { field.id == it?.id }?.value
    }

    private fun findRegistrationDataValueNew(
        field: EventRegisterField,
        fields: List<EventRegisterResponseField?>?
        //): JsonElement? {
    ): String? {

        return fields?.find { field.id == it?.id }?.value.toString()
    }

    override fun onDataChange(field: EventRegisterFieldData<*>) {
        if (field.isValid()) invalidFieldsData.remove(field) else invalidFieldsData.add(field)
        checkDataValid()
    }

    override fun onRegisterClick() {
        compositeDisposable += getRequestBody(false)
            .flatMap { /*eventRepository.eventRegister(eventId, it)*/
                eventRepository.eventRegisterNew(it)
            }
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    val group = selectedGroup
                    val fieldsData = fieldsData
                    if (group == null && fieldsData.isEmpty()) viewState.showEventRegisterConfirmation()
                    onReceiveError(it)
                },
                onSuccess = {
                    registerToEvent()
                    //viewState.showSuccessRegister(approvingMode)
                })
    }

    override fun onBackClick() {
        if (fieldsData.filter { f -> f.value != null }.isNullOrEmpty()) {
            viewState.navigateUp()
        } else {
            viewState.showSaveFormResultDraftDialog()
        }
    }

    override fun saveEventFormResultDraft() {
        compositeDisposable += getRequestBody(true)
            .flatMap { body -> eventRepository.saveEventFormResultDraft(body) }
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.navigateUp() }
            )
    }

    override fun onSuccessCancel() {
        viewState.navigateUp()
    }

    override fun onSuccessGoToList() {
        viewState.showEventLists()
    }

    override fun onSuccessGoToEvent() {
        /*compositeDisposable += eventRepository.setDefaultEvent(eventId)
                .andThen(userRepository.getUserShortNew().ignoreElement().onErrorComplete())
                .andThen(eventData.load(eventId))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { viewState.showEvent() }*/

        compositeDisposable += eventRepository.addEventToCalendar(
            EventCalendarBody(
                appData.getId(),
                EventCalendarBodyEntity(EventCalendarBody.CALENDAR_EVENT, eventId.toInt())
            )
        )
            .andThen(userRepository.getUserShortNew().ignoreElement().onErrorComplete())
            .andThen(eventData.load(eventId))
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple { viewState.showEvent() }
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
                    } else {
                        it.printStackTrace()
                    }
                }, onNext = { path ->
                    val nameAndExtension = (path.fileName(contentResolver)
                        ?: path.toString()).getFileNameAndExtension()

                    val fileName = nameAndExtension.first
                    val fileExtension = nameAndExtension.second

                    val availableExtensions = field.field.values ?: emptyList()
                    val contains = availableExtensions.isEmpty() || availableExtensions.find {
                        val availableExtension = it.toLowerCase(Locale.getDefault())
                        availableExtension == fileExtension || fileExtension == "jpg" && availableExtension == "jpeg"
                    } != null

                    if (contains) {
                        field.value = EventFile("",path, fileName, fileExtension)
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


    private fun getRequestBody(isDraft: Boolean): Single<RequestBody> {
        return Single.fromCallable {
            val group = selectedGroup
            val fieldsData = fieldsData
            if (group == null && fieldsData.isEmpty()) return@fromCallable "".toRequestBody()

            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .apply {
                    var added = false
                    if (group != null) {
                        addFormDataPart("category_id", group)
                        added = true
                    }
                    addFormDataPart("form", formId.toString())
                    added = true
                    fieldsData.forEachIndexed { index, fieldData ->
                        val position = index
                        val key = fieldData.field.id
                        val value = fieldData.value ?: return@forEachIndexed
                        //addFormDataPart("fields[$position][id]", key)
                        when (fieldData) {
                            is EventRegisterFieldData.File -> fieldData.value?.let {
                                val path = it.path
                                val fileId = it.id
                                if (path.scheme?.startsWith("https") != true && path.scheme?.contains("https") != true) {
                                    addFormDataPart("fields[$position][id]", key)
                                    val name = "${it.name}.${it.mimeType}"
                                    contentResolver.openInputStream(path)?.buffered()
                                        ?.use { stream -> stream.readBytes() }?.let { bytes ->
                                            val body = bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                                            addFormDataPart(
                                                //"fields[$position][value][file]",
                                                "fields[$position][value]",
                                                name,
                                                body
                                            )
                                            added = true
                                        }
                                }else {
                                    addFormDataPart("fields[$position][id]", key)
                                    addFormDataPart("fields[$position][value]", fileId)
                                    added = true
                                }
                            }
                            else -> {
                                if (value is Iterable<*>) {
                                    if (value.count() > 0) addFormDataPart(
                                        "fields[$position][id]",
                                        key
                                    )
                                    value.forEachIndexed { index, any ->
                                        if (any != null) {
                                            addFormDataPart(
                                                "fields[$position][value][]",
                                                any.toString()
                                            )
                                            added = true
                                        }
                                    }
                                } else {
                                    when (value) {
                                        is EventPassport ->
                                            if (value.isDataComplete()) {
                                                addFormDataPart("fields[$position][id]", key)
                                                addFormDataPart(
                                                    "fields[$position][value][series]",
                                                    value.series ?: ""
                                                )
                                                addFormDataPart(
                                                    "fields[$position][value][number]",
                                                    value.number ?: ""
                                                )
                                                addFormDataPart(
                                                    "fields[$position][value][issuedBy]",
                                                    value.issuedBy ?: ""
                                                )
                                                addFormDataPart(
                                                    "fields[$position][value][issuedDepartment]",
                                                    value.issuedDepartment ?: ""
                                                )
                                                addFormDataPart(
                                                    "fields[$position][value][issuedDate]",
                                                    value.issuedDate ?: ""
                                                )
                                                added = true
                                            } else null
                                        else -> {
                                            val data = value.toString()
                                            addFormDataPart("fields[$position][id]", key)
                                            addFormDataPart("fields[$position][value]", data)
                                            added = true
                                        }
                                    }
                                    /*val data = when (value) {
                                        is EventPassport ->
                                            if (value.isDataComplete()) GsonBuilder().serializeNulls().create().toJson(value)
                                            else null
                                        else -> value.toString()
                                    }
                                    if (data != null) {
                                        addFormDataPart("fields[$position][value]", data)
                                        added = true
                                    }*/
                                }
                            }
                        }
                    }
                    addFormDataPart("isDraft", isDraft.toString())
                    if (!added) {
                        return@fromCallable "".toRequestBody()
                    }
                }
                .build()
        }

    }

    private fun getEventData(event: EventNew): EventRegistration {
        return EventRegistration(
            id = event.id.toString(),
            organization = null,
            code = "",
            organizationId = event.binds?.organization?.id?.toString(),
            name = event.name ?: "",
            description = event.description ?: "",
            logo = null,
            image = event.image?.uri ?: event.binds?.organization?.image?.uri,
            conferenceStart = event.holdingDate?.from,
            conferenceFinish = event.holdingDate?.to,
            registrationStart = null,
            registrationFinish = null,
            status = null,
            userAgreement = event.userAgreement?.uri,
            registrationName = null,
            userRegistration = event.status?.value ?: Event.Status.FINISHED,
            registrationHeadline = null,
            registrationSubtitle = null,
            isRequireModerate = null,
            moderateRegistration = null,
            conferenceFirstActivityStart = null,
            conferenceRegistrationFinishDate = event.requestsApply?.dateLimit
        )
    }

    companion object {
        private const val API_ERROR_ALREADY_APPROVED = "Registration is approved before"
        private const val API_ERROR_REGISTRATION_CLOSED = "registration is not carried out"
    }
}
