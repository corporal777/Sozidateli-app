package com.example.ui.event.registration

import android.content.ContentResolver
import android.net.Uri
import com.example.app.R
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.EventRegisterProfilePrefilledFields.Companion.prefFromJson
import com.example.data.models.Optional
import com.example.data.models.eventRegister.EventRegisterField
import com.example.data.socket.SocketIOManager
import com.example.extensions.fileName
import com.example.extensions.getFileNameAndExtension
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.rxtakephoto.PermissionNotGrantedException
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

    private var fieldsData = arrayListOf<EventRegisterField<*>>()
    private var invalidFieldsData = mutableSetOf<EventRegisterField<*>>()

    private lateinit var eventData: EventRegisterData

    override fun attachView(view: EventRegistrationContract.View?) {
        super.attachView(view)
        if (isFirstLaunch) isFirstLaunch = false
        else compositeDisposable += getProfileFieldsData().map { it.value }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it == null || it !is EventRegisterField.Prefilled) return@subscribeSimple
                viewState.updateProfileFields(it)
                onDataChange(it)
            }
    }


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += eventRepository.getEventDetailForRegister(eventId)
            .doOnSuccess { eventData = it }
            .flatMap { getRegisterDraftsData() }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    if (eventData.hasDraft) viewState.showSavedFormResultDraftDialog(eventData)
                    else initFormResultData(eventData.event, eventData.getSortedFields())
                })
    }


    override fun initFormResultData(event: EventRegistration, result: List<EventRegisterField<*>>) {
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
                .flatMap { eventRepository.sendFormToRegister(it) }
                .flatMapCompletable { eventRepository.registerToEvent(eventId.toInt()) }
                .andThen(socket.connectToUpdates())
                .andThen(eventRepository.getEvent(eventId))
                .performOnBackgroundOutOnMain()
                .withCustomLoading(viewState)
                .subscribeSimple(
                    onError = { onReceiveError(it) },
                    onSuccess = {
                        viewState.showEventRegistrationSuccessDialog()
                        appData.sendUpdateEvent(it)
                    })
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

    override fun onDataChange(field: EventRegisterField<*>) {
        if (field.isValid()) invalidFieldsData.remove(field) else invalidFieldsData.add(field)
        checkDataValid()
    }


    override fun onNavigateUpClick() {
        viewState.hideKeyboard()
        val fields = fieldsData.filter { x -> x.field.type != EventFormFieldModel.Type.PREFILLED }
        if (fields.filter { f -> f.value != null }.isNullOrEmpty()) viewState.navigateUpClick()
        else viewState.showSaveFormResultDraftDialog()
    }


    override fun onSuccessCancel() = viewState.navigateUpClick()

    override fun onSuccessGoToList() = viewState.showEventLists()

    override fun onAddFileClick(field: EventRegisterField<EventFile?>) =
        viewState.openFileSelector(field)

    override fun onTakeFile(field: EventRegisterField<EventFile?>) =
        takeFileRequest(rxTakePhoto.takeFile(), field)

    override fun onTakeImage(field: EventRegisterField<EventFile?>) =
        takeFileRequest(rxTakePhoto.takeImage(), field)

    private fun takeFileRequest(request: Observable<Result>, field: EventRegisterField<EventFile?>) {
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

                    val availableExtensions = field.field.parameters?.extensions ?: emptyList()
                    val contains = availableExtensions.isEmpty() || availableExtensions.find {
                        val availableExtension = it.lowercase(Locale.getDefault())
                        availableExtension == fileExtension || fileExtension == "jpg" && availableExtension == "jpeg"
                    } != null

                    if (contains) {
                        field.value = EventFile("", path, fileName, fileExtension)
                        viewState.updateFileField(field.field.id.toString())
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
                    addFormDataPart("form", eventData.getFormId().toString())
                    addFormDataPart("isDraft", isDraft.toString())

                    formFields.filter { it.excludeTypes() }.forEachIndexed { index, fieldData ->
                        val key = fieldData.field.id.toString()
                        val value = fieldData.value ?: return@forEachIndexed

                        if (fieldData is EventRegisterField.File) {
                            val path = (value as EventFile).path
                            addFormDataPart("fields[$index][id]", key)
                            if (checkHttpScheme(path)) {
                                value.getReadBytes(contentResolver) { body ->
                                    val name = "${value.name}.${value.mimeType}"
                                    addFormDataPart("fields[$index][value]", name, body)
                                }
                            } else addFormDataPart("fields[$index][value]", value.id)

                        } else if (fieldData is EventRegisterField.Passport) {
                            val passport = value as EventPassport
                            if (!passport.isDataComplete()) return@forEachIndexed
                            addFormDataPart("fields[$index][id]", key)
                            addFormDataPart("fields[$index][value][series]", passport.series ?: "")
                            addFormDataPart("fields[$index][value][number]", passport.number ?: "")
                            addFormDataPart("fields[$index][value][issuedBy]", passport.issuedBy ?: "")
                            addFormDataPart("fields[$index][value][issuedDepartment]", passport.issuedDepartment ?: "")
                            addFormDataPart("fields[$index][value][issuedDate]", passport.issuedDate ?: "")

                        } else if (value is Iterable<*> && value.count() > 0) {
                            addFormDataPart("fields[$index][id]", key)
                            value.filterNotNull().forEach {
                                addFormDataPart("fields[$index][value]", it.toString())
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


    private fun getProfileFieldsData(): Maybe<Optional<EventRegisterField<*>>> {
        val data = fieldsData.find { x -> x.field.type == EventFormFieldModel.Type.PREFILLED }

        return if (data != null && data is EventRegisterField.Prefilled) {
            eventRepository.getPrefilledEventFormResult(data.field.id.toString())
                .map { it.fields.prefilledToJson() }
                .flatMapMaybe {
                    val prefilled = it.prefFromJson(data.field)
                    if (prefilled == data.value) Maybe.just(Optional(null))
                    else fieldsData.find { x -> x.field.id == data.field.id }.let { f ->
                        if (f is EventRegisterField.Prefilled) f.value = prefilled
                        Maybe.just(f.asOptional()).withDelay(400)
                    }
                }.onErrorResumeNext(Maybe.just(Optional(null)))

        } else Maybe.just(Optional(null))
    }

    private fun getRegisterDraftsData(): Maybe<EventRegisterData> {
        return eventRepository.getEventFormResultDraft(eventData.getFormId(), emptyMap())
            .map { result ->
                val list = eventData.event.formFields
                eventData.apply {
                    hasDraft = !result.fields.isNullOrEmpty()
                    addDraftFields(EventRegisterField.createFieldsData(list, result.fields))
                }
            }.onErrorResumeNext(Maybe.just(eventData))
    }

    private fun checkHttpScheme(path: Uri): Boolean {
        return path.scheme?.startsWith("https") != true && path.scheme?.contains("https") != true
    }

    private fun EventRegisterField<*>.excludeTypes(): Boolean {
        return this !is EventRegisterField.Prefilled && this !is EventRegisterField.Title
    }

    companion object {
        private const val API_ERROR_ALREADY_APPROVED = "Registration is approved before"
        private const val API_ERROR_REGISTRATION_CLOSED = "registration is not carried out"
    }
}
