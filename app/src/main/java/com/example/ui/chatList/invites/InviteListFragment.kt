package com.example.ui.chatList.invites

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserChat
import com.example.holders.ListSectionNameItem
import com.example.holders.PlaceholderItem
import com.example.holders.UserChatItem
import com.example.ui.base.BaseFragment
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.layout_list.*
import javax.inject.Inject
import javax.inject.Provider

class InviteListFragment : BaseFragment(), InviteListContract.View {

    @InjectPresenter
    lateinit var presenter: InviteListPresenter

    @Inject
    lateinit var presenterProvider: Provider<InviteListPresenter>

    @ProvidePresenter
    fun providePresenter(): InviteListPresenter = presenterProvider.get()

    private val inviteSection by lazy {
        Section().apply {
            setHeader(ListSectionNameItem(-300L))
            setHideWhenEmpty(true)
        }
    }

    private val adapter by lazy {
        PaginationListGroupAdapter<com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder>().apply {
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    if (position > 0) presenter.onItemTake(position - 1)
                }
            })

            add(inviteSection)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@InviteListFragment.adapter
        }

        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }

    override fun setInvitesData(chats: List<UserChat?>) {
        inviteSection.update(chats.map { chat ->
            if (chat == null) PlaceholderItem(PlaceholderItem.Type.CHAT_LIST)
            else UserChatItem(
                    chat,
                    { presenter.onChatClick(it) }
            )
        })

        swipeToRefresh.isRefreshing = false
    }

    override fun openChat(chatId: Int, userName: String) {
        findNavController().navigate(R.id.chat_fragment, bundleOf("label" to userName, "chatId" to chatId.toString()))
    }

    override fun layout() = R.layout.layout_list
}
