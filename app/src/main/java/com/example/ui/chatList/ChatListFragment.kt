package com.example.ui.chatList

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Speaker
import com.example.data.models.UserChat
import com.example.holders.*
import com.example.ui.base.BaseFragment
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.Section
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

    private val headerItem = ChatListHeaderItem(
            { presenter.onInputClick() },
            { presenter.onInputFilterClick() },
            { presenter.onShowChatListClick() },
            { presenter.onShowInvitesClick() }
    )

    private val chatSection by lazy { Section() }

    private val favoritesSection by lazy {
        Section().apply {
            setHeader(ListSectionNameItem(-200L, getString(R.string.search_contact_section_favorites)))
            setHideWhenEmpty(true)
        }
    }

    private val adapter by lazy {
        PaginationListGroupAdapter<com.xwray.groupie.kotlinandroidextensions.ViewHolder>().apply {
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    if (position > 0) presenter.onItemTake(position - 1)
                }
            })

            add(headerItem)
            add(chatSection)
            add(favoritesSection)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        recyclerView.apply {
            adapter = this@ChatListFragment.adapter
            if (itemDecorationCount == 0) addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        fabNewChat.setOnClickListener { presenter.onFabAddChatClick() }
    }

    override fun clearData() {
        chatSection.update(emptyList())
        favoritesSection.update(emptyList())
    }

    override fun setChatsData(chats: List<UserChat>, favorites: List<Speaker>) {
        chatSection.update(chats.map { chat ->
            UserChatItem(
                    chat,
                    { presenter.onChatClick(it) },
                    { presenter.onChatOnScreen(chat.id) },
                    { presenter.onChatGoneFromScreen(chat.id) }
            )
        })

        favoritesSection.update(favorites.map { UserItem(it.uid, it.name, it.photo) { } })
    }

    override fun setInvitesData(chats: List<UserChat>) {
        chatSection.update(chats.map { chat ->
            UserChatItem(
                    chat,
                    { presenter.onChatClick(it) }
            )
        })
    }

    override fun setChatUnreadMessageCount(chatId: String, count: Int) {
        for (i in 0 until chatSection.itemCount) {
            val item = chatSection.getItem(i)
            if (item is UserChatItem && item.userChat.id.toString() == chatId) {
                item.userChat.unreadMessageCount = count
                item.updateBadge()
                break
            }
        }
    }

    override fun selectChats() {
        headerItem.selectChatsButton()
    }

    override fun selectInvites() {
        headerItem.selectRequestsButton()
    }

    override fun showEmptyView(isShow: Boolean) {
        if (isShow) chatSection.setHeader(ChatListEmptyItem { presenter.onEmptyChatsButtonAddChatClick() })
        else chatSection.removeFooter()
    }

    override fun openChat(chatId: Int, userId: String, userName: String) {
        findNavController().navigate(ChatListFragmentDirections.chatListToChat(userName, chatId.toString(), userId))
    }

    override fun openSearchContact(action: Int) {
        val lm = recyclerView.layoutManager as? LinearLayoutManager
        val header = lm?.findViewByPosition(0)
        val inputView = header?.findViewById<View>(R.id.etSearch)

        val extras = inputView?.let { FragmentNavigatorExtras(it to it.transitionName) }
        findNavController().navigate(
                ChatListFragmentDirections.actionChatListFragmentToContactsSearchFragment(action),
                extras ?: FragmentNavigatorExtras()
        )
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_chat_list
}
