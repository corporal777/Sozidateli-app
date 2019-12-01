package com.example.ui.event.list.my

import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.data.models.MyEventsFilter
import com.example.holders.MyEventsHeaderItem
import com.example.holders.ScreenLabelItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.event.list.EventListFragment
import javax.inject.Inject
import javax.inject.Provider

class MyEventsFragment : EventListFragment<MyEventsPresenter>(), MyEventsContract.View, ToolbarFragment {

    override val title = ""

    @InjectPresenter
    override lateinit var presenter: MyEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<MyEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): MyEventsPresenter = presenterProvider.get().apply {
        filter = MyEventsFragmentArgs.fromBundle(arguments!!).filter
    }

    override fun setNoFilterHeader() {
        headGroup.update(listOf(
                ScreenLabelItem(getString(R.string.my_events_title)),
                MyEventsHeaderItem(object : MyEventsHeaderItem.OnFilterClickListener {
                    override fun onAcceptedClick() {
                        presenter.onAcceptedClick()
                    }

                    override fun onPendingClick() {
                        presenter.onPendingClick()
                    }

                    override fun onDeclinedClick() {
                        presenter.onDeclinedClick()
                    }
                })
        ))
    }

    override fun setAcceptedHeader() {
        headGroup.update(listOf(
                ScreenLabelItem(getString(R.string.my_events_accepted))
        ))
    }

    override fun setPendingHeader() {
        headGroup.update(listOf(
                ScreenLabelItem(getString(R.string.my_events_pending))
        ))
    }

    override fun setDeclinedHeader() {
        headGroup.update(listOf(
                ScreenLabelItem(getString(R.string.my_events_declined))
        ))
    }

    override fun showAccepted() {
        findNavController().navigate(MyEventsFragmentDirections.myEventsFragmentToSelf(MyEventsFilter.ACCEPTED))
    }

    override fun showPending() {
        findNavController().navigate(MyEventsFragmentDirections.myEventsFragmentToSelf(MyEventsFilter.PENDING))
    }

    override fun showDeclined() {
        findNavController().navigate(MyEventsFragmentDirections.myEventsFragmentToSelf(MyEventsFilter.DECLINED))
    }

    override fun selectEvent(event: Event) {
        findNavController().apply {
            //            graph.startDestination = R.id.event_tabs_fragment
            val opts = NavOptions.Builder()
                    .setPopUpTo(R.id.event_list_fragment, true)
                    .build()
            navigate(R.id.event_tabs_fragment, null, opts)
        }
    }
}
