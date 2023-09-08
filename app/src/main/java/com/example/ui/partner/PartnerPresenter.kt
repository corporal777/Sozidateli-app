package com.example.ui.partner

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.PartnerModel
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import javax.inject.Inject

@InjectViewState
class PartnerPresenter
@Inject constructor(
    private val eventRepository: EventRepository,
    appData: AppData
) : BasePresenter<PartnerContract.View>(appData), PartnerContract.Presenter {

    lateinit var dataEventId: String
    lateinit var dataPartnerId: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadData()
    }

    private fun loadData() {
        compositeDisposable += eventRepository.getPartnerDetails(dataPartnerId)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.setData(it)
            }
    }

    override fun onRefreshRequest() = loadData()


    private class PartnerAndImages(
        val partner: PartnerModel,
        val logo: Bitmap?,
        val background: Bitmap?
    )
}
