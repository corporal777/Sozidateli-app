package com.example.ui.partner

import com.arellomobile.mvp.InjectViewState
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class PartnerPresenter
@Inject constructor(
        private val eventRepository: EventRepository
) : BasePresenter<PartnerContract.View>(), PartnerContract.Presenter {

    lateinit var dataEventId: String
    lateinit var dataPartnerId: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += eventRepository.getPartnerById(dataEventId, dataPartnerId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    viewState.apply {
                        setTitle(it.name)
                        setData(it)
                    }
                }
    }
}
