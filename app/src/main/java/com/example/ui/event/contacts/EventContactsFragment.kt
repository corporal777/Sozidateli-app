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
import com.example.extensions.parsePhone
import com.example.holders.ProfileButtonEditItem
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

    override val title: String? = null

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
        place = args.place
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

    override fun setData(phones: List<PhoneAffiliation>, emails: List<EmailAffiliation>, webLinks: List<String>, socialLinks: List<String>, address: String?, place: String?, canShowOnMap: Boolean) {
        groupAdapter.update(mutableListOf<Item>().apply {
            addAll(phones.map { ProfileFieldTextItem(it.affiliation ?: "", it.phone.parsePhone(requireContext())) })
            addAll(emails.map { ProfileFieldTextItem(it.affiliation ?: "", it.email) })
            if (webLinks.isNotEmpty())
                add(ProfileFieldTextItem(getString(R.string.event_contacts_site), webLinks.joinToString("\n")))
            if (socialLinks.isNotEmpty())
                add(ProfileFieldTextItem(getString(R.string.event_contacts_social_networks), socialLinks.joinToString("\n")))
            if (!address.isNullOrEmpty())
                add(ProfileFieldTextItem(getString(R.string.event_contacts_address), address))
            if (!place.isNullOrEmpty())
                add(ProfileFieldTextItem(getString(R.string.event_contacts_place), place))

            if (canShowOnMap)
                add(ProfileButtonEditItem(size.toLong(), getString(R.string.event_contacts_watch_on_map)) {
                    presenter.onShowOnMapClick()
                }.apply {
                    hasDivider = false
                })
        })
    }

    override fun showMap(eventName: String, mapInfo: MapInfo?, places: Array<Place>?) {
        findNavController().navigate(R.id.event_location_fragment, EventLocationFragmentArgs.Builder(eventName, mapInfo, places).build().toBundle())
    }

    override fun layout() = R.layout.fragment_event_contacts
}
