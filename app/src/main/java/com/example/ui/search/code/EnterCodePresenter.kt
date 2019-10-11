package com.example.ui.search.code

import com.arellomobile.mvp.InjectViewState
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class EnterCodePresenter
@Inject constructor(
        private val eventRepository: EventRepository
) : BasePresenter<EnterCodeContract.View>(), EnterCodeContract.Presenter {

    override fun onSearchClick(code: String) {
        compositeDisposable += eventRepository.getEventByCode(code)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.showEvent(it)
                }, {
                    viewState.showEventNotFoundError()
                    it.printStackTrace()
                })
    }
}
