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
import com.example.ui.base.BaseBindingFragment
import com.example.ui.base.BaseFragment
import com.example.ui.base.BaseVBFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.views.dialogs.EventAgreementBottomSheet
import com.example.ui.views.dialogs.StateType
import kotlin.reflect.KClass

abstract class EventListFragmentNew<P : EventListContractNew.Presenter, T : ViewBinding> :
    BaseVBFragment<T>(), EventListContractNew.View {

    abstract var presenter: P

    protected val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        EventPagingAdapter(
            { event, accept -> presenter.onActionRegister(event, accept) },
            { presenter.onActionCancel(it) },
            { presenter.onShowEventClick(it.id.toString()) },
            { presenter.onShowAuthorization(it.id.toString()) },
            { showStateErrorMessage(StateType.BASE, false, null) })
    }

    override fun updateEvent(event: EventNew) {
        pagingAdapter.updateEventAction(event)
    }

    override fun showAboutEvent(event: String) {
        val args = AboutEventFragmentArgs.Builder(event).build().toBundle()
        findNavController().navigate(R.id.about_event_fragment, args)
    }

    override fun showEventRequest(event: String) {
        val args = EventRegistrationFragmentArgs.Builder(event).build().toBundle()
        findNavController().navigate(R.id.request_fragment, args)
    }

    override fun showAuthorization() {
        findNavController().navigate(R.id.authorization_fragment)
    }
}