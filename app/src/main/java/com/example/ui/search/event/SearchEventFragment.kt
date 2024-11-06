package com.example.ui.search.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.data.models.EventNew
import com.example.data.models.SearchFilter
import com.example.app.databinding.LayoutFilterEventSearchBinding
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventListItem
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.search.SearchFragment
import com.example.ui.views.dialogs.EventAgreementBottomSheet
import com.example.ui.views.dialogs.StateType
import com.example.ui.views.filters.event.EventFiltersBottomSheetDialog
import com.example.ui.views.filters.user.UserFiltersBottomSheetDialog
import com.example.ui.views.suggestFieldView.format.EventFormatBottomSheet
import com.example.ui.views.suggestFieldView.organization.EventOrgBottomSheet
import com.example.util.initInput
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.Group
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SearchEventFragment : SearchFragment<SearchEventPresenter, SearchFilter.EventNew>(),
    SearchEventContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchEventPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchEventPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchEventPresenter = presenterProvider.get()




    private val onEventClickListener = object : EventListItem.OnEventClickListener {
        override fun onActionRegister(event: String, agreementUrl: String?, formEnabled: Boolean) =
            presenter.onActionRegister(event, agreementUrl, formEnabled)
        override fun onActionCancel(event: String, registrationId: String?) =
            presenter.onActionCancel(event, registrationId)
        override fun onShowEventClick(view: View, event: String) =
            presenter.onShowEventClick(event)
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
        override fun onShowNeedAuth(eventId: String) { presenter.onShowAuthorization(eventId) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun showFilter(filter: SearchFilter.EventNew) {
        EventFiltersBottomSheetDialog(requireContext(), filter)
            .setFiltersSelected { presenter.onFiltersApplyClick(it) }
            .show()
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

    override fun showAuthorization() {
        findNavController().navigate(R.id.authorization_fragment)
    }

    override fun showAgreementRegisterDialog(event: String, url: String, formEnabled: Boolean) {
        EventAgreementBottomSheet(requireContext(), url)
            .setSelectCallback { presenter.onAcceptRegistrationAgreement(event, formEnabled) }
            .show()
    }
}