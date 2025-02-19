package com.example.ui.chatList.contacts

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapters.UserPagingAdapter
import com.example.adapters.UserPagingAdapter.Companion.withLoadStateAdapters
import com.example.adapters.UserPlaceholderAdapter
import com.example.adapters.chats.UserChatsAdapter
import com.example.adapters.chats.UserChatsAdapter.Companion.withLoadStateAdapters
import com.example.app.R
import com.example.app.databinding.FragmentChatListBinding
import com.example.data.models.UserChat
import com.example.data.models.UserChatModel
import com.example.data.models.UserDetail
import com.example.extensions.findGroupBy
import com.example.extensions.isVisibleAnim
import com.example.extensions.updateItem
import com.example.holders.ChatListEmptyItem
import com.example.holders.ListSectionNameItem
import com.example.holders.PlaceholderItem
import com.example.holders.UserItem
import com.example.ui.base.BaseVBFragment
import com.example.ui.chatList.contacts.items.UserChatGroup
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.smoothScrollToFirstItem
import com.google.android.material.appbar.AppBarLayout
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class ChatListFragment : BaseVBFragment<FragmentChatListBinding>(), ChatListContract.View {

    @InjectPresenter
    lateinit var presenter: ChatListPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChatListPresenter>

    @ProvidePresenter
    fun providePresenter(): ChatListPresenter = presenterProvider.get()



    private val pagingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        UserChatsAdapter { presenter.onChatClick(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            chatList.adapter = pagingAdapter.withLoadStateAdapters(
                UserPlaceholderAdapter(9),
                UserPlaceholderAdapter(1)
            ) { setDataEmpty(it) }

            setDataEmpty(isEmptyData)

            btnCreateChat.setOnClickListener { presenter.onAddChatClick() }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setData(data: PagingData<UserChatModel>) {
        pagingAdapter.submitData(lifecycle, data)
        mBinding.swipeToRefresh.isRefreshing = false
    }

    override fun setChatUnreadMessageCount(chatId: String, count: Int) {

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

    override fun setDataEmpty(show: Boolean) {
        super.setDataEmpty(show)
        mBinding.tvNoChats.isVisibleAnim = show
        mBinding.btnCreateChat.isVisibleAnim = show
    }


    fun smoothScrollToFirstItem(appBarLayout: AppBarLayout) {
        val mLayoutManager = mBinding.chatList.layoutManager as LinearLayoutManager
        mLayoutManager.smoothScrollToFirstItem(requireContext(), appBarLayout, 3)
    }

    override fun binding() = FragmentChatListBinding::class.java
    override fun layout() = R.layout.fragment_chat_list
}
