package com.example.ui.chatList.invites

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserChat
import com.example.databinding.FragmentInviteListBinding
import com.example.extensions.dp
import com.example.extensions.updateItem
import com.example.holders.ListSectionNameItem
import com.example.holders.NoDataItem
import com.example.holders.PlaceholderItem
import com.example.holders.UserChatItem
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.event.my.schedule.items.NoScheduleEventItem
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.smoothScrollToFirstItem
import com.google.android.material.appbar.AppBarLayout
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import javax.inject.Inject
import javax.inject.Provider

class InviteListFragment : BaseFragmentNew<FragmentInviteListBinding>(), InviteListContract.View {

    @InjectPresenter
    lateinit var presenter: InviteListPresenter

    @Inject
    lateinit var presenterProvider: Provider<InviteListPresenter>

    @ProvidePresenter
    fun providePresenter(): InviteListPresenter = presenterProvider.get()

    private val invitesSection by lazy { Section() }

    private val adapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(invitesSection)
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
                adapter = this@InviteListFragment.adapter
                //updatePadding(top = 24.dp)
            }

            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setInvitesData(chats: List<UserChat?>) {
        if (chats.isEmpty()) invitesSection.updateItem(
            NoScheduleEventItem(
                getString(R.string.empty_list_placeholder_message),
                padding = 70.dp
            )
        )
        else invitesSection.apply {
            update(listOf(ListSectionNameItem(-300L, getString(R.string.chat_list_chat_requests)))
                .plus(
                    chats.mapIndexed { index, chat ->
                        if (chat == null) PlaceholderItem(PlaceholderItem.Type.CHAT_LIST)
                        else {
                            val chatsCount = chats.size
                            UserChatItem(
                                chat,
                                { presenter.onChatClick(it) },
                                withDivider = index != chatsCount - 1
                            )
                        }
                    }
                ))
        }

        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun openChat(chatId: Int, userName: String) {
        findNavController().navigate(
            R.id.chat_fragment,
            bundleOf("name" to userName, "chatId" to chatId.toString())
        )
    }

    fun smoothScrollToFirstItem(appBarLayout: AppBarLayout) {
        val mLayoutManager = mBinding.recyclerView.layoutManager as LinearLayoutManager
        mLayoutManager.smoothScrollToFirstItem(requireContext(), appBarLayout, 3)
    }

    override fun layout() = R.layout.fragment_invite_list
}
