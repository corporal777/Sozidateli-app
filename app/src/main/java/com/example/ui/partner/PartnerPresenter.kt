package com.example.ui.partner

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Event
import com.example.data.models.Partner
import com.example.repository.DummyRepository
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class PartnerPresenter
@Inject constructor(
        private val eventRepository: EventRepository
) : BasePresenter<PartnerContract.View>(), PartnerContract.Presenter {

    lateinit var partner: Partner

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    override fun attachView(view: PartnerContract.View?) {
        super.attachView(view)
        if (partner.description != null) {
            viewState.setData(partner)
            return
        }
        eventRepository.getPartnerById(partner.id)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    partner = it
                    viewState.setData(it)
                }, {})
                .call(compositeDisposable)
    }
}
