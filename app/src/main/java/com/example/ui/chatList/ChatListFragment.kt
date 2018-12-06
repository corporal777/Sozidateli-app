package com.example.ui.chatList

import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.view.View
import androidx.navigation.fragment.findNavController
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
                    Picasso.get()
                            .load(item.user?.image)
                            .transform(CropCircleTransformation())
                            .placeholder(R.drawable.ic_launcher)
                            .into(ivAvatar)

                    tvName.text = item.user?.name
                    tvLastMessage.text = item.lastMessage?.text
                    itemView.setOnClickListener { presenter.onChatClick(item) }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@ChatListFragment.adapter
            if (itemDecorationCount == 0) addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun setData(data: PagedList<UserChat>) {
        adapter.submitList(data)
    }

    override fun openChat(userOpponent: String, chatId: String) {
        findNavController().navigate(ChatListFragmentDirections.chatListToChat(chatId, userOpponent))
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_chat_list
}
