package com.example.ui.search.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.*
import com.example.databinding.LayoutFilterEventBinding
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventItemNew
import com.example.ui.event.about.redesign.AboutEventFragmentNewArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.search.SearchFragment
import com.example.ui.views.StateType
import com.xwray.groupie.Group
import initDropDownView
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class SearchEventFragment : SearchFragment<SearchEventPresenter, EventNew, SearchFilter.EventNew>(),
    SearchEventContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchEventPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchEventPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchEventPresenter = presenterProvider.get()


    private val onEventClickListener = object : EventItemNew.OnEventClickListener {
        override fun onActionRegister(event: String) = presenter.onActionRegister(event)
        override fun onActionCancel(event: String, registrationId: String?) =
            presenter.onActionCancel(event, registrationId)
        override fun onShowEventClick(view: View, event: String) = presenter.onShowEventClick(event)
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(
            R.id.about_event_fragment_new,
            AboutEventFragmentNewArgs.Builder(event).build().toBundle())
    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(
            R.id.request_fragment,
            EventRegistrationFragmentArgs.Builder(event).build().toBundle()
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun createItem(itemData: EventNew?): Group {
        return if (itemData == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
        else {
            return EventItemNew(
                itemData,
                itemData.id.toString(),
                itemData.state,
                itemData.status?.value,
                itemData.binds?.currentUserRegistration?.status?.value,
                itemData.backgroundColor?.value,
                itemData.image?.uri,
                itemData.binds?.eventRegistrationState,
                itemData.userAgreement?.uri,
                itemData.binds?.currentUserRegistration?.id.toString(),
                itemData.name,
                itemData.address?.getShortAddress(),
                itemData.holdingDate?.from,
                itemData.holdingDate?.to,
                onEventClickListener,
            )
        }
    }


    override fun createFilterView(filter: SearchFilter.EventNew): View {
        return LayoutFilterEventBinding.inflate(LayoutInflater.from(requireContext()), null, false).apply {
            etAddress.apply {
                setTextWithoutSearch(filter.address)
                onTextChanged {
                    filter.address = it.toString()
                    filter.fullAddress = null
                }
                onDataSelectedListener = {
                    filter.fullAddress = it
                }
            }
            initTextFilter(etName, filter.name) { filter.name = it }
            initDateFilter(etStart, tilStart, filter.dateStart) { filter.dateStart = it }
            initDateFilter(etFinish, tilFinish, filter.dateFinish) { filter.dateFinish = it }

            val organizations = filter.organizations
            initOrganizations(filter.org, tilOrganization, tvOrganization, organizations) {
                filter.org = it
            }

            val interests = filter.interests
            if (interests.isNullOrEmpty()) {
                tilTheme.isVisible = false
                tilSpec.isVisible = false
            } else {
                initInterests(
                    interests,
                    tvTheme,
                    tilSpec,
                    tvSpec,
                    filter.theme,
                    filter.spec
                ) { theme, spec ->
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
                    { it.name ?: "" },
                    { it?.id },
                    { filter.format = it }
                )
            }
        }.root
    }

    private fun initOrganizations(
        selectedOrganizationId : Long?,
        inputLayout: View,
        textView: AutoCompleteTextView,
        organizations: List<OrganizationNew?>?,
        onOrgChange: (organization: Long?) -> Unit
    ) {
        if (organizations.isNullOrEmpty()) {
            textView.isEnabled = false
            textView.text = null
            inputLayout.isEnabled = false
        } else {
            val selected = organizations.find { x -> x?.id == selectedOrganizationId }
            initDropDownView(
                textView,
                organizations,
                selected?.legalInformation?.name?.short,
                null,
                transformKey = { it?.legalInformation?.name?.short ?: "" },
                findValue = { it?.id },
                onVariantChange = { onOrgChange(it) })
            textView.isEnabled = true
            inputLayout.isEnabled = true
        }
    }


    override fun clearFilterView(filterView: View) {
        LayoutFilterEventBinding.bind(filterView).apply {
            etName.text = null
            etAddress.text = null
            tvFormat.text = null
            tvOrganization.text = null
            tvTheme.text = null
            tvSpec.text = null
            etStart.text = null
            etFinish.text = null
        }
    }


}