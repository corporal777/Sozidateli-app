package com.example.ui.chatList

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.holders.PagedListGroup
import com.example.holders.UserChatItem
import com.example.ui.base.BaseFragment
import com.xwray.groupie.GroupAdapter
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

    private val chatGroup = PagedListGroup<UserChatItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        recyclerView.apply {
            adapter = GroupAdapter<ViewHolder>().apply {
                add(chatGroup)
            }
            if (itemDecorationCount == 0) addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        }
        btnCreateChat.setOnClickListener { presenter.onMenuAddChatClick() }
    }

    override fun setData(data: PagedList<UserChatItem>) {
        chatGroup.submitList(data)
    }

    override fun showEmptyView(isShow: Boolean) {
        emptyView.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun openChat(chatId: Int, userId: String, userName: String) {
        findNavController().navigate(ChatListFragmentDirections.chatListToChat(userName, chatId.toString(), userId))
    }

    override fun openSearchContact() {
        findNavController().navigate(ChatListFragmentDirections.actionChatListFragmentToContactsSearchFragment())
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
