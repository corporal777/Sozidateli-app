package com.example.ui.chatList.contacts

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserChat
import com.example.data.models.UserDetail
import com.example.data.models.user.User
import com.example.holders.*
import com.example.ui.base.BaseFragment
import com.example.util.TranslateAnimationUtil
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_chat_list.*
import javax.inject.Inject
import javax.inject.Provider

class ChatListFragment(val onScrollState : OnChatListScrollingState) : BaseFragment(), ChatListContract.View {

    @InjectPresenter
    lateinit var presenter: ChatListPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChatListPresenter>

    @ProvidePresenter
    fun providePresenter(): ChatListPresenter = presenterProvider.get()

    private val chatSection by lazy { Section() }

    private val favoritesSection by lazy {
        Section().apply {
            setHeader(ListSectionNameItem(-200L, getString(R.string.search_contact_section_favorites)).apply {
                withTopMargin = true
            })
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
        var mDy = 0
        recyclerView.apply {
            adapter = this@ChatListFragment.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    mDy += dy
                    onScrollState.onScrollOffsetValue(mDy)
                    if (dy <= 0){
                        onScrollState.onScrollUp(dy)
                    }else {
                        onScrollState.onScrollDown(dy)
                    }
                }
            })
        }

        fabNewChat.apply {
            setOnClickListener { presenter.onFabAddChatClick() }
        }
        swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
    }


    override fun setChatsData(chats: List<UserChat?>, favorites: List</*User*/UserDetail>) {
        if (chats.isEmpty()) {
            chatSection.update(listOf(ChatListEmptyItem { presenter.onEmptyChatsButtonAddChatClick() }))
        } else {
            chatSection.apply {
                val chatsCount = chats.size
                update(listOf(ListSectionNameItem(-300L, getString(R.string.chat_list)))
                        .plus(chats.mapIndexed { index, chat ->
                            if (chat == null) PlaceholderItem(PlaceholderItem.Type.CHAT_LIST)
                            else UserChatItem(
                                    chat,
                                    { presenter.onChatClick(it) },
                                    { presenter.onChatOnScreen(chat.id) },
                                    { presenter.onChatGoneFromScreen(chat.id) },
                                    index != chatsCount - 1
                            )
                        }))
            }
        }

        favoritesSection.update(favorites.map {
            UserItem(it.id, it.fullName, null, it.image.uri, {
                presenter.onUserClick(it.id, it.fullName, it.binds?.chatRoomWithMe)
            })
        })
        swipeToRefresh.isRefreshing = false
    }

    override fun setChatUnreadMessageCount(chatId: String, count: Int) {
        for (i in 0 until chatSection.itemCount) {
            val item = chatSection.getItem(i)
            if (item is UserChatItem && item.userChat.id.toString() == chatId) {
                if (item.userChat.unreadMessageCount != count) {
                    item.userChat.unreadMessageCount = count
                    item.notifyChanged(count)
                }
                break
            }
        }
    }

    override fun checkScrollPosition() {
        presenter.onChatScrollChange(isChatScrolledToTop())
    }

    override fun scrollToTopPosition() {
        recyclerView.scrollToPosition(0)
    }

    private fun isChatScrolledToTop(): Boolean {
        return (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == 0
    }

    override fun openChat(chatId: Int, userName: String) {
        findNavController().navigate(R.id.chat_fragment, bundleOf("label" to userName, "chatId" to chatId.toString()))
    }

    override fun openSearch() {
        findNavController().navigate(R.id.chat_search_fragment)
    }

    override fun layout() = R.layout.fragment_chat_list

    interface OnChatListScrollingState{
        fun onScrollUp(value : Int)
        fun onScrollDown(value : Int)
        fun onScrollOffsetValue(value: Int)
    }
}
