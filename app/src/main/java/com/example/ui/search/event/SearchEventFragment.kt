package com.example.ui.search.event

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.example.adapters.event.EventPagingAdapter
import com.example.adapters.event.EventPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.event.EventPlaceholderAdapter
import com.example.app.R
import com.example.data.models.EventNew
import com.example.data.models.SearchFilter
import com.example.app.databinding.LayoutListEventSearchBinding
import com.example.extensions.isVisibleAnim
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.search.SearchFragment
import com.example.ui.views.dialogs.StateType
import com.example.ui.views.filters.event.EventFiltersBottomSheetDialog
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SearchEventFragment : SearchFragment<LayoutListEventSearchBinding, SearchEventPresenter>(),
    SearchEventContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchEventPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchEventPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchEventPresenter = presenterProvider.get()



    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        EventPagingAdapter(
            { event, accept, pos -> presenter.onActionRegister(event, accept, pos) },
            { event, pos -> presenter.onActionCancel(event, pos) },
            { event -> presenter.onShowEventClick(event.id.toString()) },
            { event -> presenter.onShowAuthorization(event.id.toString()) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            searchEventList.adapter = pagingAdapter.withLoadStateAdapters(
                EventPlaceholderAdapter(1),
                EventPlaceholderAdapter(1)
            ) { setEmptyDataPlaceholder(it) }
            setEmptyDataPlaceholder(isEmptyData)

            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
            clQrScanner.setOnClickListener { presenter.onScanClick() }
        }
    }

    override fun setData(data: PagingData<EventNew>, isTemporary: Boolean) {
        pagingAdapter.submitData(lifecycle, data, isTemporary, false)
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun updateEvent(event: EventNew) {
        pagingAdapter.updateEventAction(event)
    }

    override fun showFilter(filter: SearchFilter.EventNew) {
        EventFiltersBottomSheetDialog(requireContext(), filter)
            .setFiltersSelected { presenter.onFiltersApplyClick(it) }
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

    override fun showQrScanner() {
        findNavController().navigate(R.id.qr_scanner_fragment)
    }

    override fun setEmptyDataPlaceholder(show: Boolean) {
        super.setEmptyDataPlaceholder(show)
        mBinding.tvEmptyData.isVisibleAnim = show
    }

    override fun showEventLoading(pos: Int) = pagingAdapter.executeButtonLoading(true, pos)
    override fun hideEventLoading(pos: Int) = pagingAdapter.executeButtonLoading(false, pos)


    override fun binding() = LayoutListEventSearchBinding::class.java
    override fun layout() = R.layout.layout_list_event_search
}