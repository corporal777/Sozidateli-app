package com.example.ui.chatList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.TabsFragmentAdapter
import com.example.extensions.dp
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
        BadgeDrawable(
                badgeBackgroundColor = ContextCompat.getColor(requireContext(), R.color.badge_attention_low)
        )
    }

    private val pageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            when (position) {
                0 -> presenter.onChatsSecelted()
                1 -> presenter.onInvitessSecelted()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!

        val fragments by lazy {
            listOf<Pair<Fragment, String>>(
                    ChatListFragment() to getString(R.string.chat_list_contacts_and_chats),
                    InviteListFragment() to getString(R.string.chat_list_chat_requests)
            )
        }

        view.findViewById<ViewPager>(R.id.viewPager).apply {
            adapter = TabsFragmentAdapter(fragments, childFragmentManager)
            offscreenPageLimit = fragments.size
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.apply {
            addOnPageChangeListener(pageChangeListener)
            tabLayout.setupWithViewPager(this)
        }
        setupInvitesBadge()
    }

    private fun setupInvitesBadge() {
        ((tabLayout.getChildAt(0) as? ViewGroup)?.getChildAt(1) as? ViewGroup)?.doOnNextLayout {
            (it as ViewGroup).apply {
                getChildAt(1).addBadge(invitesBadge, this) { badgeWidth, badgeHeight, anchorRect ->
                    val badgeCenterX = anchorRect.right + 8.dp
                    val badgeCenterY = height / 2

                    anchorRect.set(
                            badgeCenterX,
                            badgeCenterY - badgeHeight / 2,
                            badgeCenterX + badgeWidth,
                            badgeCenterY + badgeHeight / 2
                    )
                }
            }
        }
    }

    override fun selectChats() {
        viewPager.setCurrentItem(0, false)
    }

    override fun selectInvites() {
        viewPager.setCurrentItem(1, false)
    }

    override fun setInvitesCount(count: Int) {
        invitesBadge.apply {
            number = count
            invalidateSelf()
        }
    }

    override fun layout() = R.layout.fragment_chat_list_tabs
}
