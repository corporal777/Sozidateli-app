package com.example.ui.search.code

import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class EnterCodePresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    appData: AppData
) : BasePresenter<EnterCodeContract.View>(appData), EnterCodeContract.Presenter {


    override fun onSearchClick(code: String) {
        compositeDisposable += eventRepository.getEventsList(
            mapOf(
                EventNew.EVENT_LIMIT to 1, EventNew.EVENT_OFFSET to 0,
                EventNew.EVENT_BINDS to "rights,organization,tag,page,activity,user-registration,user-form-result,current-user-registration,destination-scheme,eventRegistrationState",
                EventNew.EVENT_CODE to code
            )
        )
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = {
                    viewState.showEventNotFoundError()
                    it.printStackTrace()
                }, onSuccess = {
                    if (it.data.isNotEmpty()) {
                        viewState.showEvent(it.data[0]?.id.toString())
                    } else {
                        viewState.showEventNotFoundError()
                    }
                }
            )
    }
}
