package com.example.ui.event.registration

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.example.app.R
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.EventRegisterProfilePrefilledFields.Companion.prefilledFromJson
import com.example.data.models.Optional
import com.example.data.socket.SocketIOManager
import com.example.extensions.getFileNameAndExtension
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.rxtakephoto.PermissionNotGrantedException
import com.google.gson.JsonElement
import com.example.extensions.fileName
import com.example.extensions.fromJson
import com.example.util.rxtakephoto.RxTakePhoto
import com.example.util.rxtakephoto.rx_image_picker.entity.Result
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
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
import kotlin.collections.ArrayList

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

    private var fieldsData = arrayListOf<EventRegisterFieldData<*>>()
    private var invalidFieldsData = mutableSetOf<EventRegisterFieldData<*>>()

    private lateinit var eventData: EventRegisterData
    private val formId get() = eventData.event.formId ?: 0
    private var approvingMode: String? = ""


    override fun attachView(view: EventRegistrationContract.View?) {
        super.attachView(view)
        if (isFirstLaunch) isFirstLaunch = false
        else compositeDisposable += getProfileFieldsData().map { it.value }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it == null || it !is EventRegisterFieldData.Prefilled) return@subscribeSimple
                viewState.updateProfileFields(it)
                onDataChange(it)
            }
    }


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += eventRepository.getEventDetailForRegister(eventId)
            .flatMap { e ->
                approvingMode = e.state?.registration?.approvingMode
                eventData = EventRegisterData.init(e)
                getPrefilledFieldsIfNeed(e).map {
                    val formFields = mapFields(it.first)
                    val formResultFields = mapFieldsResult(it.second, formFields)
                    eventData.addFields(createFieldsData(formFields, formResultFields))
                }
            }
            .flatMap { getRegisterDraftsData() }
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
        viewState.apply {
            setFormFields(event, result)
            checkDataValid()
        }
    }

    override fun onRegisterClick() {
        if (checkDataValid()) {
            compositeDisposable += getRequestBody(0)
                .flatMap { eventRepository.eventRegisterNew(it) }
                .flatMapCompletable { eventRepository.registerToEvent(eventId.toInt()) }
                .andThen(socket.connectToUpdates())
                .performOnBackgroundOutOnMain()
                .withCustomLoading(viewState)
                .subscribeSimple(
                    onError = { onReceiveError(it) },
                    onComplete = { viewState.showEventRegistrationSuccessDialog() })
        } else viewState.showErrors(invalidFieldsData)
    }

    override fun saveEventFormResultDraft() {
        compositeDisposable += getRequestBody(1)
            .flatMap { body -> eventRepository.saveEventFormResultDraft(body) }
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.navigateUpClick() }
            )
    }

    private fun checkDataValid(): Boolean {
        val isValid = invalidFieldsData.isNullOrEmpty()
        viewState.enableActionButton(isValid)
        return isValid
    }

    override fun onDataChange(field: EventRegisterFieldData<*>) {
        if (field.isValid()) invalidFieldsData.remove(field) else invalidFieldsData.add(field)
        checkDataValid()
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
                EventRegisterField.Type.PREFILLED -> {
                    EventRegisterFieldData.Prefilled(
                        field,
                        findValue(field, results).prefilledFromJson(field.values)
                    )
                }

                EventRegisterField.Type.SEPARATOR -> {
                    EventRegisterFieldData.Title(
                        field,
                        findValue(field, results).fromJson<String>()
                    )
                }

                EventRegisterField.Type.PHONE -> {
                    EventRegisterFieldData.Phone(
                        field,
                        findValue(field, results).fromJson<String>()
                    )
                }

                EventRegisterField.Type.STRING,
                EventRegisterField.Type.TEXT_AREA,
                EventRegisterField.Type.EMAIL,
                EventRegisterField.Type.SITE,
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


    override fun onNavigateUpClick() {
        viewState.hideKeyboard()
        val fields = fieldsData.filter { x -> x.field.type != EventRegisterField.Type.PREFILLED }
        if (fields.filter { f -> f.value != null }.isNullOrEmpty()) viewState.navigateUpClick()
        else viewState.showSaveFormResultDraftDialog()
    }


    override fun onSuccessCancel() = viewState.navigateUpClick()
    override fun onSuccessGoToList() = viewState.showEventLists()


    override fun onAddFileClick(field: EventRegisterFieldData<EventFile?>) =
        viewState.openFileSelector(field)

    override fun onTakeFile(field: EventRegisterFieldData<EventFile?>) =
        takeFileRequest(rxTakePhoto.takeFile(), field)

    override fun onTakeImage(field: EventRegisterFieldData<EventFile?>) =
        takeFileRequest(rxTakePhoto.takeImage(), field)

    private fun takeFileRequest(
        request: Observable<Result>,
        field: EventRegisterFieldData<EventFile?>
    ) {
        compositeDisposable += request.map { it.uri }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    if (it is PermissionNotGrantedException)
                        viewState.showToast(R.string.event_register_file_no_permission)
                    else it.printStackTrace()
                },
                onNext = { path ->
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
            val formFields = fieldsData

            if (formFields.isEmpty()) return@fromCallable "".toRequestBody()
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .apply {
                    //if (group != null) addFormDataPart("category_id", group)

                    addFormDataPart("form", formId.toString())
                    addFormDataPart("isDraft", isDraft.toString())

                    formFields.forEachIndexed { index, fieldData ->
                        val key = fieldData.field.id
                        val value = fieldData.value ?: return@forEachIndexed

                        if (fieldData is EventRegisterFieldData.Prefilled) return@forEachIndexed

                        else if (fieldData is EventRegisterFieldData.File) {
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
                            addFormDataPart("fields[$index][value][issuedBy]", passport.issuedBy ?: "")
                            addFormDataPart("fields[$index][value][issuedDepartment]", passport.issuedDepartment ?: "")
                            addFormDataPart("fields[$index][value][issuedDate]", passport.issuedDate ?: "")
                        } else if (value is Iterable<*>) {
                            if (value.count() > 0) addFormDataPart("fields[$index][id]", key)
                            value.filterNotNull().forEachIndexed { i, a ->
                                addFormDataPart("fields[$i][value]", a.toString())
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


    private fun getProfileFieldsData(): Maybe<Optional<EventRegisterFieldData<*>>> {
        val data = fieldsData.find { x -> x.field.type == EventRegisterField.Type.PREFILLED }

        return if (data != null && data is EventRegisterFieldData.Prefilled) {
            eventRepository.getPrefilledEventFormResult(data.field.id)
                .map { it.fields.prefilledToJson() }
                .flatMapMaybe {
                    val prefilled = it.prefilledFromJson(data.field.values!!)
                    if (prefilled == data.value) Maybe.just(Optional(null))
                    else fieldsData.find { x -> x.field.id == data.field.id }.let { f ->
                        if (f is EventRegisterFieldData.Prefilled) f.value = prefilled
                        Maybe.just(f.asOptional()).withDelay(400)
                    }
                }
                .onErrorResumeNext(Maybe.just(Optional(null)))

        } else Maybe.just(Optional(null))
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

    private fun getPrefilledFieldsIfNeed(e : EventNew): Maybe<Pair<List<EventRegisterFields>?, ArrayList<EventFormResultFieldsModel>>> {
        val fields = e.binds?.getParticipationForm()?.fields
        val results = arrayListOf<EventFormResultFieldsModel>().apply {
            addAll(e.binds?.getFormResult() ?: emptyList())
        }
        val pref = fields?.find { x -> x.type == EventRegisterField.Type.PREFILLED }

        return if (pref != null)
            return if (results.isEmpty() || results.none { it.id == pref.id }){
                eventRepository.getPrefilledEventFormResult(pref.id.toString()).flatMapMaybe {
                    val jsonData = it.fields.prefilledToJson()
                    results.add(EventFormResultFieldsModel(pref.id, null, jsonData))
                    Maybe.just(Pair(fields, results))
                }
            } else Maybe.just(Pair(fields, results))
        else Maybe.just(Pair(fields, results))
    }

    companion object {
        private const val API_ERROR_ALREADY_APPROVED = "Registration is approved before"
        private const val API_ERROR_REGISTRATION_CLOSED = "registration is not carried out"
    }
}
