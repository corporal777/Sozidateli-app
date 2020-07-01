package com.example.ui.event.contacts

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.text.style.URLSpan
import android.view.View
import android.widget.Toast
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EmailAffiliation
import com.example.data.models.MapInfo
import com.example.data.models.PhoneAffiliation
import com.example.data.models.Place
import com.example.extensions.parsePhone
import com.example.holders.ProfileFieldTextItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_event_contacts.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.roundToInt

class EventContactsFragment : BaseFragment(), EventContactsContract.View, ToolbarFragment {

    companion object {
        private val MAP_OPTIONS_DEFAULT = GoogleMapOptions()
                .liteMode(true)
                .mapToolbarEnabled(false)
                .zoomControlsEnabled(false)

        private const val MAP_TAG = "com.example.ui.event.contacts.EventContactsFragment.SupportMapFragment"
    }

    override val title: String? = null

    @InjectPresenter
    lateinit var presenter: EventContactsPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventContactsPresenter>

    @ProvidePresenter
    fun providePresenter(): EventContactsPresenter = presenterProvider.get().apply {
        val args = EventContactsFragmentArgs.fromBundle(requireArguments())
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

    private val mapFragment by lazy {
        SupportMapFragment.newInstance(MAP_OPTIONS_DEFAULT)
    }

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = groupAdapter
        }
    }

    override fun setData(
            phones: List<PhoneAffiliation>,
            emails: List<EmailAffiliation>,
            webLinks: List<String>,
            socialLinks: List<String>,
            address: String?,
            place: String?,
            mapInfo: MapInfo?,
            places: Array<Place>?
    ) {
        groupAdapter.update(mutableListOf<Item>().apply {
            addAll(phones.map {
                ProfileFieldTextItem(it.affiliation ?: "", it.phone.parsePhone(requireContext()))
            })
            addAll(emails.map { ProfileFieldTextItem(it.affiliation ?: "", it.email) })
            if (webLinks.isNotEmpty()) {
                add(ProfileFieldTextItem(getString(R.string.event_contacts_site), webLinks.joinToString("\n")))
            }
            if (socialLinks.isNotEmpty()) {
                add(ProfileFieldTextItem(getString(R.string.event_contacts_social_networks), socialLinks.joinToString("\n")))
            }
            if (!address.isNullOrEmpty()) {
                val addressLink = object : URLSpan("") {
                    override fun onClick(widget: View) {
                        presenter.onOpenAddressClick()
                    }
                }
                val addressClickable = address.toSpannable().apply {
                    set(0, address.length, addressLink)
                }
                add(ProfileFieldTextItem(getString(R.string.event_contacts_address), addressClickable))
            }
            if (!place.isNullOrEmpty()) {
                add(ProfileFieldTextItem(getString(R.string.event_contacts_place), place))
            }

        })

        setupMap(mapInfo)
    }

    private fun setupMap(mapInfo: MapInfo?) {
        if (mapInfo != null) {
            val lat = mapInfo.lat
            val lon = mapInfo.lon
            if (lat != null && lon != null) {
                if (childFragmentManager.findFragmentByTag(MAP_TAG) == null) {
                    childFragmentManager.beginTransaction()
                            .replace(R.id.flMapContainer, mapFragment, MAP_TAG)
                            .commitNow()
                }

                mapFragment.getMapAsync {
                    it.clear()

                    val latLng = LatLng(mapInfo.lat, mapInfo.lon)
                    it.addMarker(MarkerOptions().position(latLng))
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                }

                flMapContainer.apply {
                    updateLayoutParams {
                        val dh = Resources.getSystem().displayMetrics.heightPixels / 1.8f
                        height = dh.roundToInt()
                    }
                }

                btnShare.setOnClickListener { presenter.onShareClick() }
                btnGoTo.setOnClickListener { presenter.onOpenRouteClick() }

                llMapAction.isVisible = true
            } else {
                llMapAction.isVisible = false
            }

            val title = mapInfo.title
            tvMapDescriptionTitle.apply {
                text = title
                isVisible = !title.isNullOrEmpty()
            }

            val description = mapInfo.description
            tvMapDescription.apply {
                text = description
                isVisible = !description.isNullOrEmpty()
            }

            llMapContent.isVisible = true
        } else {
            llMapContent.isVisible = false
        }
    }

    override fun shareUrl(url: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, url)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.map_sharing)))
        } catch (e: Throwable) {
            Toast.makeText(requireContext(), R.string.map_sharing_error, Toast.LENGTH_LONG).show()
        }
    }

    override fun openUrl(url: String) {
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(requireContext(), R.string.map_route_error, Toast.LENGTH_LONG).show()
        }
    }

    override fun layout() = R.layout.fragment_event_contacts
}
