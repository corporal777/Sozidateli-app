package com.example.ui.users.favorite

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.user.User
import com.example.holders.NoDataItem
import com.example.holders.PlaceholderItem
import com.example.holders.TitledSection
import com.example.holders.UserItem
import com.example.ui.base.BaseFragment
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list.*
import javax.inject.Inject
import javax.inject.Provider

class FavoriteUsersFragment : BaseFragment(), FavoriteUsersContract.View {

    @InjectPresenter
    lateinit var presenter: FavoriteUsersPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteUsersPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteUsersPresenter = presenterProvider.get()

    private val adapter by lazy {
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
        recyclerView.apply {
            adapter = this@FavoriteUsersFragment.adapter
        }
        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    override fun setData(data: List<User?>) {
        if (data.isEmpty()) {
            adapter.update(listOf(NoDataItem(/*getString(R.string.empty_list_placeholder_message)*/"",
                    getString(R.string.user_favorites_empty_list_description))))
        } else {
            adapter.update(data.map {
                if (it == null) PlaceholderItem(PlaceholderItem.Type.USER)
                else UserItem(
                        it.user_id,
                        it.fullName,
                        it.user_city,
                        it.user_avatar,
                        { presenter.onUserClick(it) },
                        it.getUserSubscribeAction(),
                        { presenter.onUserRemoveFromFavoritesClick(it) }
                )
            })
        }

        swipeToRefresh.isRefreshing = false
    }

    override fun showUser(user: User) {
        findNavController().navigate(R.id.user_fragment, bundleOf("userId" to user.user_id.toString()))
    }

    override fun layout() = R.layout.layout_list
}