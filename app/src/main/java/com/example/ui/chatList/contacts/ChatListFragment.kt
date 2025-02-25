package com.example.ui.chatList.contacts

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapters.user.UserPlaceholderAdapter
import com.example.adapters.chats.UserChatsAdapter
import com.example.adapters.chats.UserChatsAdapter.Companion.withLoadStateAdapters
import com.example.app.R
import com.example.app.databinding.FragmentChatListBinding
import com.example.data.models.UserChatModel
import com.example.extensions.isVisibleAnim
import com.example.ui.base.BaseVBFragment
import com.example.util.smoothScrollToFirstItem
import com.google.android.material.appbar.AppBarLayout
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
            ) { setEmptyDataPlaceholder(it) }

            setEmptyDataPlaceholder(isEmptyData)

            btnCreateChat.setOnClickListener { presenter.onAddChatClick() }
            swipeToRefresh.setOnRefreshListener { presenter.onRefreshRequest() }
        }
    }

    override fun setData(data: PagingData<UserChatModel>) {
        pagingAdapter.submitData(lifecycle, data)
        mBinding.swipeToRefresh.isRefreshing = false
    }


    override fun openChat(chatId: String, userName: String, avatar: String?) {
        val bundle = bundleOf("name" to userName, "chatId" to chatId, "userAvatar" to avatar)
        findNavController().navigate(R.id.chat_fragment, bundle)
    }

    override fun openSearch() {
        findNavController().navigate(R.id.chat_search_fragment)
    }

    override fun setEmptyDataPlaceholder(show: Boolean) {
        super.setEmptyDataPlaceholder(show)
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
