package com.example.ui.partner

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Optional
import com.example.data.models.PartnerModel
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.util.loadBitmap
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
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
        compositeDisposable += eventRepository.getPartnerDetails(dataPartnerId)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .flatMap {
                    Maybe.zip(it.logo?.uri.loadBitmap(), it.image?.uri.loadBitmap(), BiFunction<Optional<Bitmap>, Optional<Bitmap>, PartnerAndImages> { logo, bg ->
                        PartnerAndImages(it, logo.value, bg.value)
                    })
                            .toSingle()
                }
                .subscribeSimple {
                    viewState.apply {
                        setData(it.partner, it.logo, it.background)
                    }
                }
    }

    private class PartnerAndImages(
            val partner: PartnerModel,
            val logo: Bitmap?,
            val background: Bitmap?
    )
}
