package com.example.ui.banned

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.app.databinding.LayoutListBinding
import com.example.data.models.UserChat
import com.example.extensions.updateItem
import com.example.holders.NoDataItem
import com.example.holders.PlaceholderItem
import com.example.holders.UserItem
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.UserSubscribeButton
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class BannedFragment : BaseToolbarFragment<LayoutListBinding>(), BannedContract.View {

    @InjectPresenter
    lateinit var presenter: BannedPresenter

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
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setItems(userChats: List<UserChat?>) {
        if (userChats.isEmpty()) adapter.updateItem(NoDataItem(getString(R.string.empty_list_placeholder_message)))
        else {
            adapter.update(userChats.map {
                if (it == null) PlaceholderItem(PlaceholderItem.Type.USER)
                else UserItem(
                        it.id,
                        it.user.nameLastName,
                        it.user.address?.getShortAddress(),
                        it.user.loadUserImage(),
                        { presenter.onUserClick(it) },
                        UserSubscribeButton.Action.UNBLOCK,
                        { presenter.onUnblockLick(it) }
                )
            })
        }

        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun openUserInfo(userId: String) {
        val args = UserFragmentArgs.Builder(userId).build().toBundle()
        findNavController().navigate(R.id.user_fragment, args)
    }

    override fun layout() = R.layout.layout_list
    override fun binding() = LayoutListBinding::class.java
    override val title: CharSequence by lazy { getString(R.string.profile_banned) }
    override fun scrollingView(): View = mBinding.recyclerView
}
