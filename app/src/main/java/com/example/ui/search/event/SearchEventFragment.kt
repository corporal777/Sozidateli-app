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
import com.example.data.models.*
import com.example.extensions.getAffiliationString
import com.example.holders.EventDataListItem
import com.example.holders.EventGroup
import com.example.holders.EventStatusItem
import com.example.holders.PlaceholderItem
import com.example.ui.event.about.AboutEventFragment.Companion.ABOUT_FROM_OTHER
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.search.SearchFragment
import com.example.ui.views.StateType
import com.xwray.groupie.Group
import initDropDownView
import kotlinx.android.synthetic.main.layout_filter_event.view.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class SearchEventFragment : SearchFragment<SearchEventPresenter, EventNew, SearchFilter.EventNew>(), SearchEventContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchEventPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchEventPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchEventPresenter = presenterProvider.get()

    private val onEventClickListener = object : EventStatusItem.OnEventClickListener {
        override fun onActionRegister(event: String) = presenter.onActionRegister(event)
        override fun onActionShowEvent(event: String) = presenter.onActionShowEvent(event)
        override fun onActionCancel(event: String, registrationId: String?) = presenter.onActionCancel(event, registrationId)
        override fun onActionWriteToOrganization(emails: List<EventPhoneModel>) = presenter.onActionWriteToOrganization(emails)
        override fun onShowEventClick(view: View, event: String) = presenter.onShowEventClick(event)
        override fun onShowFilterClick(format: Int) = presenter.onShowFormatClick(format)
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
    }

    override fun showWriteToOrganizationEmails(emails: List<EventPhoneModel>) {
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

    override fun showWriteToOrganization(email: EventPhoneModel) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email.value))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(R.id.about_event_fragment, AboutEventFragmentArgs.Builder(event, ABOUT_FROM_OTHER).build().toBundle())
    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(R.id.request_fragment, EventRegistrationFragmentArgs.Builder(event).build().toBundle())
    }

    override fun selectEvent() {
        findNavController().navigate(R.id.event_tabs_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build())
    }

    override fun createItem(itemData: EventNew?): Group {
        return if (itemData == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
        else EventGroup(
                itemData.id.toString(),
                itemData.status?.value,
                itemData.binds?.currentUserRegistration?.status?.value,
                itemData.binds?.organization?.backgroundColor?.value,
                itemData.image?.uri,
                EventFormat(name = if (itemData.format?.name.isNullOrEmpty()) itemData.format?.custom?: "" else itemData.format?.name?: ""),
                itemData.binds?.organization?.email,
                (itemData.status?.value?: "") != Event.Status.REGISTRATION,
                onEventClickListener,
                EventDataListItem(
                        -(itemData.id?.toLong()?: 0),
                        itemData.name,
                        itemData.address?.getShortAddress(),
                        itemData.holdingDate?.from,
                        itemData.binds?.getFirstActionStartDate()
                ).apply {
                    showStartTime = false
                },
                itemData.userAgreement?.name?: itemData.userAgreement?.uri,
                itemData.binds?.eventRegistrationState
        )
    }

    @SuppressLint("InflateParams")
    override fun createFilterView(filter: SearchFilter.EventNew): View {
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
                        { it.name?: "" },
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