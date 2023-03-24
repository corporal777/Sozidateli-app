package com.example.ui.event.list

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.data.models.EventNew
import com.example.holders.EventDataListItem
import com.example.holders.NoDataItem
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventGroupNew
import com.example.holders.redesign.EventItemNew
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragmentNewArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.EventRegistrationProfileFieldsDialog
import com.example.ui.views.StateType
import com.example.util.PositionOffsetScrollListener
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list.*
import onScrolled

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
        override fun onActionCancel(event: String, registrationId: String?) =
            presenter.onActionCancel(event, registrationId)

        override fun onShowEventClick(view: View, event: String) {
            eventToShowView = view
            presenter.onShowEventClick(event)
        }
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

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
            position,
            offset
        )
    }

    override fun showAboutEvent(event: String) {
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
