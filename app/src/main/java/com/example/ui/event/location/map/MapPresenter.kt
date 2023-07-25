package com.example.ui.event.location.map

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.MapInfo
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class MapPresenter
@Inject constructor(
    appData: AppData
) : BasePresenter<MapContract.View>(appData), MapContract.Presenter {

    var mapInfo: MapInfo? = null

    private var mapInitializeDisposable: Disposable? = null
    private var isMapContentSet = false

    override fun attachView(view: MapContract.View?) {
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

    override fun onMapReady() {
        mapInitializeDisposable?.dispose()
        if (isMapContentSet) {
            viewState.showContent()
            return
        }
        isMapContentSet = true
        viewState.apply {
            setMarker(mapInfo?.lat!!, mapInfo?.lon!!)
            showContent()
        }
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