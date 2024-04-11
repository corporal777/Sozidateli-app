package com.example.ui.search.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.data.models.EventNew
import com.example.data.models.SearchFilter
import com.example.databinding.LayoutFilterEventSearchBinding
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventItemNew
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.search.SearchFragment
import com.example.ui.views.StateType
import com.example.ui.views.dialogs.EventAgreementDialog
import com.example.ui.views.suggestFieldView.format.EventFormatBottomSheet
import com.example.ui.views.suggestFieldView.organization.EventOrgBottomSheet
import com.example.util.initInput
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.Group
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SearchEventFragment : SearchFragment<SearchEventPresenter, EventNew, SearchFilter.EventNew>(),
    SearchEventContract.View {

    @InjectPresenter
    override lateinit var searchPresenter: SearchEventPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchEventPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchEventPresenter = presenterProvider.get()


    private val onEventClickListener = object : EventItemNew.OnEventClickListener {
        override fun onActionRegister(event: String, agreementUrl: String?) =
            searchPresenter.onActionRegister(event, agreementUrl)
        override fun onActionCancel(event: String, registrationId: String?) =
            searchPresenter.onActionCancel(event, registrationId)
        override fun onShowEventClick(view: View, event: String) =
            searchPresenter.onShowEventClick(event)
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(
            R.id.about_event_fragment,
            AboutEventFragmentArgs.Builder(event).build().toBundle()
        )
    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(
            R.id.request_fragment,
            EventRegistrationFragmentArgs.Builder(event).build().toBundle()
        )
    }

    override fun showAgreementRegisterDialog(event: String, url: String) {
        EventAgreementDialog(requireContext(), url).setSelectCallback {
            searchPresenter.onAcceptRegistrationAgreement(event)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun createItem(itemData: EventNew?): Group {
        return if (itemData == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
        else {
            return EventItemNew(
                itemData,
                onEventClickListener,
            )
        }
    }


    override fun createFilterView(filter: SearchFilter.EventNew): View {
        return LayoutFilterEventSearchBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        )
            .apply {
//                etAddress.apply {
//                    setTextWithoutSearch(filter.address)
//                    com.example.extensions.onTextChanged {
//                        filter.address = it.toString()
//                        filter.fullAddress = null
//                    }
//                    onDataSelectedListener = {
//                        filter.fullAddress = it
//                    }
//                }
                initTextFilter(etName, filter.name) { filter.name = it }

                initRegions(filter, tvRegion, tilRegion, tilTown)
                initTowns(filter, tvTown, tilTown)

                initFormats(filter, tilFormat, tvFormat)
                initOrganizations(filter, tilOrganization, tvOrganization)
                initEventInterests(filter, this)


                initDateFilter(etStart, tilStart, filter.dateStart) { filter.dateStart = it }
                initDateFilter(etFinish, tilFinish, filter.dateFinish) { filter.dateFinish = it }
            }.root
    }


    private fun initFormats(
        filter: SearchFilter.EventNew,
        inputLayout: TextInputLayout,
        textView: AutoCompleteTextView,
    ) {
        textView.apply {
            inputLayout.endIconMode = TextInputLayout.END_ICON_NONE
            isCursorVisible = false
            isFocusable = false
            isFocusableInTouchMode = false

            setOnClickListener {
                EventFormatBottomSheet(requireContext(), filter.formats)
                    .setFormatSelectedCallback {
                        filter.format = it?.id
                        filter.customFormat = it?.name
                        this.setText(filter.getFormatName())
                    }
                    .show()
            }
            initInput(filter.getFormatName()) {
                if (it.isNullOrBlank()) {
                    filter.format = null
                    filter.customFormat = null
                }
            }
        }
    }

    private fun initOrganizations(
        filter: SearchFilter.EventNew,
        inputLayout: TextInputLayout,
        textView: AutoCompleteTextView,
    ) {
        textView.apply {
            inputLayout.endIconMode = TextInputLayout.END_ICON_NONE
            isCursorVisible = false
            isFocusable = false
            isFocusableInTouchMode = false

            setOnClickListener {
                EventOrgBottomSheet(requireContext(), filter.organizations)
                    .setOrganizationSelectedCallback {
                        filter.organizationId = it?.id
                        filter.organizationName = it?.legalInformation?.name?.short
                        this.setText(filter.getOrgName())
                    }
                    .show()
            }
            initInput(filter.getOrgName()) {
                if (it.isNullOrBlank()) {
                    filter.organizationId = null
                    filter.organizationName = null
                }
            }
        }
    }

    private fun initEventInterests(
        filter: SearchFilter.EventNew,
        binding: LayoutFilterEventSearchBinding
    ) {
        binding.apply {
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
        }
    }


    override fun clearFilterView(filterView: View) {
        LayoutFilterEventSearchBinding.bind(filterView).apply {
            etName.text = null

            tvRegion.text = null
            tvTown.text = null

            tvFormat.text = null
            tvOrganization.text = null
            tvTheme.text = null
            tvSpec.text = null
            etStart.text = null
            etFinish.text = null
        }
    }


}