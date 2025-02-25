package com.example.ui.search.user

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.example.app.R
import com.example.app.databinding.LayoutListSearchBinding
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.ui.search.SearchFragment
import com.example.adapters.user.UserPagingAdapter
import com.example.adapters.user.UserPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.user.UserPlaceholderAdapter
import com.example.extensions.isVisibleAnim
import com.example.ui.views.filters.user.UserFiltersBottomSheetDialog
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SearchUserFragment : SearchFragment<LayoutListSearchBinding, SearchUserPresenter>(),
    SearchUserContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchUserPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchUserPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchUserPresenter = presenterProvider.get()


    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        UserPagingAdapter({ presenter.onUserClick(it) }, { presenter.onUserActionCLick(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            searchList.adapter = pagingAdapter.withLoadStateAdapters(
                UserPlaceholderAdapter(9),
                UserPlaceholderAdapter(1)
            ) { setEmptyDataPlaceholder(it) }
            setEmptyDataPlaceholder(isEmptyData)
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setData(data: PagingData<UserDetail>) {
        pagingAdapter.submitData(lifecycle, data)
        mBinding.swipeToRefresh.isRefreshing = false
    }


    override fun updateUser(user: UserDetail) {
        pagingAdapter.updateUserFavorite(user)
    }

    override fun showFilter(filter: SearchFilter.UserNew) {
        UserFiltersBottomSheetDialog(requireContext(), filter)
            .setFiltersSelected { presenter.onFiltersApplyClick(it) }
            .show()
    }

    override fun showUser(user: UserDetail) {
        findNavController().navigate(R.id.user_fragment, bundleOf("userId" to user.id.toString()))
    }

    override fun showCurrentUser() {
        findNavController().navigate(R.id.user_profile_fragment)
    }

    override fun setEmptyDataPlaceholder(show: Boolean) {
        super.setEmptyDataPlaceholder(show)
        mBinding.tvEmptyData.isVisibleAnim = show
    }

    override fun binding() = LayoutListSearchBinding::class.java
    override fun layout() = R.layout.layout_list_search
}