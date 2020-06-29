package com.example.ui.search.event

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EmailAffiliation
import com.example.data.models.Event
import com.example.data.models.SearchFilter
import com.example.data.models.takeFormat
import com.example.extensions.getAffiliationString
import com.example.holders.EventDataListItem
import com.example.holders.EventGroup
import com.example.holders.EventStatusItem
import com.example.holders.PlaceholderItem
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.search.SearchFragment
import com.xwray.groupie.Group
import initDropDownView
import kotlinx.android.synthetic.main.layout_filter_event.view.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class SearchEventFragment : SearchFragment<SearchEventPresenter, Event, SearchFilter.Event>(), SearchEventContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchEventPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchEventPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchEventPresenter = presenterProvider.get()

    private val onEventClickListener = object : EventStatusItem.OnEventClickListener {
        override fun onActionRegister(event: String) = presenter.onActionRegister(event)
        override fun onActionShowEvent(event: String) = presenter.onActionShowEvent(event)
        override fun onActionCancel(event: String) = presenter.onActionCancel(event)
        override fun onActionWriteToOrganization(emails: List<EmailAffiliation>) = presenter.onActionWriteToOrganization(emails)
        override fun onShowEventClick(view: View, event: String) = presenter.onShowEventClick(event)
        override fun onShowFilterClick(format: Int) = presenter.onShowFormatClick(format)
    }

    override fun showWriteToOrganizationEmails(emails: List<EmailAffiliation>) {
        AlertDialog.Builder(requireContext())
                .setItems(
                        emails.map { it.getAffiliationString(underlinedEmail = true) }
                                .toTypedArray()
                ) { dialog, which ->
                    val email = emails[which]
                    presenter.onWriteToOrganizationEmailChosen(email)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun showWriteToOrganization(email: EmailAffiliation) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email.email))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(R.id.about_event_fragment, AboutEventFragmentArgs.Builder(event).build().toBundle())
    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(R.id.request_fragment, EventRegistrationFragmentArgs.Builder(event).build().toBundle())
    }

    override fun selectEvent() {
        findNavController().navigate(R.id.event_tabs_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build())
    }

    override fun createItem(itemData: Event?): Group {
        return if (itemData == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
        else EventGroup(
                itemData.id,
                itemData.status,
                itemData.userRegistration,
                itemData.backgroundColor,
                itemData.backgroundImage,
                itemData.takeFormat(),
                itemData.organization?.emails,
                onEventClickListener,
                EventDataListItem(
                        -itemData.id.toLong(),
                        itemData.name,
                        itemData.shortAddress ?: itemData.addressCity,
                        itemData.conferenceStart,
                        itemData.conferenceFirstActivityStart
                )
        )
    }

    @SuppressLint("InflateParams")
    override fun createFilterView(filter: SearchFilter.Event): View {
        return layoutInflater.inflate(R.layout.layout_filter_event, null).apply {
            etAddress.apply {
                setTextWithoutSearch(filter.address)
                onTextChanged { filter.address = it.toString() }
            }
            initTextFilter(etName, filter.name) { filter.name = it }
            initDateFilter(etStart, tilStart, filter.dateStart) { filter.dateStart = it }
            initDateFilter(etFinish, tilFinish, filter.dateFinish) { filter.dateFinish = it }

            val interests = filter.interests
            if (interests.isNullOrEmpty()) {
                tilTheme.isVisible = false
                tilSpec.isVisible = false
            } else {
                initInterests(interests, tvTheme, tilSpec, tvSpec, filter.theme, filter.spec) { theme, spec ->
                    filter.theme = theme
                    filter.spec = spec
                }
                tilTheme.isVisible = true
                tilSpec.isVisible = true
            }

            val formats = filter.formats
            if (formats.isNullOrEmpty()) {
                tilFormat.isVisible = false
            } else {
                tilFormat.isVisible = true
                initDropDownView(
                        tvFormat,
                        formats,
                        formats.find { it.id == filter.format }?.name,
                        null,
                        { it.name },
                        { it?.id },
                        { filter.format = it }
                )
            }
        }
    }


    override fun clearFilterView(filterView: View) {
        filterView.apply {
            etAddress.text = null
            etName.text = null
            etStart.text = null
            etFinish.text = null
            tvTheme.text = null
            tvSpec.text = null
            tvFormat.text = null
        }
    }
}