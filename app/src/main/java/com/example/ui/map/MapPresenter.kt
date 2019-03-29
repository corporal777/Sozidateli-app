package com.example.ui.map

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class MapPresenter
@Inject constructor(
        userEventData: UserEventData
) : BasePresenter<MapContract.View>(), MapContract.Presenter {

    private val place = userEventData.event!!.place

    override fun onMapReady() {
        val coordinates = place?.coordinates
        coordinates?.let {
            viewState.setMarker(coordinates.first(), coordinates.last())
        }
        place?.let {
            viewState.setDescription(it.howToGet)
        }

    }
}
