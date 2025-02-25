package com.example.ui.event.list

import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.adapters.event.EventPagingAdapter
import com.example.app.R
import com.example.data.models.EventNew
import com.example.ui.base.BaseVBFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs

abstract class EventListFragment<P : EventListContract.Presenter, T : ViewBinding> :
    BaseVBFragment<T>(), EventListContract.View {

    abstract var presenter: P

    protected val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        EventPagingAdapter(
            { event, accept, pos -> presenter.onActionRegister(event, accept, pos) },
            { event, pos -> presenter.onActionCancel(event, pos) },
            { event -> presenter.onShowEventClick(event.id.toString()) },
            { event -> presenter.onShowAuthorization(event.id.toString()) })
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

    override fun showEventLoading(pos: Int) {
        pagingAdapter.executeButtonLoading(true, pos)
    }

    override fun hideEventLoading(pos: Int) {
        pagingAdapter.executeButtonLoading(false, pos)
    }
}