package com.example.ui.event.registration

import android.Manifest
import android.content.ContentResolver
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.EventFormResultModel.Companion.EVENT_FORM_RESULT_FORM_ID
import com.example.data.models.EventFormResultModel.Companion.EVENT_FORM_RESULT_USER_ID
import com.example.data.socket.SocketIOManager
import com.example.extensions.getFileNameAndExtension
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.rxtakephoto.PermissionNotGrantedException
import com.google.gson.JsonElement
import com.tbruyelle.rxpermissions2.RxPermissions
import fileName
import fromJson
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.MaybeSubject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withDelay
import java.util.*
import javax.inject.Inject

@InjectViewState
class EventRegistrationPresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val rxPermissions: RxPermissions,
    private val contentResolver: ContentResolver,
    private val socket: SocketIOManager,
    private val appData: AppData
) : BasePresenter<EventRegistrationContract.View>(appData), EventRegistrationContract.Presenter {

    lateinit var eventId: String
    private var isFirstLaunch = true

    private var selectedGroup: String? = null
    private var fieldsData = arrayListOf<EventRegisterFieldData<*>>()
    private var invalidFieldsData: MutableSet<EventRegisterFieldData<*>> = mutableSetOf()

    private var takeFileMaybe: MaybeSubject<Uri>? = null
    private var formId: Int = 0
    private var approvingMode: String? = null
    private var mDy = 0


    private lateinit var eventData: EventRegistration

    private val formFields = arrayListOf<EventRegisterField>()
    private val prefilledFormFields = arrayListOf<EventRegisterFields>()

    override fun changeAppBarBackground(value: Int) {
        mDy = value
        viewState.updateAppBarBackgroundColorValue(mDy)
    }

    override fun attachView(view: EventRegistrationContract.View?) {
        super.attachView(view)
        //viewState.updateAppBarBackgroundColorValue(mDy)
        compositeDisposable += loadUserProfileFormFields()
            .doOnSuccess {
                val field = fieldsData.find { x -> x.field.id == it.field.id }
                if (field != null && it.field.type == EventRegisterField.Type.PREFILLED) {
                    val prefilledField = field as EventRegisterFieldData.Prefilled
                    val index = fieldsData.indexOf(prefilledField)
                    fieldsData[index] = it
                    invalidFieldsData = fieldsData.filter { f -> !f.isValid() }.toMutableSet()
                }
            }
            .withDelay(500)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it.value != null) {
                    viewState.updateProfileFields(it.value!!)
                    checkDataValid()
                }
            }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            setContentPlaceholder()
            updateAppBarBackgroundColorValue(mDy)
            enableActionButton(true)
        }

        compositeDisposable += eventRepository.getEventDetailForRegister(eventId)
            .doOnSuccess {
                approvingMode = it.state?.registration?.approvingMode
                val form =
                    it.binds?.form?.firstOrNull { e -> e.type == EventFormModel.Type.PARTICIPATION }
                eventData = getEventData(form, it)
                formId = form?.id ?: 0
                formFields.addAll(mapFields(form?.fields?.filter { x -> x.type != EventRegisterField.Type.PREFILLED }))
                prefilledFormFields.addAll(form?.fields?.filter { x -> x.type == EventRegisterField.Type.PREFILLED }
                    ?: emptyList())
            }
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { event ->
                    if (event.state?.registration?.formEnabled == true) {
                        viewState.setFormHeader(eventData)
                        getEventFormResult(eventData, formFields)
                    } else registerToEvent(false)
                })
    }

    private fun getEventFormResult(eventData: EventRegistration, list: List<EventRegisterField>) {
        val registerResult = EventRegisterData(eventData, false, mutableListOf(), mutableListOf())
        compositeDisposable += Completable.create { emitter ->
            val disposable = CompositeDisposable()
            disposable += eventRepository.getEventFormResult(getFormBody())
                .doOnSuccess { result ->
                    val fieldsResultList =
                        mapFieldsResult(
                            result.data.firstOrNull { x -> x.form == formId }?.fields,
                            list
                        )
                    val formResult = createFieldsData(list, fieldsResultList)
                    registerResult.addFields(formResult)
                }
                .subscribeSimple(
                    onError = {
                        it.printStackTrace()
                        getProfileFormFields(emitter, disposable, registerResult, list)
                    }, onSuccess = {
                        getProfileFormFields(emitter, disposable, registerResult, list)
                    })
            emitter.setDisposable(disposable)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    initFormResultData(registerResult.event, registerResult.getSortedFields())
                    if (it is HttpException && it.code() == 404) it.printStackTrace()
                    else onReceiveError(it)
                }, onComplete = {
                    if (registerResult.hasDraft) {
                        viewState.showLoadSavedFormResultDraftDialog(registerResult)
                    } else {
                        initFormResultData(registerResult.event, registerResult.getSortedFields())
                    }
                })
    }


    private fun getProfileFormFields(
        emitter: CompletableEmitter,
        disposable: CompositeDisposable,
        registerResult: EventRegisterData,
        list: List<EventRegisterField>
    ) {
        disposable += loadUserProfileFormFields()
            .doOnSuccess {
                registerResult.addField(it)
            }
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    getEventFormDraftFields(emitter, disposable, registerResult, list)
                },
                onSuccess = {
                    getEventFormDraftFields(emitter, disposable, registerResult, list)
                })
    }


    private fun getEventFormDraftFields(
        emitter: CompletableEmitter,
        disposable: CompositeDisposable,
        registerResult: EventRegisterData,
        list: List<EventRegisterField>
    ) {
        disposable += eventRepository.getEventFormResultDraft(formId, emptyMap())
            .doOnSuccess { result ->
                registerResult.hasDraft = !result.fields.isNullOrEmpty()
                val fieldsResultList = mapFieldsResult(result.fields, list)
                val draftFields = createFieldsData(list, fieldsResultList)
                registerResult.addDraftFields(draftFields)
            }
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    emitter.onError(it)
                },
                onSuccess = {
                    emitter.onComplete()
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
            else showEventRegisterConfirmation()
        }
    }


    private fun registerToEvent(withLoading: Boolean) {
        compositeDisposable += eventRepository.registerToEvent(eventId.toInt())
            .andThen(socket.connectToUpdates())
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .let {
                if (withLoading) it.withCustomProgressBarLoadingDialog(viewState)
                else it
            }
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = { viewState.showSuccessRegister(approvingMode) }
            )
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

    override fun onRegisterClick() {
        compositeDisposable += getRequestBody(0)
            .flatMap { eventRepository.eventRegisterNew(it) }
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
                    registerToEvent(true)
                })
    }

    override fun onBackClick() {
        val fields = fieldsData.filter { x -> x.field.type != EventRegisterField.Type.PREFILLED }
        if (fields.filter { f -> f.value != null }.isNullOrEmpty()) {
            viewState.navigateUp()
        } else {
            viewState.showSaveFormResultDraftDialog()
        }
    }

    override fun saveEventFormResultDraft() {
        compositeDisposable += getRequestBody(1)
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
        viewState.showEvent(eventId)
    }


    override fun onRegisterCancelClick() {
        viewState.navigateUp()
    }

    override fun onSelectedGroupChange(groupId: String?) {
        selectedGroup = groupId
        checkDataValid()
    }

    private fun checkDataValid() {
        val isValid = invalidFieldsData.isNullOrEmpty()
        viewState.enableActionButton(isValid)
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
                    var added = false
                    if (group != null) addFormDataPart("category_id", group)

                    addFormDataPart("form", formId.toString())
                    addFormDataPart("isDraft", isDraft.toString())
                    added = true

                    fieldsData.forEachIndexed { index, fieldData ->
                        val key = fieldData.field.id
                        val value = fieldData.value ?: return@forEachIndexed

                        when (fieldData) {
                            is EventRegisterFieldData.Prefilled -> {
                                //if (!isDraft) addFormDataPart("fields[$index][id]", key)
                            }
                            is EventRegisterFieldData.File -> fieldData.value?.let {
                                val path = it.path
                                val fileId = it.id
                                if (checkHttpScheme(path)) {
                                    addFormDataPart("fields[$index][id]", key)
                                    val name = "${it.name}.${it.mimeType}"
                                    contentResolver.openInputStream(path)?.buffered()
                                        ?.use { stream -> stream.readBytes() }?.let { bytes ->
                                            val body = bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                                            addFormDataPart("fields[$index][value]", name, body)
                                            added = true
                                        }
                                } else {
                                    addFormDataPart("fields[$index][id]", key)
                                    addFormDataPart("fields[$index][value]", fileId)
                                    added = true
                                }
                            }
                            else -> {
                                if (value is Iterable<*>) {
                                    if (value.count() > 0) addFormDataPart("fields[$index][id]", key)
                                    value.forEachIndexed { _, any ->
                                        if (any != null) {
                                            addFormDataPart("fields[$index][value]", any.toString())
                                            added = true
                                        }
                                    }
                                } else {
                                    when (value) {
                                        is EventPassport ->
                                            if (value.isDataComplete()) {
                                                addFormDataPart("fields[$index][id]", key)
                                                addFormDataPart("fields[$index][value][series]", value.series ?: "")
                                                addFormDataPart("fields[$index][value][number]", value.number ?: "")
                                                addFormDataPart("fields[$index][value][issuedBy]", value.issuedBy ?: "")
                                                addFormDataPart("fields[$index][value][issuedDepartment]", value.issuedDepartment ?: "")
                                                addFormDataPart("fields[$index][value][issuedDate]", value.issuedDate ?: "")
                                                added = true
                                            }
                                        else -> {
                                            val data = value.toString()
                                            addFormDataPart("fields[$index][id]", key)
                                            addFormDataPart("fields[$index][value]", data)
                                            added = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!added) return@fromCallable "".toRequestBody()
                }.build()
        }

    }

    private fun getEventData(form: EventFormModel?, event: EventNew): EventRegistration {
        return EventRegistration.setEventRegistration(event).apply {
            setBackgroundColor(event)
            registrationHeadline = form?.title
            registrationSubtitle = form?.subtitle
        }
    }

    private fun getFormBody(): Map<String, Int> {
        return mapOf(
            EVENT_FORM_RESULT_FORM_ID to formId,
            EVENT_FORM_RESULT_USER_ID to appData.getId()
        )
    }

    private fun loadUserProfileFormFields(): Maybe<EventRegisterFieldData.Prefilled> {
        return if (!prefilledFormFields.isNullOrEmpty()) {
            val prefilledFieldsId = prefilledFormFields.firstOrNull()?.id ?: 0
            val options = prefilledFormFields.firstOrNull()?.parameters?.options

            eventRepository.loadEventFormResult(prefilledFieldsId.toString())
                .map { data ->
                    data.toProfileFieldsFormResult().apply {
                        setFieldsIsChosen(options, this)
                    }
                }
                .flatMapMaybe {
                    val field = prefilledFormFields.first().let { f ->
                        EventRegisterField(
                            f.id.toString(), f.name, f.sort ?: 0,
                            EventRegisterField.Type.PREFILLED, f.isRequired,
                            f.description, f.parameters?.options, null, null, null
                        )
                    }
                    Maybe.just(EventRegisterFieldData.Prefilled(field, it))
                }
        } else {
            Maybe.defer { Maybe.just(null) }
        }
    }

    private fun checkHttpScheme(path: Uri): Boolean {
        return path.scheme?.startsWith("https") != true && path.scheme?.contains("https") != true
    }

    companion object {
        private const val API_ERROR_ALREADY_APPROVED = "Registration is approved before"
        private const val API_ERROR_REGISTRATION_CLOSED = "registration is not carried out"
    }
}
