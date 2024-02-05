package com.example.ui.auth.authorization

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.R
import com.example.data.models.SnUser
import com.example.databinding.FragmentAuthorizationBinding
import com.example.holders.StoriesItem
import com.example.interfaces.BackgroundImageFragment
import com.example.ui.auth.confirm.email.ConfirmEmailCodeFragmentArgs
import com.example.ui.auth.confirm.phone.ConfirmPhoneCodeFragmentArgs
import com.example.ui.auth.snAuth.SnAuthFragmentArgs
import com.example.ui.base.BaseFragment
import com.example.util.Utils
import com.example.util.setCurrentItemWithDuration
import com.google.android.material.tabs.TabLayoutMediator
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import onPageStateChanged
import javax.inject.Inject
import javax.inject.Provider

class AuthorizationFragment : BaseFragment<FragmentAuthorizationBinding>(),
    AuthorizationContract.View, BackgroundImageFragment {

    @InjectPresenter
    lateinit var presenter: AuthorizationPresenter

    @Inject
    lateinit var presenterProvider: Provider<AuthorizationPresenter>

    @ProvidePresenter
    fun providePresenter(): AuthorizationPresenter = presenterProvider.get()

    private val groupAdapter by lazy { GroupAdapter<GroupieViewHolder>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            storiesPager.apply {
                offscreenPageLimit = 7
                adapter = groupAdapter
            }
            btnRegister.setOnClickListener { presenter.onRegisterClick() }
            btnLogin.setOnClickListener { presenter.onLoginClick() }
            btnLoginVk.setOnClickListener { presenter.onAuthVkClick(requireContext()) }
            btnLoginGos.setOnClickListener {}
        }
    }

    override fun setStories(stories: List<String>) {
        groupAdapter.update(stories.mapIndexed { index, text -> StoriesItem(index, text) })
    }

    override fun setPagerScroll(size: Int) {
        mBinding.apply {
            tabDots.apply { TabLayoutMediator(this, storiesPager) { tab, position ->
                if (position == 0 || position == size - 1) tab.view.isVisible = false
            }.attach() }
            storiesPager.setCurrentItem(1, false)
            storiesPager.onPageStateChanged { state ->
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    when (storiesPager.currentItem) {
                        size - 1 -> storiesPager.setCurrentItem(1, false)
                        0 -> storiesPager.setCurrentItem(size - 2, false)
                    }
                }
            }
        }
    }

    override fun showNextStory() {
        mBinding.storiesPager.apply {
            val position = currentItem
            setCurrentItem(position + 1, true)
//            if (position < groupAdapter.itemCount - 1)
//                setCurrentItem(position + 1, true)
//            else setCurrentItem(0, false)
        }
    }


    override fun showLogin() {
        findNavController().navigate(R.id.login_fragment)
    }

    override fun showRegistration() {
        findNavController().navigate(R.id.userRegistrationFragment)
    }

    override fun showSnAuthorization(snAuth: SnUser) {
        val args = SnAuthFragmentArgs.Builder(snAuth).build().toBundle()
        findNavController().navigate(R.id.snAuthFragment, args)
    }

    override fun showCustomLoading(type: Int) = mBinding.run {
        if (type == 1) btnLoginVk.showProgressLoading(true)
        else btnLoginGos.showProgressLoading(true)
    }

    override fun hideCustomLoading(type: Int) = mBinding.run {
        if (type == 1) btnLoginVk.showProgressLoading(false)
        else btnLoginGos.showProgressLoading(false)
    }

    override fun hideAllLoadings() {
        mBinding.apply {
            btnLoginVk.showProgressLoading(false)
            btnLoginGos.showProgressLoading(false)
        }
    }

    override fun getFragmentBackgroundDrawable(): Drawable? = null
    override val isLightStatus = false
    override fun layout() = R.layout.fragment_authorization
}
