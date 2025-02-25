package com.example.ui.event.my

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapters.event.EventPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.event.EventPlaceholderAdapter
import com.example.app.R
import com.example.data.models.EventNew
import com.example.data.models.MyEventsFilter
import com.example.app.databinding.FragmentMyEventsBinding
import com.example.data.models.SearchFilter
import com.example.extensions.isVisibleAnim
import com.example.ui.views.filters.event.my.MyEventsFiltersBottomSheetDialog
import com.example.util.SearchInput
import com.example.util.smoothScrollToFirstItem
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.offsetChangedListener
import com.example.extensions.onCheckedChanged
import com.example.extensions.setFiltersBackground
import com.example.ui.event.list.EventListFragment
import com.example.ui.views.CustomSpannableString
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class MyEventsFragment : EventListFragment<MyEventsPresenter, FragmentMyEventsBinding>(),
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
                layoutManager = LinearLayoutManager(requireContext())
                adapter = pagingAdapter.withLoadStateAdapters(
                    EventPlaceholderAdapter(1),
                    EventPlaceholderAdapter(1)
                ) { setEmptyDataPlaceholder(it) }
                setEmptyDataPlaceholder(isEmptyData)
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

            btnDeclined.onCheckedChanged { isChecked ->
                presenter.onEventStateClick(isChecked, MyEventsFilter.DECLINED)
            }
            btnApproved.onCheckedChanged { isChecked ->
                presenter.onEventStateClick(isChecked, MyEventsFilter.APPROVED)
            }
            btnPending.onCheckedChanged { isChecked ->
                presenter.onEventStateClick(isChecked, MyEventsFilter.PENDING)
            }

            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
            appBarLayout.offsetChangedListener { a, o ->
                updateAppBarViews(abs(o / a.totalScrollRange).toFloat())
            }
        }

    }

    override fun setData(data: PagingData<EventNew>) {
        pagingAdapter.submitData(lifecycle, data, presenter.isTemporaryUser(), false)
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showFilters(filter: SearchFilter.EventNew) {
        MyEventsFiltersBottomSheetDialog(requireContext(), filter)
            .setFiltersSelected { presenter.onApplyFiltersClick(it) }
            .show()
    }

    override fun setFiltersChosen(isChosen: Boolean) {
        mBinding.btnFilter.setFiltersBackground(isChosen)
    }

    override fun setEmptyDataPlaceholder(show: Boolean) {
        super.setEmptyDataPlaceholder(show)
        val title: String
        val description: String
        if (presenter.isHasSearchParam()) {
            title = getString(R.string.no_data_found)
            description = getString(R.string.no_event_with_params_title)
        } else {
            title = getString(R.string.no_event_schedule_you_have)
            description = getString(R.string.choose_event_and_do_request)
        }
        mBinding.tvEmptyData.apply {
            isVisibleAnim = show
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
        mBinding.toolbar.isVisible = canShow
        mBinding.btnGoToMyTimeTable.setOnClickListener {
            findNavController().navigate(R.id.my_schedule_events_fragment)
        }
    }

    override fun scrollToFirstItem() {
        val mLayoutManager = mBinding.eventsList.layoutManager as LinearLayoutManager
        mLayoutManager.smoothScrollToFirstItem(requireContext(), mBinding.appBarLayout, 1)
    }

    override fun onExpandedState(withAnim: Boolean) {
        mBinding.tvLabelLarge.apply {
            if (withAnim) alpha = 0F
            visibility = View.VISIBLE
            if (withAnim) animate().setDuration(500).alpha(1.0f)
        }
    }

    override fun onCollapsedState(withAnim: Boolean) {
        mBinding.tvLabelLarge.apply {
            if (withAnim) alpha = 1F
            if (withAnim) animate().setDuration(500).alpha(0.0f)
            visibility = View.GONE
        }
    }

    override fun binding() = FragmentMyEventsBinding::class.java
    override fun layout(): Int = R.layout.fragment_my_events
}