package com.example.ui.event.contacts

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.EmailAffiliation
import com.example.data.models.MapInfo
import com.example.data.models.PhoneAffiliation
import com.example.data.models.Place
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class EventContactsPresenter
@Inject constructor() : BasePresenter<EventContactsContract.View>(), EventContactsContract.Presenter {

    lateinit var eventName: String
    lateinit var phones: List<PhoneAffiliation>
    lateinit var emails: List<EmailAffiliation>
    lateinit var webLinks: List<String>
    lateinit var socialLinks: List<String>
    var address: String? = null
    var place: String? = null
    var mapInfo: MapInfo? = null
    var places: Array<Place>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(phones, emails, webLinks, socialLinks, address, place, mapInfo != null || !places.isNullOrEmpty())
    }

    override fun onShowOnMapClick() {
        viewState.showMap(eventName, mapInfo, places)
    }
}
