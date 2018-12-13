package com.example.ui.map

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class MapPresenter
@Inject constructor(
        appData: AppData
) : BasePresenter<MapContract.View>(), MapContract.Presenter {

    private val place = appData.event!!.place

    override fun onMapReady() {
        val coordinates = place.coordinates
        viewState.setMarker(coordinates.first(), coordinates.last())
        viewState.setDescription(place.howToGet)
    }
}
