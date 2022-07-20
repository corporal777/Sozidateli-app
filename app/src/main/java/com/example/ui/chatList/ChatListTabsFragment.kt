package com.example.ui.chatList

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.ui.chatList.contacts.ChatListFragment
import com.example.ui.chatList.invites.InviteListFragment
import kotlinx.android.synthetic.main.fragment_chat_list_tabs.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class ChatListTabsFragment : BaseFragment(), ChatListTabsContract.View {


    @InjectPresenter
    lateinit var presenter: ChatListTabsPresenter

    private var isFinishAnimation = true

    @Inject
    lateinit var presenterProvider: Provider<ChatListTabsPresenter>

    private var onScrollStateChangeListener = object : ChatListFragment.OnChatListScrollingState{
        override fun onScrollUp(value: Int) {
            showView(clTabs)
            Log.e("VALUE", value.toString())
        }

        override fun onScrollDown(value: Int) {
            hideView(clTabs)
        }

        override fun onScrollOffsetValue(value: Int) {
            val mElevation = abs(value / 10f)
            app_bar_layout.apply {
                elevation = if (mElevation <= 10f) {
                    mElevation
                } else {
                    10f
                }
            }
        }
    }

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
                ChatListFragment(onScrollStateChangeListener),
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
            //setOnTouchListener(TranslateAnimationUtil(requireContext(), clTabs))
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


    private fun hideView(animationView: View) {
        if (animationView == null || animationView.getVisibility() == View.GONE) {
            return
        }
        val animationDown =
            AnimationUtils.loadAnimation(animationView.getContext(), R.anim.move_up)
        animationDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                animationView.setVisibility(View.VISIBLE)
                isFinishAnimation = false
            }

            override fun onAnimationEnd(animation: Animation) {
                animationView.setVisibility(View.GONE)
                isFinishAnimation = true
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        if (isFinishAnimation) {
            animationView.startAnimation(animationDown)
        }
    }

    private fun showView(animationView : View) {
        if (animationView == null || animationView.getVisibility() == View.VISIBLE) {
            return
        }
        val animationUp =
            AnimationUtils.loadAnimation(animationView.getContext(), R.anim.move_down)
        animationUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                animationView.setVisibility(View.VISIBLE)
                isFinishAnimation = false
            }

            override fun onAnimationEnd(animation: Animation) {
                isFinishAnimation = true
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        if (isFinishAnimation) {
            animationView.startAnimation(animationUp)
        }
    }
}
