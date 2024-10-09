package com.example.ui.state

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.app.databinding.FragmentUserStateBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.state.base.MainInfoFragmentArgs
import com.example.ui.state.maxNew.MaxStateScreenType
import com.example.ui.state.maxNew.contacts.MaxStatusContactsFragmentArgs
import com.example.ui.state.maxNew.education.MaxStatusEducationFragmentArgs
import com.example.ui.state.maxNew.interests.MaxStatusInterestsFragmentArgs
import com.example.ui.state.maxNew.work.MaxStatusWorkFragmentArgs
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.Utils
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.onPageSelected
import javax.inject.Inject
import javax.inject.Provider

class UserStateFragment : BaseFragment<FragmentUserStateBinding>(), UserStateContract.View,
    ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: UserStatePresenter

    @Inject
    lateinit var presenterProvider: Provider<UserStatePresenter>

    @ProvidePresenter
    fun providePresenter(): UserStatePresenter = presenterProvider.get()

    private val onPageSelected = onPageSelected { selectTab(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startPostponedEnterTransition()
        mBinding.apply {
            viewPager.run {
                selectTab(currentItem)
                registerOnPageChangeCallback(onPageSelected)
            }
            btnBase.setOnClickListener { viewPager.currentItem = 0 }
            btnMax.setOnClickListener { viewPager.currentItem = 1 }
        }
    }

    override fun setStatesUI(states: List<StateItemModel>) {
        val adapter = UserStateAdapter {
            when (it) {
                UserState.BASE -> {
                    findNavController().navigate(
                        R.id.mainInfoFragment,
                        MainInfoFragmentArgs.Builder().setType(it).setScreen(2).build().toBundle()
                    )
                }
                UserState.MAX -> {
                    val base = states.firstOrNull { st -> st.state == UserState.BASE }
                    if (base?.isDone == true)
                        when (Utils.maxStateScreen(presenter.getUserData())) {
                            MaxStateScreenType.BASE -> {
                                findNavController().navigate(
                                    R.id.maxStatusContactsFragment,
                                    MaxStatusContactsFragmentArgs.Builder().setScreen(2).build()
                                        .toBundle()
                                )
                            }
                            MaxStateScreenType.INTERESTS -> {
                                findNavController().navigate(
                                    R.id.maxStatusInterestsFragment,
                                    MaxStatusInterestsFragmentArgs.Builder().setScreen(2).build()
                                        .toBundle()
                                )
                            }
                            MaxStateScreenType.EDUCATION -> {
                                findNavController().navigate(
                                    R.id.maxStatusEducationFragment,
                                    MaxStatusEducationFragmentArgs.Builder().setScreen(2).build()
                                        .toBundle()
                                )
                            }

                            MaxStateScreenType.WORK -> {
                                findNavController().navigate(
                                    R.id.maxStatusWorkFragment,
                                    MaxStatusWorkFragmentArgs.Builder().setScreen(2).build()
                                        .toBundle()
                                )
                            }
                            MaxStateScreenType.DONE -> presenter.onClickClose()
                        }
                    else {
                        findNavController().navigate(
                            R.id.mainInfoFragment,
                            MainInfoFragmentArgs.Builder().setType(it).setScreen(2).build()
                                .toBundle()
                        )
                    }
                }
            }
        }
        adapter.submitList(states)
        mBinding.viewPager.adapter = adapter
    }

    private fun selectTab(position: Int) {
        mBinding.clTabs.apply {
            for (p in 0 until childCount) {
                getChildAt(p).isSelected = p == position
            }
        }
    }

    override fun animationType(): AnimType = AnimType.AXIS
    override fun layout(): Int = R.layout.fragment_user_state
    override val title: CharSequence by lazy { getString(R.string.states) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}