package com.example.ui.favoritesTab.users

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapters.user.UserPagingAdapter
import com.example.adapters.user.UserPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.user.UserPlaceholderAdapter
import com.example.app.R
import com.example.app.databinding.LayoutDataListBinding
import com.example.data.models.UserDetail
import com.example.extensions.isVisibleAnim
import com.example.ui.base.BaseVBFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class FavoriteUsersFragment : BaseVBFragment<LayoutDataListBinding>(), FavoriteUsersContract.View {

    @InjectPresenter
    lateinit var presenter: FavoriteUsersPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteUsersPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteUsersPresenter = presenterProvider.get()


    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        UserPagingAdapter(
            { presenter.onUserClick(it) },
            { presenter.onUserRemoveFavoritesClick(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            dataListView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = pagingAdapter.withLoadStateAdapters(
                    UserPlaceholderAdapter(9),
                    UserPlaceholderAdapter(1)
                ) { setEmptyDataPlaceholder(it) }
                setEmptyDataPlaceholder(isEmptyData)
            }
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


    override fun setEmptyDataPlaceholder(show: Boolean) {
        super.setEmptyDataPlaceholder(show)
        mBinding.tvEmptyDataTitle.apply {
            isVisibleAnim = show
            text = getString(R.string.blank_list_error)
        }
        mBinding.tvEmptyDataDescription.apply {
            isVisibleAnim = show
            text = getString(R.string.user_favorites_empty_list_description)
        }
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showUser(user: UserDetail) {
        findNavController().navigate(R.id.user_fragment, bundleOf("userId" to user.id.toString()))
    }

    override fun binding() = LayoutDataListBinding::class.java
    override fun layout() = R.layout.layout_data_list
}