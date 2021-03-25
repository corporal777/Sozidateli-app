package com.example.ui.event.contacts

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.*
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class EventContactsPresenter
@Inject constructor() : BasePresenter<EventContactsContract.View>(), EventContactsContract.Presenter {

    companion object {
        private const val GOOGLE_MAP_SHARE_URL = "https://www.google.com/maps/search/?api=1&query="
        private const val GOOGLE_MAP_ROUTE_URL = "geo:0,0?mode=d&q="
    }

    lateinit var eventName: String
    lateinit var phones: List<EventPhoneModel>
    lateinit var emails: List<EventPhoneModel>
    lateinit var webLinks: List<String>
    lateinit var socialLinks: List<String>
    var address: String? = null
    var place: String? = null
    var mapInfo: MapInfo? = null
    var places: Array<Place>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(
                phones,
                emails,
                webLinks,
                socialLinks,
                address,
                place,
                mapInfo,
                places
        )
    }

    override fun onShareClick() {
        val mapInfo = this.mapInfo ?: return
        viewState.shareUrl("${GOOGLE_MAP_SHARE_URL}${mapInfo.lat},${mapInfo.lon}")
    }

    override fun onOpenRouteClick() {
        val mapInfo = this.mapInfo ?: return
        viewState.openUrl("${GOOGLE_MAP_ROUTE_URL}${mapInfo.lat},${mapInfo.lon}")
    }

    override fun onOpenAddressClick() {
        val address = this.address ?: return
        viewState.openUrl("${GOOGLE_MAP_SHARE_URL}${address}")
    }
}
