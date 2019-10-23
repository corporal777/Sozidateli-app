package com.example.ui.event.contacts

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EmailAffiliation
import com.example.data.models.MapInfo
import com.example.data.models.PhoneAffiliation
import com.example.data.models.Place
import com.example.holders.ActionButtonItem
import com.example.holders.ActionButtonItem.Companion.ACTION_SHOW_ON_MAP
import com.example.holders.ProfileFieldTextItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.location.EventLocationFragmentArgs
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_event_contacts.*
import javax.inject.Inject
import javax.inject.Provider

class EventContactsFragment : BaseFragment(), EventContactsContract.View, ToolbarFragment {

    override val title: String
        get() = getString(R.string.about_event_contacts)

    @InjectPresenter
    lateinit var presenter: EventContactsPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventContactsPresenter>

    @ProvidePresenter
    fun providePresenter(): EventContactsPresenter = presenterProvider.get().apply {
        val args = EventContactsFragmentArgs.fromBundle(arguments!!)
        eventName = args.eventName
        phones = args.phones.toList()
        emails = args.emails.toList()
        webLinks = args.webLinks.toList()
        socialLinks = args.socialLinks.toList()
        address = args.address
        mapInfo = args.mapInfo
        places = args.places
    }

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = groupAdapter
        }
    }

    override fun setData(phones: List<PhoneAffiliation>, emails: List<EmailAffiliation>, webLinks: List<String>, socialLinks: List<String>, address: String?, canShowOnMap: Boolean) {
        groupAdapter.update(mutableListOf<Item>().apply {
            addAll(phones.map { ProfileFieldTextItem(it.affiliation ?: "", it.phone) })
            addAll(emails.map { ProfileFieldTextItem(it.affiliation ?: "", it.email) })
            if (webLinks.isNotEmpty())
                add(ProfileFieldTextItem(getString(R.string.event_contacts_site), webLinks.joinToString("\n")))
            if (socialLinks.isNotEmpty())
                add(ProfileFieldTextItem(getString(R.string.event_contacts_social_networks), socialLinks.joinToString("\n")))
            if (!address.isNullOrEmpty())
                add(ProfileFieldTextItem(getString(R.string.event_contacts_address), address))

            if (canShowOnMap)
                add(ActionButtonItem(size.toLong(), ACTION_SHOW_ON_MAP) {
                    presenter.onShowOnMapClick()
                })
        })
    }

    override fun showMap(eventName: String, mapInfo: MapInfo?, places: Array<Place>?) {
        findNavController().navigate(R.id.event_location, EventLocationFragmentArgs.Builder(eventName, mapInfo, places).build().toBundle())
    }

    override fun layout() = R.layout.fragment_event_contacts
}
