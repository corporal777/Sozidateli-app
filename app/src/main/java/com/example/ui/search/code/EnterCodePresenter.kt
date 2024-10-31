package com.example.ui.search.code

import com.example.data.AppData
import com.example.data.models.EventNew
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class EnterCodePresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    appData: AppData
) : BasePresenter<EnterCodeContract.View>(appData), EnterCodeContract.Presenter {


    override fun onSearchClick(code: String) {
        compositeDisposable += eventRepository.getEventByCode(code)
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = {
                    viewState.showEventNotFoundError()
                    it.printStackTrace()
                }, onSuccess = {
                    viewState.showEvent(it.id.toString())
                }
            )

    }
}
