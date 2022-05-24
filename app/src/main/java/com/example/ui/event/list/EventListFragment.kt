package com.example.ui.event.list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.data.models.*
import com.example.extensions.getAffiliationString
import com.example.holders.EventDataListItem
import com.example.holders.EventStatusItem
import com.example.holders.NoDataItem
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventGroupNew
import com.example.holders.redesign.EventItemNew
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.old.AboutEventFragment.Companion.ABOUT_FROM_OTHER
import com.example.ui.event.about.old.AboutEventFragmentArgs
import com.example.ui.event.about.redesign.AboutEventFragmentNewArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.search.tabs.SearchTabsFragmentArgs
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.EventRegistrationProfileFieldsDialog
import com.example.ui.views.StateType
import com.example.util.PositionOffsetScrollListener
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list.*

abstract class EventListFragment<P : EventListContract.Presenter> : BaseFragment(),
    EventListContract.View {

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

    private val onEventClickListener = object : EventItemNew.OnEventClickListener {
        override fun onActionRegister(event: String) = presenter.onActionRegister(event)
        //override fun onActionShowEvent(event: String) = presenter.onActionShowEvent(event)
        override fun onActionCancel(event: String, registrationId: String?) =
            presenter.onActionCancel(event, registrationId)

       // override fun onActionWriteToOrganization(emails: List<EventPhoneModel>) =
           // presenter.onActionWriteToOrganization(emails)

        override fun onShowEventClick(view: View, event: String) {
            eventToShowView = view
            presenter.onShowEventClick(event)
        }

       // override fun onShowFilterClick(format: Int) = presenter.onShowFilterClick(format)
        override fun onShowUpdateState() = showStateErrorMessage(StateType.BASE, false, null)
    }

    private var eventToShowView: View? = null

    private var eventDataListItem: EventDataListItem? = null

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

    override fun setData(events: List<EventNew?>) {
        Log.e("EventsList", "start")
        Log.e("EventsList", "size: " + events.size)
        dataGroup.update(events.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
            else EventGroupNew(
                it,
                onEventClickListener,
            )
//            else EventGroup(
//                    it.id.toString(),
//                    it.status?.value,
//                    it.binds?.currentUserRegistration?.status?.value,
//                    it.binds?.organization?.backgroundColor?.value,
//                    it.image?.uri,
//                    EventFormat(name = if (it.format?.name.isNullOrEmpty()) it.format?.custom?: "" else it.format?.name?: ""),
//                    it.binds?.organization?.email,
//                    (it.status?.value?: "") != Event.Status.REGISTRATION,
//                    onEventClickListener,
//                    createEventDataListItem(event = it),
//                    it.userAgreement?.uri,
//                    it.binds?.eventRegistrationState,
//                    true,
//                    it.binds?.currentUserRegistration?.id.toString()
//            )
        })
        Log.e("EventsList", "finish")
        swipeToRefresh.isRefreshing = false
    }

    protected open fun createEventDataListItem(event: EventNew): EventDataListItem {
        return EventDataListItem(
            -event.id?.toLong()!!,
            event.name,
            event.address?.getShortAddress(),
            event.holdingDate?.from,
            event.binds?.getFirstActionStartDate()
        )
    }

    override fun showEmptyListPlaceholder() {
        dataGroup.update(listOf(NoDataItem(getString(R.string.empty_list_placeholder_message))))
        swipeToRefresh.isRefreshing = false
    }

    override fun showWriteToOrganizationEmails(emails: List<EventPhoneModel>) {
        val emailsList = emails.map {
            it.getAffiliationString(underlinedEmail = true)
        }
            .filter { it.isNotEmpty() }
            .toTypedArray()

        AlertDialog.Builder(requireContext())
            .setItems(emailsList) { dialog, which ->
                val email = emails[which]
                presenter.onWriteToOrganizationEmailChosen(email)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun showWriteToOrganization(email: EventPhoneModel) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email.value))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
            position,
            offset
        )
    }

    override fun showAboutEvent(event: String) {
//        findNavController().navigate(
//            R.id.about_event_fragment,
//            AboutEventFragmentArgs.Builder(event, ABOUT_FROM_OTHER).build().toBundle(),
//            null,
//            eventToShowView?.let { FragmentNavigatorExtras(it to it.transitionName) })

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

    override fun selectEvent() {
        findNavController().navigate(
            R.id.event_tabs_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build()
        )
    }

    override fun showSearch(format: Int) {
        val filter = SearchFilter.Event().apply { this.format = format }
        findNavController().navigate(
            R.id.search_tabs_fragment,
            SearchTabsFragmentArgs.Builder(filter).build().toBundle()
        )
    }

    override fun showRegistrationFieldsRequest(fields: List<String>) {
        EventRegistrationProfileFieldsDialog(requireContext(), fields) {
            presenter.onShowEditProfileClick()
        }
            .show()
    }

    override fun showEditProfile(id: String) {
        findNavController().navigate(
            R.id.user_profile_fragment,
            UserFragmentArgs.Builder(id).build().toBundle()
        )
    }

    override fun layout() = R.layout.layout_list
}
