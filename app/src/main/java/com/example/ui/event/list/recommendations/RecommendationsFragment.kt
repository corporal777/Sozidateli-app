package com.example.ui.event.list.recommendations

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapters.EventPagingAdapter
import com.example.adapters.EventPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.EventPlaceholderAdapter
import com.example.app.R
import com.example.data.models.EventNew
import com.example.app.databinding.FragmentRecommendationsBinding
import com.example.extensions.dp
import com.example.extensions.findGroupBy
import com.example.extensions.updateGroup
import com.example.extensions.updateItem
import com.example.ui.event.list.EventListFragment
import com.example.ui.event.list.recommendations.items.RecommendationItemsGroup
import com.example.ui.event.my.schedule.items.NoScheduleEventItem
import com.example.ui.profile.ProfileFragmentArgs
import com.example.util.pagination.PaginationGroupAdapter
import com.example.util.smoothScrollToFirstItem
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.offsetChangedListener
import com.example.ui.event.list.EventListFragmentNew
import com.example.ui.views.dialogs.StateType
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs
import kotlin.reflect.KClass

class RecommendationsFragment : EventListFragmentNew<RecommendationsPresenter, FragmentRecommendationsBinding>(),
    RecommendationsContract.View {

    @InjectPresenter
    override lateinit var presenter: RecommendationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<RecommendationsPresenter>

    @ProvidePresenter
    fun providePresenter(): RecommendationsPresenter = presenterProvider.get().apply {
        try {
            val args = RecommendationsFragmentArgs.fromBundle(requireArguments())
            this.onShowSavedEventOrProfile(args.isOpenProfile)
        } catch (_: Exception) {
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            eventsList.apply {
                adapter = pagingAdapter.withLoadStateAdapters(
                    EventPlaceholderAdapter(1),
                    EventPlaceholderAdapter(1)
                ) { tvEmptyData.isVisible = it }
            }

            etSearch.setOnClickListener { presenter.onSearchClick() }
            btnLogin.setOnClickListener { presenter.onShowAuthorization(null) }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
            appBarLayout.offsetChangedListener { appBarLayout, i ->
                updateAppBarViews(abs(i / appBarLayout.totalScrollRange.toFloat()))
            }
        }
    }

    override fun setData(data: PagingData<EventNew>, isNeedUpdateApp: Boolean) {
        pagingAdapter.submitData(lifecycle, data, presenter.isTemporaryUser(), isNeedUpdateApp)
        mBinding.swipeToRefresh.isRefreshing = false
    }


    override fun setAuthorizationButton(isTemporary: Boolean) {
        mBinding.btnLogin.isVisible = isTemporary
    }

    override fun showSearch() {
        findNavController().navigate(R.id.search_tabs_fragment)
    }

    override fun showUserProfile() {
        val args = ProfileFragmentArgs.Builder(true).build().toBundle()
        findNavController().navigate(R.id.profile_fragment, args)
    }

    override fun scrollToFirstItem() {
        val mLayoutManager = mBinding.eventsList.layoutManager as LinearLayoutManager
        mLayoutManager.smoothScrollToFirstItem(requireContext(), mBinding.appBarLayout, 1)
    }

    override fun onExpandedState() {
        mBinding.apply {
            tvLabelSmall.apply {
                alpha = 1F
                animate().setDuration(500).alpha(0.0f)
                visibility = View.GONE
            }
            tvLabelLarge.apply {
                visibility = View.VISIBLE
                alpha = 0F
                animate().setDuration(500).alpha(1.0f)
            }
        }
    }

    override fun onCollapsedState() {
        mBinding.apply {
            tvLabelSmall.apply {
                alpha = 0F
                animate().setDuration(500).alpha(1.0f)
                tvLabelSmall.visibility = View.VISIBLE
            }
            tvLabelLarge.apply {
                alpha = 1F
                animate().setDuration(500).alpha(0.0f)
                visibility = View.GONE
            }
        }
    }

    override fun layout(): Int = R.layout.fragment_recommendations
}