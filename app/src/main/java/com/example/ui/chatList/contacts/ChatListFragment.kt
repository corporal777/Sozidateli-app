package com.example.ui.chatList.contacts

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserChat
import com.example.data.models.UserDetail
import com.example.databinding.FragmentChatListBinding
import com.example.extensions.findGroupBy
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.*
import com.example.ui.base.BaseFragmentNew
import com.example.ui.chatList.contacts.items.UserChatGroup
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.smoothScrollToFirstItem
import com.google.android.material.appbar.AppBarLayout
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class ChatListFragment() :
    BaseFragmentNew<FragmentChatListBinding>(), ChatListContract.View {

    @InjectPresenter
    lateinit var presenter: ChatListPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChatListPresenter>

    @ProvidePresenter
    fun providePresenter(): ChatListPresenter = presenterProvider.get()

    private val chatSection by lazy { Section() }
    private val favoritesSection by lazy {
        Section().apply {
            setHeader(
                ListSectionNameItem(
                    -200L,
                    getString(R.string.search_contact_section_favorites)
                )
            )
            setHideWhenEmpty(true)
        }
    }

    private val adapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    if (position > 0) presenter.onItemTake(position - 1)
                }
            })
            add(chatSection)
            add(favoritesSection)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            chatList.apply {
                adapter = this@ChatListFragment.adapter
                onScrolled { _, dy -> }
            }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }


    override fun setChatsData(chats: List<UserChat?>, favorites: List<UserDetail>) {
        if (chats.isEmpty()) chatSection.updateItem(ChatListEmptyItem { presenter.onAddChatClick() })
        else {
            chatSection.update(
                chats.mapIndexed { index, chat ->
                    val withDivider = index != (chats.size - 1)
                    if (chat == null) PlaceholderItem(PlaceholderItem.Type.CHAT_LIST)
                    else UserChatGroup(
                        chat,
                        { presenter.onChatClick(it) },
                        { },
                        { },
                        withDivider
                    )
                }
            )
        }

        favoritesSection.update(favorites.map {
            UserItem(it.id, it.fullName, null, it.loadUserImage(), {
                presenter.onUserClick(
                    it.id,
                    it.nameLastName,
                    it.loadUserImage(),
                    it.binds?.chatRoomWithMe
                )
            })
        })
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun setChatUnreadMessageCount(chatId: String, count: Int) {
        val item =
            chatSection.findGroupBy<UserChatGroup> { x -> x.userChat.id.toString() == chatId }
        //val item = chatSection.findItemBy<UserChatItem> { x -> x.userChat.id.toString() == chatId }
        item?.updateBadge(count)
    }

    override fun setChatUnreadMessage(chatId: String, message: String) {
        //val item = chatSection.findItemBy<UserChatItem> { x -> x.userChat.id.toString() == chatId }
        val item =
            chatSection.findGroupBy<UserChatGroup> { x -> x.userChat.id.toString() == chatId }
        if (item != null) {
            item.updateMessage(message)
            val oldPosition = chatSection.getPosition(item)
            if (oldPosition != 0 && oldPosition != 1) {
                chatSection.remove(item)
                chatSection.add(0, item)
            }
        }
    }

    override fun openChat(chatId: Int, userName: String, avatar: String?) {
        findNavController().navigate(
            R.id.chat_fragment,
            bundleOf("name" to userName, "chatId" to chatId.toString(), "userAvatar" to avatar)
        )
    }

    override fun openSearch() {
        findNavController().navigate(R.id.chat_search_fragment)
    }

    fun smoothScrollToFirstItem(appBarLayout: AppBarLayout) {
        val mLayoutManager = mBinding.chatList.layoutManager as LinearLayoutManager
        mLayoutManager.smoothScrollToFirstItem(requireContext(), appBarLayout, 3)
    }

    override fun layout() = R.layout.fragment_chat_list
}
