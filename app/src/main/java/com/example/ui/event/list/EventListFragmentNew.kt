package com.example.ui.event.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.ViewBindingProperty
import by.kirich1409.viewbindingdelegate.internal.emptyVbCallback
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapters.EventPagingAdapter
import com.example.app.R
import com.example.data.models.EventNew
import com.example.holders.redesign.EventListItem
import com.example.ui.agreement.UserAgreementBottomSheetDialog
import com.example.ui.base.BaseBindingFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.views.dialogs.EventAgreementBottomSheet
import com.example.ui.views.dialogs.StateType
import kotlin.reflect.KClass

abstract class EventListFragmentNew<P : EventListContractNew.Presenter>(res : Int) :
    BaseBindingFragment(res), EventListContractNew.View {

    abstract var presenter: P

    val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        EventPagingAdapter(
            { presenter.onActionRegister(it, false) },
            { presenter.onActionCancel(it) },
            { presenter.onShowEventClick(it.id.toString()) },
            { presenter.onShowAuthorization(it.id.toString()) },
            { showStateErrorMessage(StateType.BASE, false, null) })
    }

    override fun updateEvent(event: EventNew) {
        pagingAdapter.updateEventAction(event)
    }

    override fun invalidatePagingData() {
        pagingAdapter.refreshData()
    }

    override fun showAgreementRegisterDialog(event: EventNew) {
        UserAgreementBottomSheetDialog(requireContext(), event)
            .setAcceptedCallback { isAccept, eventNew ->
                if (isAccept) presenter.onActionRegister(eventNew, true)
                else updateEvent(eventNew)
            }.show()
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