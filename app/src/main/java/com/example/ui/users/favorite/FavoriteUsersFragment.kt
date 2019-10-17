package com.example.ui.users.favorite

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.user.User
import com.example.holders.TitledSection
import com.example.holders.UserItem
import com.example.ui.base.BaseFragment
import com.example.ui.views.UserSubscribeButton
import com.example.util.LayoutListWithPlaceholderUtil
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*
import javax.inject.Inject
import javax.inject.Provider

class FavoriteUsersFragment : BaseFragment(), FavoriteUsersContract.View {

    @InjectPresenter
    lateinit var presenter: FavoriteUsersPresenter

    @Inject
    lateinit var presenterProvider: Provider<FavoriteUsersPresenter>

    @ProvidePresenter
    fun providePresenter(): FavoriteUsersPresenter = presenterProvider.get()

    private lateinit var placeholderUtil: LayoutListWithPlaceholderUtil

    private val usersSection = TitledSection(-100L)

    private val adapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(usersSection)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    if (position > 0) presenter.onItemTake(position - 1)
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@FavoriteUsersFragment.adapter

            placeholderUtil = LayoutListWithPlaceholderUtil(view).apply {
                doNotShowUntilDataLoad = true
                setMessage(getString(R.string.banned_empty_message))
                setImage(R.drawable.ic_neutral_face)
            }
        }
    }

    override fun setData(data: List<User>) {
        usersSection.update(data.map {
            UserItem(
                    it.user_id,
                    it.fullName,
                    it.user_avatar,
                    { presenter.onUserClick(it) },
                    UserSubscribeButton.Action.UNFAVORITE,
                    { presenter.onUserRemoveFromFavoritesClick(it) }
            )
        })
        placeholderUtil.isDataLoad = true
    }

    override fun showUser(user: User) {
        findNavController().navigate(R.id.user_fragment, bundleOf("userId" to user.user_id.toString()))
    }

    override fun layout() = R.layout.layout_list_with_placeholder
}