package com.example.ui.partner

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Optional
import com.example.data.models.Partner
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
                .flatMap {
                    Maybe.zip(it.logo.loadBitmap(), it.background.loadBitmap(), BiFunction<Optional<Bitmap>, Optional<Bitmap>, PartnerAndImages> { logo, bg ->
                        PartnerAndImages(it, logo.value, bg.value)
                    })
                            .toSingle()
                }
                .subscribeSimple {
                    viewState.apply {
                        setData(it.partner, it.logo, it.background)
                    }
                }
        /*compositeDisposable += eventRepository.getPartnerDetails(dataPartnerId)
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
                }*/
    }

    private class PartnerAndImages(
            val partner: /*PartnerModel*/Partner,
            val logo: Bitmap?,
            val background: Bitmap?
    )
}
