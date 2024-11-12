package com.example.ui.event.list

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.app.R
import com.example.holders.redesign.EventListItem
import com.example.ui.base.BaseBindingFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.views.dialogs.EventAgreementBottomSheet
import com.example.ui.views.dialogs.StateType
import kotlin.reflect.KClass

abstract class EventListFragment<P : EventListContract.Presenter, T : ViewDataBinding> :
    BaseFragment<T>(), EventListContract.View {

    abstract var presenter: P


    protected val onEventClickListener = object : EventListItem.OnEventClickListener {
        override fun onActionRegister(event: String, agreementUrl: String?, formEnabled: Boolean) =
            presenter.onActionRegister(event, agreementUrl, formEnabled)

        override fun onActionCancel(event: String, registrationId: String?) =
            presenter.onActionCancel(event, registrationId)

        override fun onShowEventClick(view: View, event: String) = presenter.onShowEventClick(event)
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
        override fun onShowNeedAuth(eventId: String) { presenter.onShowAuthorization(eventId) }
    }

    override fun showAgreementRegisterDialog(event: String, url: String, formEnabled: Boolean) {
        EventAgreementBottomSheet(requireContext(), url)
            .setSelectCallback { presenter.onAcceptRegistrationAgreement(event, formEnabled) }
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
}
