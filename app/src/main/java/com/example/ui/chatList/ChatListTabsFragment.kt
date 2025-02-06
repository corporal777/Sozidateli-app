package com.example.ui.chatList

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.adapters.PagerStateAdapter
import com.example.app.databinding.FragmentChatListTabsBinding
import com.example.ui.base.BaseFragment
import com.example.ui.chatList.contacts.ChatListFragment
import com.example.ui.chatList.invites.InviteListFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.offsetChangedListener
import com.example.extensions.onPageChanged
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class ChatListTabsFragment : BaseFragment<FragmentChatListTabsBinding>(), ChatListTabsContract.View {

    @InjectPresenter
    lateinit var presenter: ChatListTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChatListTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): ChatListTabsPresenter = presenterProvider.get()

    private val pageChangeListener = onPageChanged { position ->
        selectTab(position)
    }

    private val fragments by lazy { listOf(ChatListFragment(), InviteListFragment()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            viewPager.apply {
                adapter = object : PagerStateAdapter(childFragmentManager){
                    override fun getItem(position: Int) = fragments[position]
                    override fun getCount() = fragments.size
                }
                selectTab(currentItem)
                addOnPageChangeListener(pageChangeListener)
            }

            btnTabChats.setOnClickListener { viewPager.currentItem = 0 }
            btnTabRequests.setOnClickListener { viewPager.currentItem = 1 }
            fabNewChat.setOnClickListener { presenter.onFabAddChatClick() }
            appBarLayout.offsetChangedListener { appBarLayout, i ->
                updateAppBarViews(abs(i / appBarLayout.totalScrollRange.toFloat()))

            }
        }
    }

    override fun selectTab(position: Int) {
        when (position) {
            0 -> {
                mBinding.apply {
                    if (!fabNewChat.isShown) fabNewChat.show()
                    btnTabChats.isSelected = true
                    btnTabRequests.isSelected = false
                }
            }
            1 -> {
                mBinding.apply {
                    if (fabNewChat.isShown) fabNewChat.hide()
                    btnTabRequests.isSelected = true
                    btnTabChats.isSelected = false
                }
            }
        }
    }

    override fun setInvitesCount(count: Int) {
        mBinding.tvInvitesBadge.isVisible = count > 0
    }

    override fun setChatsCount(count: Int) {
        mBinding.tvChatsBadge.isVisible = count > 0
    }

    override fun scrollToFirstItem() {
        if (mBinding.viewPager.currentItem == 0) {
            (fragments[0] as ChatListFragment).smoothScrollToFirstItem(mBinding.appBarLayout)
        } else {
            (fragments[1] as InviteListFragment).smoothScrollToFirstItem(mBinding.appBarLayout)
        }
    }

    override fun openSearch() {
        findNavController().navigate(R.id.chat_search_fragment)
    }

    override fun onExpandedState() {
        mBinding.apply {
            tvLabelSmall.apply {
                alpha = 1F
                animate().setDuration(500).alpha(0.0f)
                visibility = View.GONE
            }
            tvLabelLarge.apply {
                visibility = View.VISIBLE
                alpha = 0F
                animate().setDuration(500).alpha(1.0f)
            }
        }
    }

    override fun onCollapsedState() {
        mBinding.apply {
            tvLabelSmall.apply {
                alpha = 0F
                animate().setDuration(500).alpha(1.0f)
                tvLabelSmall.visibility = View.VISIBLE
            }
            tvLabelLarge.apply {
                alpha = 1F
                animate().setDuration(500).alpha(0.0f)
                visibility = View.GONE
            }
        }
    }

    override fun layout() = R.layout.fragment_chat_list_tabs
}
