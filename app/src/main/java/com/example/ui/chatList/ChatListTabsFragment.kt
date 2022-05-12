package com.example.ui.chatList

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.chatList.contacts.ChatListFragment
import com.example.ui.chatList.invites.InviteListFragment
import com.example.ui.views.ChangeStateDialog
import com.example.ui.views.StateType
import kotlinx.android.synthetic.main.fragment_chat_list_tabs.*
import javax.inject.Inject
import javax.inject.Provider

class ChatListTabsFragment : BaseFragment(), ChatListTabsContract.View, ToolbarFragment {

    override val title: String
        get() = getString(R.string.chat_list)

    @InjectPresenter
    lateinit var presenter: ChatListTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChatListTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): ChatListTabsPresenter = presenterProvider.get()

    private val pageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            when (position) {
                0 -> presenter.onChatsSelected()
                1 -> presenter.onInvitesSelected()
            }

            selectTab(position)
        }
    }

    private val fragments by lazy {
        listOf(
                ChatListFragment(),
                InviteListFragment()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.apply {
            adapter = object : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                override fun getItem(position: Int) = fragments[position]
                override fun getCount() = fragments.size
            }
            addOnPageChangeListener(pageChangeListener)
        }

        btnTabChats.setOnClickListener { viewPager.currentItem = 0 }
        btnTabRequests.setOnClickListener { viewPager.currentItem = 1 }
    }

    override fun selectTab(position: Int) {
        when (position) {
            0 -> {
                btnTabChats.isSelected = true
                btnTabRequests.isSelected = false
            }
            1 -> {
                btnTabRequests.isSelected = true
                btnTabChats.isSelected = false
            }
        }
    }

    override fun setInvitesCount(count: Int) {
        tvInvitesBadge.isVisible = count > 0
    }

    override fun setChatsCount(count: Int) {
        tvChatsBadge.isVisible = count > 0
    }

    override fun layout() = R.layout.fragment_chat_list_tabs
}
