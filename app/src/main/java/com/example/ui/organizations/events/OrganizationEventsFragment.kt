package com.example.ui.organizations.events

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.example.adapters.event.EventPagingAdapter
import com.example.adapters.event.EventPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.event.EventPlaceholderAdapter
import com.example.app.R
import com.example.app.databinding.LayoutListBinding
import com.example.app.databinding.LayoutListSearchBinding
import com.example.data.models.EventNew
import com.example.extensions.isVisibleAnim
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.views.dialogs.StateType
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class OrganizationEventsFragment : BaseToolbarFragment<LayoutListSearchBinding>(),
    OrganizationEventsContract.View {

    @InjectPresenter
    lateinit var presenter: OrganizationEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<OrganizationEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): OrganizationEventsPresenter = presenterProvider.get().apply {
        organizationId =
            OrganizationEventsFragmentArgs.fromBundle(requireArguments()).organizationId
    }


    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        EventPagingAdapter(
            { event, accept, pos -> presenter.onActionRegister(event, accept, pos) },
            { event, pos -> presenter.onActionCancel(event, pos) },
            { presenter.onShowEventClick(it.id.toString()) },
            { presenter.onShowAuthorization(it.id.toString()) })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            searchList.adapter = pagingAdapter.withLoadStateAdapters(
                EventPlaceholderAdapter(1),
                EventPlaceholderAdapter(1)
            ) { setEmptyDataPlaceholder(it) }

            setEmptyDataPlaceholder(isEmptyData)

            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setData(data: PagingData<EventNew>, isTemporary: Boolean) {
        pagingAdapter.submitData(lifecycle, data, isTemporary, false)
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun updateEvent(event: EventNew) {
        pagingAdapter.updateEventAction(event)
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

    override fun setEmptyDataPlaceholder(show: Boolean) {
        super.setEmptyDataPlaceholder(show)
        mBinding.tvEmptyData.isVisibleAnim = show
    }

    override fun showEventLoading(pos: Int) = pagingAdapter.executeButtonLoading(true, pos)
    override fun hideEventLoading(pos: Int) = pagingAdapter.executeButtonLoading(false, pos)


    override fun binding() = LayoutListSearchBinding::class.java
    override fun layout(): Int = R.layout.layout_list_search
    override val title: CharSequence by lazy { getString(R.string.organization_events) }
    override fun scrollingView(): View = mBinding.searchList
}
