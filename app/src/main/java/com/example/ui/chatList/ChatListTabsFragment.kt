package com.example.ui.chatList

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentChatListBinding
import com.example.databinding.FragmentChatListTabsBinding
import com.example.ui.base.BaseFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.chatList.contacts.ChatListFragment
import com.example.ui.chatList.invites.InviteListFragment
import com.example.ui.event.list.recommendations.RecommendationsFragment
import com.example.util.smoothScrollToFirstItem
import com.google.android.material.appbar.AppBarLayout
import offsetChangedListener
import onPageChanged
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class ChatListTabsFragment : BaseFragmentNew<FragmentChatListTabsBinding>(),
    ChatListTabsContract.View {

    @InjectPresenter
    lateinit var presenter: ChatListTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChatListTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): ChatListTabsPresenter = presenterProvider.get()

    private val pageChangeListener = onPageChanged { position ->
        when (position) {
            0 -> presenter.onChatsSelected()
            1 -> presenter.onInvitesSelected()
        }
        selectTab(position)
    }

    private val fragments by lazy { listOf(ChatListFragment(), InviteListFragment()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            viewPager.apply {
                adapter = object :
                    FragmentPagerAdapter(
                        childFragmentManager,
                        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
                    ) {
                    override fun getItem(position: Int) = fragments[position]
                    override fun getCount() = fragments.size
                }
                addOnPageChangeListener(pageChangeListener)
            }

            btnTabChats.setOnClickListener { viewPager.currentItem = 0 }
            btnTabRequests.setOnClickListener { viewPager.currentItem = 1 }
            fabNewChat.setOnClickListener { presenter.onFabAddChatClick() }
            appBarLayout.offsetChangedListener { appBarLayout, i ->
                updateViews(abs(i / appBarLayout.totalScrollRange.toFloat()))
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

    fun smoothScrollToFirstItem() {
        if (mBinding.viewPager.currentItem == 0) {
            (fragments[0] as ChatListFragment).smoothScrollToFirstItem(mBinding.appBarLayout)
        } else {
            (fragments[1] as InviteListFragment).smoothScrollToFirstItem(mBinding.appBarLayout)
        }
    }

    override fun openSearch() {
        findNavController().navigate(R.id.chat_search_fragment)
    }

    private var cashCollapseState: Pair<Int, Int>? = null
    private fun updateViews(offset: Float) {
        when {
            offset < SWITCH_BOUND -> Pair(TO_EXPANDED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
            else -> Pair(TO_COLLAPSED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
        }.apply {
            when {
                cashCollapseState != null && cashCollapseState != this -> {
                    when (first) {
                        TO_EXPANDED -> {
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
                        TO_COLLAPSED -> {
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
                    }
                    cashCollapseState = Pair(first, SWITCHED)
                }
                else -> {
                    cashCollapseState = Pair(first, WAIT_FOR_SWITCH)
                }
            }
        }
    }

    companion object {
        const val SWITCH_BOUND = 0.3f
        const val TO_EXPANDED = 0
        const val TO_COLLAPSED = 1
        const val WAIT_FOR_SWITCH = 0
        const val SWITCHED = 1
    }


    override fun layout() = R.layout.fragment_chat_list_tabs
}
