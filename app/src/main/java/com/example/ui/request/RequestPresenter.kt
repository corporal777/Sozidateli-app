package com.example.ui.request

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.data.models.EventRegisterResponse
import com.example.data.models.RegisterFieldResponse
import com.example.events.OnUpdateMyEventsEvent
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.internal.operators.completable.CompletableFromAction
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.io.File
import javax.inject.Inject

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
            it.second.custom_fields?.let { fillingFieldArray ->
                fillingFieldArray.forEach { fillingField ->

                    it.first.fields?.let { arrayFields ->
                        arrayFields.forEach {
                            if (fillingField.field_id == it.field_id) {
                                it.dataFromServer = fillingField
                            }
                        }
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

    override fun onGoTeEventListClick() {
        viewState.navigateUp()
    }

    override fun onCloseClick() = viewState.navigateUp()
}
