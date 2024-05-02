package com.example.ui.event.registration

import android.Manifest
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.example.R
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.Optional
import com.example.data.socket.SocketIOManager
import com.example.exceptions.NoEventFormException
import com.example.extensions.getFileNameAndExtension
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.rxtakephoto.PermissionNotGrantedException
import com.google.gson.JsonElement
import com.tbruyelle.rxpermissions2.RxPermissions
import com.example.extensions.fileName
import com.example.extensions.fromJson
import com.example.util.rxtakephoto.RxTakePhoto
import com.example.util.rxtakephoto.rx_image_picker.entity.Result
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.MaybeSubject
import moxy.InjectViewState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomLoading
import withDelay
import withProgressBarDialogLoading
import java.util.*
import javax.inject.Inject

@InjectViewState
class EventRegistrationPresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    private val rxTakePhoto: RxTakePhoto,
    private val contentResolver: ContentResolver,
    private val socket: SocketIOManager,
    private val appData: AppData
) : BasePresenter<EventRegistrationContract.View>(appData), EventRegistrationContract.Presenter {

    lateinit var eventId: String
    private var isFirstLaunch = true

    private var selectedGroup: String? = null
    private var fieldsData = arrayListOf<EventRegisterFieldData<*>>()
    private var invalidFieldsData: MutableSet<EventRegisterFieldData<*>> = mutableSetOf()

    private val formId: Int
        get() = eventData.event.formId ?: 0

    private var mDy = 0
    private lateinit var eventData: EventRegisterData

    private var approvingMode: String? = ""


    override fun changeAppBarBackground(value: Int) {
        mDy = value
        viewState.updateAppBarBackgroundColorValue(mDy)
    }

    override fun attachView(view: EventRegistrationContract.View?) {
        super.attachView(view)
        if (isFirstLaunch) isFirstLaunch = false
        else compositeDisposable += getProfileFieldsData().map { it.value }
            .doOnSuccess {
                val field = fieldsData.find { x -> x.field.id == it?.field?.id }
                if (field != null && it?.field?.type == EventRegisterField.Type.PREFILLED) {
                    fieldsData.set(fieldsData.indexOf(field), it)
                    invalidFieldsData = fieldsData.filter { f -> !f.isValid() }.toMutableSet()
                }
            }
            .withDelay(500)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it?.value != null) {
                    viewState.updateProfileFields(it.value!!)
                    checkDataValid()
                }
            }
    }


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            updateAppBarBackgroundColorValue(mDy)
            enableActionButton(true)
        }

        compositeDisposable += eventRepository.getEventDetailForRegister(eventId)
            .doOnSuccess { e ->
                approvingMode = e.state?.registration?.approvingMode
                val form = e.binds?.form?.firstOrNull { o -> o.type == EventFormModel.Type.PARTICIPATION }
                val formFields = mapFields(form?.fields?.filter { x -> x.type != EventRegisterField.Type.PREFILLED })
                val formResultFields = mapFieldsResult(e.binds?.userFormResult?.firstOrNull()?.result?.fields, formFields)
                eventData = EventRegisterData.init(e).apply {
                    addFields(createFieldsData(formFields, formResultFields))
                }
            }
            .flatMap { getRegisterDraftsData() }
            .flatMapSingle { getProfileFieldsData().doOnSuccess { eventData.addField(it.value) } }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    if (eventData.hasDraft) viewState.showSavedFormResultDraftDialog(eventData)
                    else initFormResultData(eventData.event, eventData.getSortedFields())
                })
    }


    override fun initFormResultData(
        event: EventRegistration,
        result: List<EventRegisterFieldData<*>>
    ) {
        fieldsData.addAll(result)
        invalidFieldsData = fieldsData.filter { field -> !field.isValid() }.toMutableSet()
        val hasForm = !result.filter { x -> x.value != null || x.field != null }.isNullOrEmpty()
        viewState.apply {
            setFormFields(event, result, hasForm)
            if (hasForm) checkDataValid()
        }
    }

    override fun onRegisterClick() {
        compositeDisposable += getRequestBody(0)
            .flatMap { eventRepository.eventRegisterNew(it) }
            .flatMapCompletable { eventRepository.registerToEvent(eventId.toInt()) }
            .andThen(socket.connectToUpdates())
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = { viewState.showSuccessRegister(approvingMode) })
    }

    private fun mapFields(it: List<EventRegisterFields>?): List<EventRegisterField> {
        return mutableListOf<EventRegisterField>().apply {
            it?.forEach { field -> add(field.createData()) }
        }
    }

    private fun mapFieldsResult(
        it: List<EventFormResultFieldsModel>?,
        fields: List<EventRegisterField>?
    ): List<EventRegisterResponseField> {
        return mutableListOf<EventRegisterResponseField>().apply {
            it?.forEach { field ->
                val type = fields?.firstOrNull { it.id == field.id.toString() }
                add(field.createData(type?.type))
            }
        }
    }

    private fun findValue(
        field: EventRegisterField,
        fields: List<EventRegisterResponseField?>?
    ): JsonElement? {
        return fields?.find { field.id == it?.id }?.value
    }

    private fun createFieldsData(
        fields: List<EventRegisterField>?,
        results: List<EventRegisterResponseField?>?
    ): List<EventRegisterFieldData<*>>? {
        return fields?.mapNotNull { field ->
            when (field.type) {
                EventRegisterField.Type.STRING,
                EventRegisterField.Type.TEXT_AREA,
                EventRegisterField.Type.GROUP,
                EventRegisterField.Type.NUMBER ->
                    EventRegisterFieldData.String(
                        field,
                        findValue(field, results).fromJson<String>()
                    )

                EventRegisterField.Type.DATE,
                EventRegisterField.Type.DATETIME,
                EventRegisterField.Type.DATETIMEPLANED ->
                    EventRegisterFieldData.Date(field, findValue(field, results).fromJson<String>())

                EventRegisterField.Type.CHECKBOXES,
                EventRegisterField.Type.CHECKBOX ->
                    EventRegisterFieldData.Checkbox(
                        field,
                        findValue(field, results).fromJson<Set<String>>()
                    )

                EventRegisterField.Type.SELECT_BOX ->
                    EventRegisterFieldData.SelectBox(
                        field,
                        findValue(field, results).fromJson<String>()
                    )

                EventRegisterField.Type.RADIO_BOX ->
                    EventRegisterFieldData.RadioBox(
                        field,
                        findValue(field, results).fromJson<String>()
                    )

                EventRegisterField.Type.FILE ->
                    EventRegisterFieldData.File(
                        field, findValue(field, results).fromJson(EventFile.Deserializer())
                    )

                EventRegisterField.Type.BOOLEAN ->
                    EventRegisterFieldData.Boolean(
                        field,
                        findValue(field, results).fromJson<Boolean>()
                    )

                EventRegisterField.Type.PASSPORT ->
                    EventRegisterFieldData.Passport(
                        field,
                        findValue(field, results).fromJson<EventPassport>()
                    )

                else -> null
            }
        }
    }


    override fun onDataChange(field: EventRegisterFieldData<*>) {
        if (field.isValid()) invalidFieldsData.remove(field) else invalidFieldsData.add(field)
        checkDataValid()
    }

    override fun onBackClick() {
        val fields = fieldsData.filter { x -> x.field.type != EventRegisterField.Type.PREFILLED }
        if (fields.filter { f -> f.value != null }.isNullOrEmpty()) viewState.navigateUp()
        else viewState.showSaveFormResultDraftDialog()
    }

    override fun saveEventFormResultDraft() {
        compositeDisposable += getRequestBody(1)
            .flatMap { body -> eventRepository.saveEventFormResultDraft(body) }
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.navigateUp() }
            )
    }

    override fun onSuccessCancel() = viewState.navigateUp()
    override fun onSuccessGoToList() = viewState.showEventLists()
    override fun onPersonalDataFileClick(url: String) = viewState.openUrl(url)

    override fun onSelectedGroupChange(groupId: String?) {
        selectedGroup = groupId
        checkDataValid()
    }

    private fun checkDataValid() {
        val isValid = invalidFieldsData.isNullOrEmpty()
        viewState.enableActionButton(isValid)
    }



    override fun onAddFileClick(field: EventRegisterFieldData<EventFile?>) {
        viewState.openFileSelector(field)
    }

    override fun onTakeFile(field: EventRegisterFieldData<EventFile?>) {
        takeFileRequest(rxTakePhoto.takeFile(), field)
    }

    override fun onTakeImage(field: EventRegisterFieldData<EventFile?>) {
        takeFileRequest(rxTakePhoto.takeImage(), field)
    }

    private fun takeFileRequest(request: Observable<Result>, field: EventRegisterFieldData<EventFile?>) {
        compositeDisposable += request.map { it.uri }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    if (it is PermissionNotGrantedException)
                        viewState.showToast(R.string.event_register_file_no_permission)
                    else it.printStackTrace()
                },
                onNext = { path ->
                    val nameAndExtension = (path.fileName(contentResolver) ?: path.toString()).getFileNameAndExtension()

                    val fileName = nameAndExtension.first
                    val fileExtension = nameAndExtension.second

                    val availableExtensions = field.field.values ?: emptyList()
                    val contains = availableExtensions.isEmpty() || availableExtensions.find {
                        val availableExtension = it.toLowerCase(Locale.getDefault())
                        availableExtension == fileExtension || fileExtension == "jpg" && availableExtension == "jpeg"
                    } != null

                    if (contains) {
                        field.value = EventFile("", path, fileName, fileExtension)
                        viewState.updateFileField(field.field.id)
                    } else viewState.showWrongFileExtensions(availableExtensions)
                })

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


    private fun getRequestBody(isDraft: Int): Single<RequestBody> {
        return Single.fromCallable {
            val group = selectedGroup
            val fieldsData = fieldsData
            if (group == null && fieldsData.isEmpty()) return@fromCallable "".toRequestBody()

            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .apply {
                    if (group != null) addFormDataPart("category_id", group)

                    addFormDataPart("form", formId.toString())
                    addFormDataPart("isDraft", isDraft.toString())

                    fieldsData.forEachIndexed { index, fieldData ->
                        val key = fieldData.field.id
                        val value = fieldData.value ?: return@forEachIndexed

                        if (fieldData is EventRegisterFieldData.Prefilled) {
                            return@forEachIndexed
                        } else if (fieldData is EventRegisterFieldData.File) {
                            val path = (value as EventFile).path
                            addFormDataPart("fields[$index][id]", key)
                            if (checkHttpScheme(path)) {
                                value.getReadBytes(contentResolver) { body ->
                                    val name = "${value.name}.${value.mimeType}"
                                    addFormDataPart("fields[$index][value]", name, body)
                                }
                            } else addFormDataPart("fields[$index][value]", value.id)
                        } else if (fieldData is EventRegisterFieldData.Passport) {
                            val passport = value as EventPassport
                            if (!passport.isDataComplete()) return@forEachIndexed

                            addFormDataPart("fields[$index][id]", key)
                            addFormDataPart("fields[$index][value][series]", passport.series ?: "")
                            addFormDataPart("fields[$index][value][number]", passport.number ?: "")
                            addFormDataPart(
                                "fields[$index][value][issuedBy]",
                                passport.issuedBy ?: ""
                            )
                            addFormDataPart(
                                "fields[$index][value][issuedDepartment]",
                                passport.issuedDepartment ?: ""
                            )
                            addFormDataPart(
                                "fields[$index][value][issuedDate]",
                                passport.issuedDate ?: ""
                            )
                        } else if (value is Iterable<*>) {
                            if (value.count() > 0) addFormDataPart("fields[$index][id]", key)
                            value.forEachIndexed { _, a ->
                                if (a != null) addFormDataPart(
                                    "fields[$index][value]",
                                    a.toString()
                                )
                            }
                        } else {
                            val data = value.toString()
                            addFormDataPart("fields[$index][id]", key)
                            addFormDataPart("fields[$index][value]", data)
                        }
                    }
                }.build()
        }

    }


    private fun getProfileFieldsData(): Single<Optional<EventRegisterFieldData.Prefilled>> {
        val form = eventData.event.form
        val field = form?.fields?.find { x -> x.type == EventRegisterField.Type.PREFILLED }
        if (field != null) {
            return eventRepository.loadEventFormResult(field.id.toString())
                .map {
                    EventRegisterFieldData.Prefilled(
                        field.createData(),
                        it.toFormResult(field.parameters?.options)
                    ).asOptional()
                }

        } else return Single.just(Optional(null))
    }

    private fun getRegisterDraftsData(): Maybe<EventRegisterData> {
        return eventRepository.getEventFormResultDraft(formId, emptyMap())
            .map { result ->
                val list = eventData.fieldsData.map { it.field }
                val fieldsResultList = mapFieldsResult(result.fields, list)
                val draftFields = createFieldsData(list, fieldsResultList)
                eventData.apply {
                    hasDraft = !result.fields.isNullOrEmpty()
                    addDraftFields(draftFields)
                }
            }.onErrorResumeNext(Maybe.just(eventData))
    }

    private fun checkHttpScheme(path: Uri): Boolean {
        return path.scheme?.startsWith("https") != true && path.scheme?.contains("https") != true
    }

    companion object {
        private const val API_ERROR_ALREADY_APPROVED = "Registration is approved before"
        private const val API_ERROR_REGISTRATION_CLOSED = "registration is not carried out"
    }
}
