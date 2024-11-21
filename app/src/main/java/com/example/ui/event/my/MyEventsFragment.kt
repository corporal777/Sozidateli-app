package com.example.ui.event.my

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapters.EventPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.EventPlaceholderAdapter
import com.example.extensions.dp
import com.example.extensions.updateItem
import com.example.app.R
import com.example.data.models.EventNew
import com.example.data.models.MyEventsFilter
import com.example.app.databinding.FragmentMyEventsBinding
import com.example.ui.event.my.schedule.items.NoScheduleEventItem
import com.example.ui.views.filters.event.my.MyEventsFiltersBottomSheetDialog
import com.example.util.SearchInput
import com.example.util.smoothScrollToFirstItem
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.offsetChangedListener
import com.example.extensions.setFiltersBackground
import com.example.ui.event.list.EventListFragmentNew
import com.example.ui.views.CustomSpannableString
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class MyEventsFragment : EventListFragmentNew<MyEventsPresenter, FragmentMyEventsBinding>(),
    MyEventsContract.View {

    @InjectPresenter
    override lateinit var presenter: MyEventsPresenter

    @Inject
    lateinit var presenterProvider: Provider<MyEventsPresenter>

    @ProvidePresenter
    fun providePresenter(): MyEventsPresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventsList.apply {
                adapter = pagingAdapter.withLoadStateAdapters(
                    EventPlaceholderAdapter(1),
                    EventPlaceholderAdapter(1)
                ) { showEmptyListPlaceholder(it) }
                layoutManager = LinearLayoutManager(requireContext())
            }
            etSearch.apply {
                SearchInput(this).apply {
                    setOnFocusChange { hasFocus ->
                        clSearch.setBackgroundResource(
                            if (hasFocus) R.drawable.background_search_field_rounded_focused
                            else R.drawable.background_search_field_rounded_normal
                        )
                    }
                    setOnAfterTextChange {
                        btnClear.isVisible = !it.isNullOrEmpty()
                        presenter.onSearchTextChange(it)
                    }
                    setOnTextChangeDone {
                        presenter.onSearchTextChange(it)
                        hideKeyboard()
                    }
                }
            }
            btnClear.apply {
                isVisible = !etSearch.text.isNullOrEmpty()
                setOnClickListener { etSearch.text = null }
            }
            btnFilter.setOnClickListener { presenter.onShowFiltersClick() }
            btnDeclined.setOnCheckedChangeListener { _, isChecked ->
                presenter.onEventStateClick(isChecked, MyEventsFilter.DECLINED)
            }
            btnApproved.setOnCheckedChangeListener { _, isChecked ->
                presenter.onEventStateClick(isChecked, MyEventsFilter.APPROVED)
            }
            btnPending.setOnCheckedChangeListener { _, isChecked ->
                presenter.onEventStateClick(isChecked, MyEventsFilter.PENDING)
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
            appBarLayout.offsetChangedListener { appBarLayout, offset ->
                updateAppBarViews(abs(offset / appBarLayout.totalScrollRange.toFloat()))
            }
        }

    }

    override fun setData(data: PagingData<EventNew>) {
        pagingAdapter.submitData(lifecycle, data, presenter.isTemporaryUser(), false)
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showFilters() {
        MyEventsFiltersBottomSheetDialog(requireContext(), presenter.searchFilter)
            .setFiltersSelected { presenter.onApplyFiltersClick(it) }
            .show()
    }

    override fun setFiltersChosen(isChosen: Boolean) {
        mBinding.btnFilter.setFiltersBackground(isChosen)
    }

    override fun showEmptyListPlaceholder(show: Boolean) {
        val title : String
        val description : String
        if (presenter.isHasSearchParam()) {
            title = getString(R.string.no_data_found)
            description = getString(R.string.no_event_with_params_title)
        } else {
            title = getString(R.string.no_event_schedule_you_have)
            description = getString(R.string.choose_event_and_do_request)
        }
        mBinding.tvEmptyData.apply {
            isVisible = show
            text = SpannableStringBuilder().apply {
                append(CustomSpannableString(title).apply {
                    setTextSizeSpan(R.dimen.no_data_found_title_text_size, requireContext())
                    setColorSpan(R.color.black, requireContext())
                    setFontSpan("fonts/sf_pro_text_bold.ttf", requireContext())
                })
                append("\n")
                append(CustomSpannableString(description).apply {
                    setTextSizeSpan(R.dimen.no_data_found_desc_text_size, requireContext())
                    setColorSpan(R.color.no_data_item_description_color, requireContext())
                    setFontSpan("fonts/sf_pro_text_semibold.ttf", requireContext())
                })
            }
        }
        mBinding.swipeToRefresh.isRefreshing = false
    }


    override fun setShowScheduleEvents(canShow: Boolean) {
        mBinding.toolbar.apply {
            isVisible = canShow
            mBinding.btnGoToMyTimeTable.setOnClickListener {
                findNavController().navigate(R.id.my_schedule_events_fragment)
            }
        }
    }

    override fun scrollToFirstItem() {
        val mLayoutManager = mBinding.eventsList.layoutManager as LinearLayoutManager
        mLayoutManager.smoothScrollToFirstItem(requireContext(), mBinding.appBarLayout, 1)
    }

    override fun onExpandedState() {
        mBinding.tvLabelLarge.apply {
            visibility = View.VISIBLE
            alpha = 0F
            animate().setDuration(500).alpha(1.0f)
        }
    }

    override fun onCollapsedState() {
        mBinding.tvLabelLarge.apply {
            alpha = 1F
            animate().setDuration(500).alpha(0.0f)
            visibility = View.GONE
        }
    }

    override fun layout(): Int = R.layout.fragment_my_events
}