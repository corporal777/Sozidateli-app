package com.example.ui.chatList.contacts

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Speaker
import com.example.data.models.UserChat
import com.example.data.models.user.User
import com.example.holders.ChatListEmptyItem
import com.example.holders.ListSectionNameItem
import com.example.holders.UserChatItem
import com.example.holders.UserItem
import com.example.ui.base.BaseFragment
import com.example.ui.contactsSearch.ContactsSearchFragment.Companion.SEARCH_ACTION_INPUT
import com.example.ui.views.BadgeDrawable
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat_list.*
import javax.inject.Inject
import javax.inject.Provider

class ChatListFragment : BaseFragment(), ChatListContract.View {

    @InjectPresenter
    lateinit var presenter: ChatListPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChatListPresenter>

    @ProvidePresenter
    fun providePresenter(): ChatListPresenter = presenterProvider.get()

    private val chatSection by lazy {
        Section().apply {
            setHideWhenEmpty(true)
        }
    }

    private val favoritesSection by lazy {
        Section().apply {
            setHeader(ListSectionNameItem(-200L, getString(R.string.search_contact_section_favorites)))
            setHideWhenEmpty(true)
        }
    }

    private val adapter by lazy {
        PaginationListGroupAdapter<ViewHolder>().apply {
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    if (position > 0) presenter.onItemTake(position - 1)
                }
            })

            add(chatSection)
            add(favoritesSection)
        }
    }

    private val badgeColor by lazy { ContextCompat.getColor(requireContext(), R.color.badge_attention_high) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        recyclerView.apply {
            adapter = this@ChatListFragment.adapter
        }

        fabNewChat.setOnClickListener { presenter.onFabAddChatClick() }
    }

    override fun setChatsData(chats: List<UserChat>, favorites: List<User>) {
        if (chats.isEmpty()) {
            chatSection.removeHeader()
            chatSection.update(listOf(ChatListEmptyItem { presenter.onEmptyChatsButtonAddChatClick() }))
        } else {
            chatSection.apply {
                if (groupCount == 0 || getGroup(0) != CHAT_SECTION_HEADER) {
                    setHeader(CHAT_SECTION_HEADER)
                }
                update(chats.map { chat ->
                    UserChatItem(
                            chat,
                            { presenter.onChatClick(it) },
                            { presenter.onChatOnScreen(chat.id) },
                            { presenter.onChatGoneFromScreen(chat.id) },
                            BadgeDrawable(badgeBackgroundColor = badgeColor)
                    )
                })
            }
        }

        favoritesSection.update(favorites.map {
            UserItem(it.user_id, it.fullName, it.user_avatar) {
                presenter.onUserClick(it.user_id, it.fullName)
            }
        })
    }

    override fun setChatUnreadMessageCount(chatId: String, count: Int) {
        for (i in 0 until chatSection.itemCount) {
            val item = chatSection.getItem(i)
            if (item is UserChatItem && item.userChat.id.toString() == chatId) {
                if (item.userChat.unreadMessageCount != count) {
                    item.userChat.unreadMessageCount = count
                    item.notifyChanged()
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
        findNavController().navigate(R.id.contacts_search_fragment, bundleOf("searchAction" to SEARCH_ACTION_INPUT))
    }

    override fun layout() = R.layout.fragment_chat_list

    companion object {
        private val CHAT_SECTION_HEADER = ListSectionNameItem(-300L)
    }
}
