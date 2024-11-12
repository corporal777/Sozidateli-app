package com.example.ui.search.user

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.app.R
import com.example.app.databinding.LayoutListSearchBinding
import com.example.data.models.SearchFilter
import com.example.data.models.UserDetail
import com.example.ui.search.SearchFragment
import com.example.adapters.UserPagingAdapter
import com.example.adapters.UserPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.UserPlaceholderAdapter
import com.example.ui.views.filters.user.UserFiltersBottomSheetDialog
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SearchUserFragment :
    SearchFragment<SearchUserPresenter, SearchFilter.UserNew>(R.layout.layout_list_search),
    SearchUserContract.View {

    @InjectPresenter
    override lateinit var presenter: SearchUserPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchUserPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchUserPresenter = presenterProvider.get()

    private val viewBinding: LayoutListSearchBinding by viewBinding()

    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        UserPagingAdapter({ presenter.onUserClick(it) }, { presenter.onUserActionCLick(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.apply {
            searchList.adapter = pagingAdapter
                .withLoadStateAdapters(
                    UserPlaceholderAdapter(9),
                    UserPlaceholderAdapter(1)
                ) { setDataEmpty(it, getString(R.string.no_data_found)) }
            swipeToRefresh.setOnRefreshListener {
                presenter.onRefreshRequest()
            }
        }
    }

    override fun setData(data: PagingData<UserDetail>) {
        pagingAdapter.submitData(lifecycle, data)
        viewBinding.swipeToRefresh.isRefreshing = false
    }

    override fun invalidatePagingData() {
        pagingAdapter.refresh()
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
}