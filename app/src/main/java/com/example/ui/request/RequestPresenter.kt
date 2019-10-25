package com.example.ui.request

import android.Manifest
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.*
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.rxtakephoto.PermissionNotGrantedException
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.MaybeSubject
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class RequestPresenter
@Inject constructor(
        private val eventRepository: EventRepository,
        private val rxPermissions: RxPermissions
) : BasePresenter<RequestContract.View>(), RequestContract.Presenter {

    lateinit var eventId: String

    private var takeFileMaybe: MaybeSubject<String>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.enableActionButton(true)

        val loadFields = eventRepository.getEventRegisterField(eventId)
        val loadRegister = eventRepository.getEventRegister(eventId)

        compositeDisposable += Single.zip(loadFields, loadRegister, BiFunction<RegisterFieldsData, EventRegisterResponse, Pair<RegistrationEvent, List<RegisterEventFieldData<*>>?>> { fields, registration ->
            val fieldsData = fields.fields?.map {
                when (it.type) {
                    RegisterEventField.Type.STRING,
                    RegisterEventField.Type.TEXT_AREA,
                    RegisterEventField.Type.NUMBER -> RegisterEventFieldData.String(it, null)
                    RegisterEventField.Type.DATE,
                    RegisterEventField.Type.DATETIME -> RegisterEventFieldData.Date(it, null)
                    RegisterEventField.Type.CHECKBOX -> RegisterEventFieldData.Checkbox(it, null)
                    RegisterEventField.Type.SELECT_BOX -> RegisterEventFieldData.SelectBox(it, null)
                    RegisterEventField.Type.RADIO_BOX -> RegisterEventFieldData.RadioBox(it, null)
                    RegisterEventField.Type.FILE -> RegisterEventFieldData.File(it, null)
                    RegisterEventField.Type.BOOLEAN -> RegisterEventFieldData.Boolean(it, null)
                    RegisterEventField.Type.PASSPORT -> RegisterEventFieldData.Passport(it, null)
                }
            }

            registration.event to fieldsData
        })
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple({
                    it.printStackTrace()
                }) {
                    viewState.setFields(it.first, it.second ?: emptyList())
                }
    }

    override fun onDataChange(field: String, value: Any?, fieldForRemove: String?) {

    }

    override fun onRegisterClick() {

    }

    override fun onGoTeEventListClick() {
        viewState.navigateUp()
    }

    override fun onPersonalDataFileClick(url: String) {
        viewState.openUrl(url)
    }

    override fun onAddFileClick(fieldId: String) {
        takeFileMaybe?.onComplete()
        compositeDisposable += rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .flatMapMaybe {
                    if (it) {
                        viewState.openFileSelector()
                        MaybeSubject.create<String>().apply { takeFileMaybe = this }
                    } else Maybe.error<String>(PermissionNotGrantedException())
                }
                .subscribeSimple {
                    viewState.updateFileField(fieldId, it)
                }
    }

    override fun onFileSelected(path: String) {
        takeFileMaybe?.onSuccess(path)
    }

    override fun onFileSelectionCancel() {
        takeFileMaybe?.onComplete()
    }

    override fun onCloseClick() = viewState.navigateUp()
}
