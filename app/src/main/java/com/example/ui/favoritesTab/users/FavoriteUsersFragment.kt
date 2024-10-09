package com.example.ui.favoritesTab.users

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.data.models.UserDetail
import com.example.app.databinding.LayoutListBinding
import com.example.extensions.updateItem
import com.example.holders.PlaceholderItem
import com.example.holders.UserItem
import com.example.ui.base.BaseFragment
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class FavoriteUsersFragment : BaseFragment<LayoutListBinding>(), FavoriteUsersContract.View {

    @InjectPresenter
    lateinit var presenter: FavoriteUsersPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteUsersPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteUsersPresenter = presenterProvider.get()

    private val groupAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    presenter.onItemTake(position)
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            recyclerView.apply {
                adapter = groupAdapter
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setData(data: List<UserDetail?>) {
        groupAdapter.update(data.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.USER)
            else UserItem(
                it.id,
                it.nameLastName,
                it.address?.city,
                it.loadUserImage(),
                { presenter.onUserClick(it) },
                it.getUserSubscribeAction(),
                { presenter.onUserRemoveFromFavoritesClick(it) })
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun setUsersFavoriteEmptyPlaceholder() {
        groupAdapter.updateItem(
            NoEventItem(
                getString(R.string.blank_list_error),
                getString(R.string.user_favorites_empty_list_description)
            )
        )
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun showUser(user: UserDetail) {
        findNavController().navigate(R.id.user_fragment, bundleOf("userId" to user.id.toString()))
    }

    override fun layout() = R.layout.layout_list
}