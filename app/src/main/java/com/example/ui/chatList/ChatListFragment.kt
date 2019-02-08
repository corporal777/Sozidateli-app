package com.example.ui.chatList

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.SimplePagingRecyclerViewAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.UserChat
import com.example.ui.base.BaseFragment
import com.example.util.CropCircleTransformation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.item_chat.*
import javax.inject.Inject
import javax.inject.Provider

class ChatListFragment : BaseFragment(), ChatListContract.View {

    @InjectPresenter
    lateinit var presenter: ChatListPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChatListPresenter>

    @ProvidePresenter
    fun providePresenter(): ChatListPresenter = presenterProvider.get()

    private val adapter: SimplePagingRecyclerViewAdapter<UserChat> by lazy {
        object : SimplePagingRecyclerViewAdapter<UserChat>(
                { oldItem, newItem -> oldItem.id == newItem.id },
                { oldItem, newItem -> oldItem == newItem }
        ) {
            override fun getItemLayout(itemView: Int) = R.layout.item_chat

            override fun onBindItem(viewHolder: ViewHolder, item: UserChat?, position: Int) {
                item!!
                viewHolder.apply {
                    if (!item.user?.user_avatar.isNullOrEmpty()) {
                        Picasso.get()
                                .load(item.user?.user_avatar)
                                .transform(CropCircleTransformation())
                                .placeholder(R.drawable.ic_launcher)
                                .into(ivAvatar)
                    }
                    tvName.text = item.user?.user_name
                    tvLastMessage.text = item.lastMessage?.text
                    itemView.setOnClickListener { presenter.onChatClick(item) }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        recyclerView.apply {
            adapter = this@ChatListFragment.adapter
            if (itemDecorationCount == 0) addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        }
    }

    override fun setData(data: PagedList<UserChat>) {
        adapter.submitList(data)
    }

    override fun openChat(chatId: String) {
        findNavController().navigate(ChatListFragmentDirections.chatListToChat("Tecтовый чат", chatId))
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
