package com.example.ui.banned

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserChat
import com.example.holders.PlaceholderItem
import com.example.holders.TitledSection
import com.example.holders.UserItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.UserSubscribeButton
import com.example.util.LayoutListWithPlaceholderUtil
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_list_with_placeholder.*
import javax.inject.Inject
import javax.inject.Provider

class BannedFragment : BaseFragment(), BannedContract.View, ToolbarFragment {

    override val title: CharSequence
        get() = getString(R.string.profile_banned)

    @InjectPresenter
    lateinit var presenter: BannedPresenter

    @Inject
    lateinit var presenterProvider: Provider<BannedPresenter>

    @ProvidePresenter
    fun providePresenter(): BannedPresenter = presenterProvider.get()

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
            adapter = this@BannedFragment.adapter
        }

        placeholderUtil = LayoutListWithPlaceholderUtil(view).apply { setDefault() }
        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    override fun setItems(userChats: List<UserChat?>) {
        usersSection.update(userChats.map {
            if (it == null) PlaceholderItem(PlaceholderItem.Type.USER)
            else UserItem(
                    it.id,
                    it.user.fullName,
                    it.user.user_avatar,
                    { presenter.onUserClick(it) },
                    UserSubscribeButton.Action.UNBLOCK,
                    { presenter.onUnblockLick(it) }
            )
        })
        placeholderUtil.isDataLoad = true
        swipeToRefresh.isRefreshing = false
    }

    override fun openUserInfo(userId: String) {
        findNavController().navigate(BannedFragmentDirections.bannedFragmentToUserFragment(userId))
    }

    override fun layout() = R.layout.layout_list_with_placeholder
}
