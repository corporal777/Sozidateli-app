package com.example.ui.chatList

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.holders.ChatListEmptyItem
import com.example.holders.ChatListHeaderItem
import com.example.holders.PagedListGroup
import com.example.holders.UserChatItem
import com.example.ui.base.BaseFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
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

    private val chatsGroup = PagedListGroup<UserChatItem>()
    private val chatSection = Section().apply { add(chatsGroup) }
    private val headerItem = ChatListHeaderItem({ presenter.onInputClick() }, { presenter.onInputFilterClick() })

    private val adapter = GroupAdapter<ViewHolder>().apply {
        addAll(listOf(headerItem, chatSection))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        recyclerView.apply {
            adapter = this@ChatListFragment.adapter
        }
    }

    override fun setChats(data: PagedList<UserChatItem>) {
        chatsGroup.submitList(data)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_chat_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addChat -> presenter.onMenuAddChatClick()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_chat_list
}
