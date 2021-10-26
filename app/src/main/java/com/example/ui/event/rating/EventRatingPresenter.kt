package com.example.ui.event.rating

import android.Manifest
import android.content.ContentResolver
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
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
class EventRatingPresenter
@Inject constructor(
        private val eventRepository: EventRepository,
        private val rxPermissions: RxPermissions,
        private val contentResolver: ContentResolver,
        appData: AppData
) : BasePresenter<EventRatingContract.View>(appData), EventRatingContract.Presenter {

    lateinit var eventId: String

    private var fieldsData: List<EventRegisterFieldData<*>> = emptyList()
    private var invalidFieldsData: MutableSet<EventRegisterFieldData<*>> = mutableSetOf()
    private var takeFileMaybe: MaybeSubject<Uri>? = null

    private var rating = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.enableActionButton(true)

        compositeDisposable += eventRepository.loadEventRatingData(eventId)
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
                }
    }

    override fun onDataChange(field: EventRegisterFieldData<*>) {
        if (field.isValid()) invalidFieldsData.remove(field) else invalidFieldsData.add(field)
        checkDataValid()
    }

    override fun onSendClick() {
        compositeDisposable += Single.fromCallable {
            val fieldsData = fieldsData
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
                    .build()
        }
                .flatMapCompletable { eventRepository.setEventRating(eventId, it) }
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onComplete = {
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

    override fun onRatingChange(rating: Int) {
        this.rating = rating
        checkDataValid()
    }
}
