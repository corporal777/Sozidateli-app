package com.example.ui.status.tabs

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentPagerAdapter
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.user.User
import com.example.extensions.dp
import com.example.ui.base.BaseFragment
import com.example.ui.status.StatusFragment
import kotlinx.android.synthetic.main.fragment_request.*
import kotlinx.android.synthetic.main.fragment_status_pages.*
import javax.inject.Inject
import javax.inject.Provider

class StatusPagesFragment : BaseFragment(), StatusTabsContract.View {

    @InjectPresenter
    lateinit var presenter: StatusTabsPresenter

    @Inject
    lateinit var presenterProvider: Provider<StatusTabsPresenter>

    @ProvidePresenter
    fun providePresenter(): StatusTabsPresenter = presenterProvider.get()

    private val pageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            when (position) {
                0 -> presenter.onAnonymousSelected()
                1 -> presenter.onProtectedSelected()
                2 -> presenter.onMaximumSelected()
            }
            setPageTitle(position)
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            val leftAlpha = 1f - positionOffset
            when (position) {
                0 -> {
                    pages[0].setContentAlpha(leftAlpha)
                    pages[1].setContentAlpha(positionOffset)
                }
                1 -> {
                    pages[1].setContentAlpha(leftAlpha)
                    pages[2].setContentAlpha(positionOffset)
                }
                2 -> {
                    pages[2].setContentAlpha(leftAlpha)
                }
            }
        }
    }

    private val pageStatusScrollListener: (Int, Int) -> Unit = { position, scrollY: Int ->
        when (position) {
            0 -> {
                pages[1].scrollStatusTo(scrollY)
                pages[2].scrollStatusTo(scrollY)
            }
            1 -> {
                pages[0].scrollStatusTo(scrollY)
                pages[2].scrollStatusTo(scrollY)
            }
            2 -> {
                pages[0].scrollStatusTo(scrollY)
                pages[1].scrollStatusTo(scrollY)
            }
        }

        setPageTitle(position)
    }

    private lateinit var pages: List<StatusFragment>
    private var titleBottomLocation = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pages = listOf(
                StatusFragment.initWithStatus(User.Status.LOW_PROTECTION).apply {
                    statusScrollListener = createStatusScrollListener(0)
                },
                StatusFragment.initWithStatus(User.Status.MID_PROTECTION).apply {
                    statusScrollListener = createStatusScrollListener(1)
                },
                StatusFragment.initWithStatus(User.Status.MAX_PROTECTION).apply {
                    statusScrollListener = createStatusScrollListener(2)
                }
        )

        viewPager.apply {
            offscreenPageLimit = pages.size
            pageMargin = 8.dp
            adapter = object : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                override fun getItem(position: Int) = pages[position]
                override fun getCount() = pages.size
            }
            addOnPageChangeListener(pageChangeListener)
        }

        clTitle.doOnNextLayout {
            val location = IntArray(2).apply { it.getLocationOnScreen(this) }
            titleBottomLocation = location[1] + it.height
        }

        ibClose.setOnClickListener { presenter.onCloseClick() }
    }

    override fun selectTab(position: Int) {
        viewPager.setCurrentItem(position, false)
    }

    private fun createStatusScrollListener(position: Int): (Int) -> Unit {
        return { scrollY: Int ->
            pageStatusScrollListener.invoke(position, scrollY)
        }
    }

    private fun setPageTitle(position: Int) {
        val statusViewLocation = pages[position].getStatusViewLocation()
        val visible = titleBottomLocation > statusViewLocation

        tvTitle.text = if (visible) getString(when (position) {
            1 -> R.string.profile_status_mid
            2 -> R.string.profile_status_max
            else -> R.string.profile_status_low
        }) else null

        if (visible != tvTitle.isVisible) {
            TransitionManager.beginDelayedTransition(clTitle, Fade())
            tvTitle.isVisible = visible
        }
    }

    override fun layout() = R.layout.fragment_status_pages
}
