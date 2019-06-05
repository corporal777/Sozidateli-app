package com.example.ui.search.enterCode

import call
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class EnterCodePresenter
@Inject constructor(private val eventRepository: EventRepository) : BasePresenter<EnterCodeContract.View>(), EnterCodeContract.Presenter {

    override fun onSearchClick(code: String) {
        eventRepository.getEventList(limit = 1, offset = 0, qr = code)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    if (it.data.isNotEmpty()) {
                        viewState.showEvent(it.data[0].event)
                    } else {
                        viewState.showErrorDialog(listOf(R.string.by_qr_not_found_event), null)
                    }
                }, { it.printStackTrace() }).call(compositeDisposable)
    }
}
