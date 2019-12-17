package com.example.ui.event.list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.data.models.EmailAffiliation
import com.example.data.models.Event
import com.example.data.models.SearchFilter
import com.example.holders.*
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.search.tabs.SearchTabsFragmentArgs
import com.example.util.PositionOffsetScrollListener
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list.*

abstract class EventListFragment<P : EventListContract.Presenter> : BaseFragment(), EventListContract.View {

    abstract var presenter: P

    val headGroup = Section()
    val dataGroup = Section()

    val adapter = PaginationListGroupAdapter<GroupieViewHolder>().apply {
        add(headGroup)
        add(dataGroup)
        setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
            override fun onItemTake(position: Int) {
                val headCount = headGroup.itemCount
                if (position < headCount) return
                presenter.onItemTake(position - headCount)
            }
        })
    }

    private val onEventClickListener = object : EventStatusItem.OnEventClickListener {
        override fun onActionRegister(event: String) = presenter.onActionRegister(event)
        override fun onActionShowEvent(event: String) = presenter.onActionShowEvent(event)
        override fun onActionCancel(event: String) = presenter.onActionCancel(event)
        override fun onActionWriteToOrganization(emails: List<EmailAffiliation>) = presenter.onActionWriteToOrganization(emails)
        override fun onShowEventClick(event: String) = presenter.onShowEventClick(event)
        override fun onShowFilterClick(format: Int) = presenter.onShowFilterClick(format)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@EventListFragment.adapter
            addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                presenter.onScrollChange(position, offset)
            })
        }

        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    override fun setData(events: List<Event?>) {
        dataGroup.update(events.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
            else EventGroup(
                    it.id,
                    it.status,
                    it.userRegistration,
                    it.backgroundColor,
                    it.backgroundImage,
                    it.format,
                    it.email,
                    onEventClickListener,
                    EventDataListItem(-it.id.toLong(), it.name, it.addressCity, it.conferenceStart, it.conferenceFirstActivityStart)
            )
        })
        swipeToRefresh.isRefreshing = false
    }

    override fun showEmptyListPlaceholder() {
        dataGroup.update(listOf(NoDataItem(getString(R.string.empty_list_placeholder_message))))
        swipeToRefresh.isRefreshing = false
    }

    override fun showWriteToOrganizationEmails(emails: List<EmailAffiliation>) {
        AlertDialog.Builder(requireContext())
                .setItems(emails.map { it.getAffiliationString() }.toTypedArray()) { dialog, which ->
                    val email = emails[which]
                    presenter.onWriteToOrganizationEmailChosen(email)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun showWriteToOrganization(email: EmailAffiliation) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email.email))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, offset)
    }

    override fun showAboutEvent(event: String) {
        findNavController().navigate(R.id.about_event_fragment, AboutEventFragmentArgs.Builder(event).build().toBundle())
    }

    override fun showEventRequest(event: String) {
        findNavController().navigate(R.id.request_fragment, EventRegistrationFragmentArgs.Builder(event).build().toBundle())
    }

    override fun selectEvent() {
        findNavController().navigate(R.id.event_tabs_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build())
    }

    override fun showSearch(format: Int) {
        val filter = SearchFilter.Event().apply { this.format = format }
        findNavController().navigate(R.id.search_tabs_fragment, SearchTabsFragmentArgs.Builder(filter).build().toBundle())
    }

    override fun layout() = R.layout.layout_list
}
