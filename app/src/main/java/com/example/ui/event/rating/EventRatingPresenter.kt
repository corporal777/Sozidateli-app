package com.example.ui.event.rating

import android.Manifest
import android.content.ContentResolver
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.data.models.*
import com.example.extensions.getFileNameAndExtension
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
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
import okhttp3.RequestBody.Companion.toRequestBody
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import java.util.*
import javax.inject.Inject

@InjectViewState
class EventRatingPresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    private val rxPermissions: RxPermissions,
    private val contentResolver: ContentResolver,
    private val appData: AppData
) : BasePresenter<EventRatingContract.View>(appData), EventRatingContract.Presenter {

    lateinit var eventId: String

    private var fieldsData: List<EventRegisterFieldData<*>> = emptyList()
    private var invalidFieldsData: MutableSet<EventRegisterFieldData<*>> = mutableSetOf()
    private var takeFileMaybe: MaybeSubject<Uri>? = null
    private var formId: Int? = null
    private var hasGroup = false
    private var selectedGroup: String? = null
    private var rating = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.enableActionButton(true)

        compositeDisposable += eventRepository.getEventDetailForRegister(eventId)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple { event ->
                //approvingMode = event.state?.rating?.approvingMode
                rating = event.state?.rating?.askDelay ?: 0
                val eventData = EventRegistration(
                    id = event.id.toString(),
                    organization = null,
                    code = "",
                    organizationId = event.binds?.organization?.id?.toString(),
                    name = event.name ?: "",
                    description = event.description
                        ?: "",
                    logo = null,
                    image = "",
                    backgroundColor = "",
                    conferenceStart = event.holdingDate?.from,
                    conferenceFinish = event.holdingDate?.to,
                    registrationStart = null,
                    registrationFinish = null,
                    status = null,
                    userAgreement = event.userAgreement?.uri,
                    registrationName = null,
                    userRegistration = event.status?.value
                        ?: Event.Status.FINISHED,
                    registrationHeadline = null,
                    registrationSubtitle = null,
                    isRequireModerate = null,
                    moderateRegistration = null,
                    conferenceFirstActivityStart = null,
                    conferenceRegistrationFinishDate = event.requestsApply?.dateLimit
                )

                if (event.state?.rating?.formEnabled == true) {
                    compositeDisposable += eventRepository.getEventForm(
                        mutableMapOf<String, Any>().apply {
                            put(EventFormModel.FORM_EVENT_ID, eventId)
                            put(EventFormModel.FORM_TYPE, "rating")
                        }).withCheckInternetConnectivity()
                        .performOnBackgroundOutOnMain()
                        .withLoadingDialog(viewState)
                        .subscribeSimple { fields ->
                            if (fields.data.isNotEmpty()) {
                                val form =
                                    fields.data.firstOrNull { it.type == EventFormModel.Type.RATING }
                                eventData.registrationHeadline = form?.title
                                eventData.registrationSubtitle = form?.subtitle
                                val files = form?.files
                                val fieldsList = mapFields(form?.fields)
                                formId = form?.id

                                compositeDisposable += eventRepository.getEventFormResult(
                                    mutableMapOf<String, Any>().apply {
                                        put(
                                            EventFormResultModel.EVENT_FORM_RESULT_FORM_ID,
                                            formId ?: 0
                                        )
                                        put(
                                            EventFormResultModel.EVENT_FORM_RESULT_USER_ID,
                                            appData.getId()
                                        )
                                    }
                                ).withCheckInternetConnectivity()
                                    .performOnBackgroundOutOnMain()
                                    .withLoadingDialog(viewState)
                                    .subscribeSimple { fieldsResult ->
                                        val fieldsResultList = mapFieldsResult(
                                            fieldsResult.data.firstOrNull { it.form == formId }?.fields,
                                            fieldsList
                                        )

                                        val fieldsDataApi = createFieldsData(
                                            fieldsList, /*registration.fields*/
                                            fieldsResultList
                                        )?.toMutableList()

                                        val result = EventRegisterData(
                                            eventData,
                                            hasDraft = false,
                                            fieldsData = fieldsDataApi!!,
                                            mutableListOf()
                                        )

                                        viewState.apply {
                                            val hasForm = result.fieldsData.isNotEmpty()
                                            fieldsData = result.fieldsData
                                            invalidFieldsData =
                                                fieldsData.filter { field -> !field.isValid() }
                                                    .toMutableSet()
                                            setFields(
                                                result.event,
                                                result.fieldsData,
                                                rating,
                                                files
                                            )

                                            /*if (hasForm) {
                                                checkDataValid()
                                            } else {

                                            }*/
                                        }
                                    }
                            }
                        }
                }
            }

        /*compositeDisposable += eventRepository.loadEventRatingData(eventId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    viewState.apply {
                        fieldsData = it.fieldsData
                        invalidFieldsData = fieldsData.filter { field -> !field.isValid() }.toMutableSet()
                        rating = it.ratingValue ?: 0
                        setFields(it.event, fieldsData, rating)
                        checkDataValid()
                    }
                }*/
    }

    private fun mapFields(it: List<EventRegisterFields>?): List<EventRegisterField> {
        val result = mutableListOf<EventRegisterField>()
        it?.forEach { field ->
            result.add(
                EventRegisterField(
                    field.id.toString(), field.name, field.sort ?: 0,
                    field.type ?: EventRegisterField.Type.STRING, field.isRequired,
                    field.description, emptyList(), null, null, null
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
                EventRegisterField.Type.DATETIME -> EventRegisterFieldData.Date(
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

    override fun onDataChange(field: EventRegisterFieldData<*>) {
        if (field.isValid()) invalidFieldsData.remove(field) else invalidFieldsData.add(field)
        checkDataValid()
    }

    override fun onSendClick() {
        compositeDisposable += Single.fromCallable {
            /*val fieldsData = fieldsData
            MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .apply {
                        addFormDataPart("rating_value", rating.toString())
                        fieldsData.forEach { fieldData ->
                            val key = fieldData.field.id
                            val value = fieldData.value ?: return@forEach

                            when (fieldData) {
                                is EventRegisterFieldData.File -> fieldData.value?.let {
                                    val path = it.path
                                    if (path.scheme?.startsWith("http") != true) {
                                        val name = "${it.name}.${it.mimeType}"
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
                    .build()*/
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
                    addFormDataPart("ratingMark", rating.toString())
                    fieldsData.forEachIndexed { index, fieldData ->
                        //val key = fieldData.field.id
                        val position = index
                        val key = fieldData.field.id
                        val value = fieldData.value ?: return@forEachIndexed
                        //addFormDataPart("fields[$position][id]", key)
                        when (fieldData) {
                            is EventRegisterFieldData.File -> fieldData.value?.let {
                                val path = it.path
                                if (path.scheme?.startsWith("http") != true) {
                                    addFormDataPart("fields[$position][id]", key)
                                    val name = "${it.name}.${it.mimeType}"
                                    contentResolver.openInputStream(path)?.buffered()
                                        ?.use { stream -> stream.readBytes() }?.let { bytes ->
                                            val body =
                                                bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                                            addFormDataPart(
                                                "fields[$position][value][file]",
                                                name,
                                                body
                                            )
                                            added = true
                                        }
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
                                }
                            }
                        }
                    }

                    if (!added) {
                        return@fromCallable "".toRequestBody()
                    }
                }
                .build()
        }
            .flatMap { /*eventRepository.setEventRating(eventId, it)*/eventRepository.eventRegisterNew(
                it
            )
            }
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple(
                onSuccess = {
                    viewState.apply {
                        showSuccessRate()
                        navigateUp()
                    }
                }
            )
    }

    private fun checkDataValid() {
        viewState.enableActionButton(rating > 0 && invalidFieldsData.isEmpty())
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

                    val availableExtensions =
                        field.field.values ?: /*emptyList()*/listOf("jpg", "jpeg")
                    val contains = availableExtensions.find {
                        val availableExtension = it.toLowerCase(Locale.getDefault())
                        availableExtension == fileExtension || fileExtension == "jpg" && availableExtension == "jpeg"
                    } != null

                    if (contains) {
                        field.value = EventFile("", path, fileName, fileExtension)
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

    override fun onRatingChange(rating: Int) {
        this.rating = rating
        checkDataValid()
    }
}
