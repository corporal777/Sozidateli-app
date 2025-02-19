package com.example.ui.search.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapters.EventPagingAdapter
import com.example.adapters.EventPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.EventPlaceholderAdapter
import com.example.adapters.UserPagingAdapter
import com.example.adapters.UserPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.UserPlaceholderAdapter
import com.example.app.R
import com.example.data.models.EventNew
import com.example.data.models.SearchFilter
import com.example.app.databinding.LayoutFilterEventSearchBinding
import com.example.app.databinding.LayoutListEventSearchBinding
import com.example.app.databinding.LayoutListSearchBinding
import com.example.extensions.isVisibleAnim
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventListItem
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.registration.EventRegistrationFragmentArgs
import com.example.ui.search.SearchFragment
import com.example.ui.views.dialogs.EventAgreementBottomSheet
import com.example.ui.views.dialogs.StateType
import com.example.ui.views.filters.event.EventFiltersBottomSheetDialog
import com.example.ui.views.filters.user.UserFiltersBottomSheetDialog
import com.example.ui.views.suggestFieldView.format.EventFormatBottomSheet
import com.example.ui.views.suggestFieldView.organization.EventOrgBottomSheet
import com.example.util.initInput
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.Group
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
            { event, accept -> presenter.onActionRegister(event, accept) },
            { presenter.onActionCancel(it) },
            { presenter.onShowEventClick(it.id.toString()) },
            { presenter.onShowAuthorization(it.id.toString()) },
            { showStateErrorMessage(StateType.BASE, false, null) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            searchEventList.adapter = pagingAdapter.withLoadStateAdapters(
                EventPlaceholderAdapter(1),
                EventPlaceholderAdapter(1)
            ) { setDataEmpty(it) }
            setDataEmpty(isEmptyData)

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

    override fun setDataEmpty(show: Boolean) {
        super.setDataEmpty(show)
        mBinding.tvEmptyData.isVisibleAnim = show
    }

    override fun binding() = LayoutListEventSearchBinding::class.java
    override fun layout() = R.layout.layout_list_event_search
}