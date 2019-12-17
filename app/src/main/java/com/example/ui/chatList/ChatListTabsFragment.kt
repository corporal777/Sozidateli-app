package com.example.ui.chatList

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.extensions.dp
import com.example.extensions.sp
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.chatList.contacts.ChatListFragment
import com.example.ui.chatList.invites.InviteListFragment
import com.example.ui.views.BadgeDrawable
import com.example.ui.views.addBadge
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

    private val invitesBadge by lazy {
        BadgeDrawable(badgeBackgroundColor = ContextCompat.getColor(requireContext(), R.color.badge_attention_high), shouldDrawText = false, badgeTextSize = 6f.sp)
    }

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
        setupInvitesBadge()

        btnTabChats.setOnClickListener { viewPager.currentItem = 0 }
        btnTabRequests.setOnClickListener { viewPager.currentItem = 1 }
    }

    private fun setupInvitesBadge() {
        btnTabRequests.doOnNextLayout {
            it.addBadge(invitesBadge) { badgeWidth, badgeHeight, anchorRect ->
                val badgeCenterX = anchorRect.right - 24.dp
                val badgeCenterY = anchorRect.height() / 2

                anchorRect.set(
                        badgeCenterX,
                        badgeCenterY - badgeHeight / 2,
                        badgeCenterX + badgeWidth,
                        badgeCenterY + badgeHeight / 2
                )
            }
        }
    }

    override fun selectTab(position: Int) {
        clTabs.apply {
            for (p in 0 until childCount) {
                getChildAt(p).isSelected = p == position
            }
        }
    }

    override fun setInvitesCount(count: Int) {
        invitesBadge.apply {
            number = count
            invalidateSelf()
        }
    }

    override fun layout() = R.layout.fragment_chat_list_tabs
}
