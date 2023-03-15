package com.example.ui.search.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
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
import com.example.ui.views.suggestFieldView.format.EventFormatBottomSheet
import com.example.ui.views.suggestFieldView.organization.EventOrgBottomSheet
import com.example.util.initInput
import com.google.android.material.textfield.TextInputLayout
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
            AboutEventFragmentNewArgs.Builder(event).build().toBundle()
        )
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
        return LayoutFilterEventBinding.inflate(LayoutInflater.from(requireContext()), null, false)
            .apply {
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
                initFormats(filter, tilFormat, tvFormat)
                initOrganizations(filter, tilOrganization, tvOrganization)
                initEventInterests(filter, this)

                initTextFilter(etName, filter.name) { filter.name = it }
                initDateFilter(etStart, tilStart, filter.dateStart) { filter.dateStart = it }
                initDateFilter(etFinish, tilFinish, filter.dateFinish) { filter.dateFinish = it }
            }.root
    }

    private fun initFormats(
        filter: SearchFilter.EventNew,
        inputLayout: TextInputLayout,
        textView: AutoCompleteTextView,
    ) {
//        textView.apply {
//            inputLayout.endIconMode = TextInputLayout.END_ICON_NONE
//            isCursorVisible = false
//            isFocusable = false
//            isFocusableInTouchMode = false
//
//            setOnClickListener {
//                EventFormatBottomSheet(requireContext(), filter.formats)
//                    .setFormatSelectedCallback {
//                        filter.format = it?.id
//                        filter.customFormat = it?.name
//                        this.setText(filter.getFormatName())
//                    }
//                    .show()
//            }
//            initInput(filter.getFormatName()) {
//                if (it.isNullOrBlank()) {
//                    filter.format = null
//                    filter.customFormat = null
//                }
//            }
//        }

        if (filter.formats.isNullOrEmpty()) {
            inputLayout.isVisible = false
        } else {
            inputLayout.isVisible = true
            initDropDownView(
                textView,
                filter.formats!!,
                filter.formats!!.find { it.id == filter.format }?.name,
                null,
                { it.name ?: "" },
                { it?.id },
                { filter.format = it }
            )
        }
    }

    private fun initOrganizations(
        filter: SearchFilter.EventNew,
        inputLayout: TextInputLayout,
        textView: AutoCompleteTextView,
    ) {
//        textView.apply {
//            inputLayout.endIconMode = TextInputLayout.END_ICON_NONE
//            isCursorVisible = false
//            isFocusable = false
//            isFocusableInTouchMode = false
//
//            setOnClickListener {
//                EventOrgBottomSheet(requireContext(), filter.organizations)
//                    .setOrganizationSelectedCallback {
//                        filter.organizationId = it?.id
//                        filter.organizationName = it?.legalInformation?.name?.short
//                        this.setText(filter.getOrgName())
//                    }
//                    .show()
//            }
//            initInput(filter.getOrgName()) {
//                if (it.isNullOrBlank()) {
//                    filter.organizationId = null
//                    filter.organizationName = null
//                }
//            }
//        }

        if (filter.organizations.isNullOrEmpty()) {
            textView.isEnabled = false
            textView.text = null
            inputLayout.isVisible = false
        } else {
            val selected = filter.organizations!!.find { x -> x.id == filter.organizationId }
            initDropDownView(
                textView,
                filter.organizations!!,
                selected?.legalInformation?.name?.short,
                null,
                transformKey = { it?.legalInformation?.name?.short ?: "" },
                findValue = { it?.id },
                onVariantChange = { filter.organizationId })
            textView.isEnabled = true
            inputLayout.isVisible = true
        }
    }

    private fun initEventInterests(filter: SearchFilter.EventNew, binding: LayoutFilterEventBinding){
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