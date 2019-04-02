package com.example.ui.mapTabs.map

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class MapPresenter
@Inject constructor(
        private val userEventData: UserEventData
) : BasePresenter<MapContract.View>(), MapContract.Presenter {


    override fun onMapReady() {
        userEventData.mapInfo?.let {
            viewState.setMarker(it.geo_lat, it.geo_log)

            it.scheme_descriptions?.let { describe ->
                viewState.setDescription(describe)
            }
        }

    }
}
