package com.example.ui.event.list

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.holders.redesign.EventItemNew
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.views.dialogs.EventAgreementBottomDialog
import com.example.ui.views.dialogs.EventRegistrationSuccessBottomDialog
import com.example.ui.views.dialogs.StateType

abstract class EventListFragment<P : EventListContract.Presenter, T : ViewDataBinding> :
    BaseFragment<T>(), EventListContract.View {

    abstract var presenter: P

    protected val onEventClickListener = object : EventItemNew.OnEventClickListener {
        override fun onActionRegister(event: String, agreementUrl: String?, formEnabled: Boolean) =
            presenter.onActionRegister(event, agreementUrl, formEnabled)

        override fun onActionCancel(event: String, registrationId: String?) =
            presenter.onActionCancel(event, registrationId)

        override fun onShowEventClick(view: View, event: String) = presenter.onShowEventClick(event)
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
    }

    override fun showAgreementRegisterDialog(event: String, url: String, formEnabled: Boolean) {
        EventAgreementBottomDialog(requireContext(), url)
            .setSelectCallback { presenter.onAcceptRegistrationAgreement(event, formEnabled) }
            .show()
    }

    override fun showEventRegistrationSuccessDialog() {
        EventRegistrationSuccessBottomDialog(requireContext())
            .setSelectCallback {  }
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
}
