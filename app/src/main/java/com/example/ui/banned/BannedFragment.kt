package com.example.ui.banned

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserChat
import com.example.databinding.LayoutListBinding
import com.example.holders.NoDataItem
import com.example.holders.PlaceholderItem
import com.example.holders.UserItem
import com.example.interfaces.ToolbarFragmentNew
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.UserSubscribeButton
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class BannedFragment : BaseFragmentNew<LayoutListBinding>(), BannedContract.View,
    ToolbarFragmentNew {

    @InjectPresenter
    lateinit var presenter: BannedPresenter

    var mDy = 0

    @Inject
    lateinit var presenterProvider: Provider<BannedPresenter>

    @ProvidePresenter
    fun providePresenter(): BannedPresenter = presenterProvider.get()

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
        mBinding.apply {
            recyclerView.apply {
                adapter = this@BannedFragment.adapter
                onScrolled { dx, dy ->
                    presenter.changeScrollingOffset(this.computeVerticalScrollOffset())
                }
            }

            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setItems(userChats: List<UserChat?>) {
        if (userChats.isEmpty()) {
            adapter.update(listOf(NoDataItem(getString(R.string.empty_list_placeholder_message))))
        } else {
            adapter.update(userChats.map {
                if (it == null) PlaceholderItem(PlaceholderItem.Type.USER)
                else UserItem(
                        it.id,
                        it.user.nameLastName,
                        it.user.address?.getShortAddress()/*user_city*/,
                        it.user.image.uri/*user_avatar*/,
                        { presenter.onUserClick(it) },
                        UserSubscribeButton.Action.UNBLOCK,
                        { presenter.onUnblockLick(it) }
                )
            })
        }

        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun openUserInfo(userId: String) {
        findNavController().navigate(BannedFragmentDirections.bannedFragmentToUserFragment(userId))
    }


    override fun layout() = R.layout.layout_list
    override val title: CharSequence by lazy { getString(R.string.profile_banned) }
    override val actionIconHidden: Boolean = true
    override val actionIcon: Drawable? = null
    override fun actionIconClick() {}
    override fun toolbarTitleClick() {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
