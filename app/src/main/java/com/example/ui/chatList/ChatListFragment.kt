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
import com.squareup.picasso.NetworkPolicy
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
                { oldItem, newItem ->
                    oldItem.id == newItem.id
                            && oldItem.lastMessage == newItem.lastMessage
                            && oldItem.user.user_name == newItem.user.user_name
                }
        ) {
            override fun getItemLayout(itemView: Int) = R.layout.item_chat

            override fun onBindItem(viewHolder: ViewHolder, item: UserChat?, position: Int) {
                Picasso.get().isLoggingEnabled = true
                item!!
                viewHolder.apply {
                    Picasso.get().load(item.user.user_avatar.let { if (it.isNullOrBlank()) null else it })
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .transform(CropCircleTransformation())
                            .placeholder(R.drawable.avatar_placeholder)
                            .into(ivAvatar)

                    tvName.text = item.user.fullName
                    tvLastMessage.text = item.lastMessage ?: "-"
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
        btnCreateChat.setOnClickListener { presenter.onMenuAddChatClick() }
    }


    override fun showEmptyView(isShow: Boolean) {
        emptyView.visibility = if(isShow) View.VISIBLE else View.GONE
    }

    override fun setData(data: PagedList<UserChat>) {
        adapter.submitList(data)
    }

    override fun openChat(chatId: String, userId: String, userName: String) {
        findNavController().navigate(ChatListFragmentDirections.chatListToChat(userName, chatId, userId))

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
