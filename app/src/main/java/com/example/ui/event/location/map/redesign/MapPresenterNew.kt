package com.example.ui.event.location.map.redesign

import android.Manifest
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.MapInfo
import com.example.ui.base.BasePresenter
import com.example.ui.event.location.map.MapContract
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class MapPresenterNew
@Inject constructor(
    private val rxPermissions: RxPermissions,
    appData: AppData
) : BasePresenter<MapContractNew.View>(appData), MapContractNew.Presenter {

    var mapInfo: MapInfo? = null

    private var mapInitializeDisposable: Disposable? = null
    private var isMapContentSet = false

    override fun attachView(view: MapContractNew.View?) {
        super.attachView(view)
        viewState.setDescription(mapInfo?.title, mapInfo?.description)

        val lat = mapInfo?.lat
        val lon = mapInfo?.lon

        viewState.initializeMap()
        if (lat != null && lon != null) {
            compositeDisposable += Completable.fromAction { viewState.initializeMap() }
                .withLoadingDialog(viewState)
                .subscribe()
                .apply { mapInitializeDisposable = this }
        } else {
            viewState.showContent()
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

    }

    override fun onMapReady() {
        mapInitializeDisposable?.dispose()
        if (isMapContentSet) {
            viewState.showContent()
            return
        }

        val setContent: (Boolean) -> Unit = {
            isMapContentSet = true
            viewState.apply {
                enableCurrentLocation(it)
                setMarker(mapInfo?.lat!!, mapInfo?.lon!!)
                showContent()
            }
        }
        compositeDisposable += rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe({
                setContent(it)
            }, {
                setContent(false)
            })
    }

    override fun onShareClick() {
        viewState.shareUrl("$GOOGLE_MAP_SHARE_URL${mapInfo?.lat},${mapInfo?.lon}")
    }

    override fun onOpenRouteClick() {
        viewState.openUrl("$GOOGLE_MAP_ROUTE_URL${mapInfo?.lat},${mapInfo?.lon}")
    }

    companion object {
        private const val GOOGLE_MAP_SHARE_URL = "https://www.google.com/maps/search/?api=1&query="
        private const val GOOGLE_MAP_ROUTE_URL = "geo:0,0?mode=d&q="
    }

}