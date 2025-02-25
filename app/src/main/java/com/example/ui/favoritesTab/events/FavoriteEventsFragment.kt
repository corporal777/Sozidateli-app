package com.example.ui.favoritesTab.events

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapters.event.FavoriteEventPagingAdapter
import com.example.adapters.event.FavoriteEventPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.organization.OrganizationPlaceholderAdapter
import com.example.app.R
import com.example.app.databinding.LayoutDataListBinding
import com.example.data.models.EventNew
import com.example.extensions.isVisibleAnim
import com.example.ui.base.BaseVBFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class FavoriteEventsFragment : BaseVBFragment<LayoutDataListBinding>(),
    FavoriteEventsContract.View {

    @InjectPresenter
    lateinit var presenter: FavoriteEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteEventsPresenter = presenterProvider.get()


    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        FavoriteEventPagingAdapter(
            { presenter.onShowEventClick(it.id.toString()) },
            { presenter.onEventActionClick(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            dataListView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = pagingAdapter.withLoadStateAdapters(
                    OrganizationPlaceholderAdapter(5),
                    OrganizationPlaceholderAdapter(1)
                ) { setEmptyDataPlaceholder(it) }
                setEmptyDataPlaceholder(isEmptyData)
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setData(events: PagingData<EventNew>) {
        pagingAdapter.submitData(lifecycle, events)
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun updateEvent(event: EventNew) {
        pagingAdapter.updateEventAction(event)
    }

    override fun showAboutEvent(event: String) {
        val args = AboutEventFragmentArgs.Builder(event).build().toBundle()
        findNavController().navigate(R.id.about_event_fragment, args)
    }


    override fun setEmptyDataPlaceholder(show: Boolean) {
        super.setEmptyDataPlaceholder(show)
        mBinding.tvEmptyDataTitle.apply {
            isVisibleAnim = show
            text = getString(R.string.blank_list_error)
        }
        mBinding.tvEmptyDataDescription.apply {
            isVisibleAnim = show
            text = getString(R.string.events_favorites_empty_list_description)
        }
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun binding() = LayoutDataListBinding::class.java
    override fun layout(): Int = R.layout.layout_data_list
}
