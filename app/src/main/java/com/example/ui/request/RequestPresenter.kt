package com.example.ui.request

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.operators.completable.CompletableFromAction
import okhttp3.MediaType
import okhttp3.RequestBody
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.io.File
import javax.inject.Inject
import android.content.DialogInterface
import com.example.R
import com.example.data.models.EventRegisterResponse
import com.example.data.models.RegisterFieldResponse
import com.example.events.OnUpdateMyEventsEvent
import com.example.util.PART_ERROR_REQUEST_EVENT_FIELD_REQUIRED
import com.example.util.PART_ERROR_REQUEST_EVENT_FILE_ERROR
import com.example.util.PART_ERROR_REQUEST_EVENT_REGISTER_END
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import okhttp3.MultipartBody
import org.greenrobot.eventbus.EventBus


@InjectViewState
class RequestPresenter
@Inject constructor(
        private val eventRepository: EventRepository
) : BasePresenter<RequestContract.View>(), RequestContract.Presenter {


    lateinit var event: Event

    private var data = HashMap<String, RequestBody?>()
    private var files = HashMap<String, MultipartBody.Part?>()

    private var currentFileSelectorPosition = -1

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.enableActionButton(true)

        val loadCustField = eventRepository.getEventRegisterField(event.id)
        val loadRegister = eventRepository.getEventRegister(event.id)


        Single.zip(loadCustField, loadRegister, BiFunction<RegisterFieldResponse, EventRegisterResponse, Pair<RegisterFieldResponse, EventRegisterResponse>> { t1, t2 ->
            Pair(t1, t2)
        }).map {
            it.second.custom_fields?.let { hashFields ->
                it.first.fields?.let { arrayFields ->
                    arrayFields.forEach {
                        it.dataFromServer = hashFields[it.field_id]
                    }
                }
                it.first.selectedCategory = it.second.group
            }
            return@map it.first
        }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setFields(it)
                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)
    }

    override fun onDataChange(field: String, value: Any?, fieldForRemove: String?) {
        val requestBody: RequestBody

        if (value != null) {
            if (value is File) {
                requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), value)
                files[field] = MultipartBody.Part.createFormData(field, value.name, requestBody)
                //data.remove(fieldForRemove)
            } else {
                requestBody = RequestBody.create(MediaType.parse("text/plain"), value.toString())
                data[field] = requestBody
            }
        }

        if (value == null) {
            data.remove(field)
            files.remove(field)
        }
    }

    override fun onClickOpenFileSelector(position: Int) {
        currentFileSelectorPosition = position
        viewState.openFileSelector()
    }

    override fun onFileSelected(path: String) {
        CompletableFromAction.complete()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    viewState.updateFileField(currentFileSelectorPosition, path)
                }.call(compositeDisposable)
    }

    override fun onRegisterClick() {
        eventRepository.eventRegister(event.id, data, files.map { it.value })
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.showSuccessRegister()
                    EventBus.getDefault().postSticky(OnUpdateMyEventsEvent())
                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)
    }

    override fun onError(errors: List<String>) {
        val messageIds = linkedSetOf<Int>()
        var hasImportantError = false
        errors.forEach {
            if (it.contains(PART_ERROR_REQUEST_EVENT_REGISTER_END)) {
                hasImportantError = true
                messageIds.add(R.string.request_event_register_end)
            } else if (it.contains(PART_ERROR_REQUEST_EVENT_FIELD_REQUIRED)) {
                messageIds.add(R.string.request_event_required_field)
            } else if(it.contains(PART_ERROR_REQUEST_EVENT_FILE_ERROR)){
                messageIds.add(R.string.request_event_file_error)
            }
        }
        if(hasImportantError){
            viewState.showErrorDialog(messageIds.toList(), DialogInterface.OnDismissListener { viewState.navigateUp() })
        } else{
            viewState.showToast(messageIds.toList())
        }
    }

    override fun onGoTeEventListClick() {
        viewState.navigateUp()
    }

    override fun onCloseClick() = viewState.navigateUp()
}
