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
    private var mDy = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(0f)
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

    override fun attachView(view: PartnerContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(Math.abs(mDy / 10f))
    }

    override fun changeAppBarElevation(value: Int) {
        mDy += value
        viewState.setAppBarElevation(Math.abs(mDy / 10f))
    }

    private class PartnerAndImages(
            val partner: PartnerModel,
            val logo: Bitmap?,
            val background: Bitmap?
    )
}
