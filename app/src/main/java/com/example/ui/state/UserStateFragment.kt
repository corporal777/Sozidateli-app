package com.example.ui.state

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentUserStateBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.state.base.MainInfoFragmentArgs
import com.example.ui.state.maxNew.MaxStateScreenType
import com.example.ui.state.maxNew.education.MaxStatusEducationFragmentArgs
import com.example.ui.state.maxNew.interests.MaxStatusInterestsFragmentArgs
import com.example.ui.state.maxNew.mainInfo.MaxStatusContactsFragmentArgs
import com.example.ui.state.maxNew.work.MaxStatusWorkFragmentArgs
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.Utils
import com.google.android.material.tabs.TabLayoutMediator
import onBackPressedCallback
import javax.inject.Inject
import javax.inject.Provider

class UserStateFragment : BaseFragment<FragmentUserStateBinding>(true), UserStateContract.View,
    ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: UserStatePresenter

    @Inject
    lateinit var presenterProvider: Provider<UserStatePresenter>

    @ProvidePresenter
    fun providePresenter(): UserStatePresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startPostponedEnterTransition()
        onBackPressedCallback(true) {
            presenter.onClickClose()
        }
    }

    override fun setStatesUI(states: List<StateItemModel>) {
        val tabsList = listOf(getString(R.string.base), getString(R.string.max))
        val adapter = UserStateAdapter {
            when (it) {
                UserState.BASE ->{
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
                            MainInfoFragmentArgs.Builder().setType(it).setScreen(2).build().toBundle()
                        )
                    }
                }
            }
        }
        adapter.submitList(states)
        mBinding.apply {
            viewPager.adapter = adapter
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = tabsList[position]
            }.attach()
            for (tabIndex in 0 until tabs.tabCount) {
                val tabTextView =
                    ((tabs.getChildAt(0) as LinearLayout).getChildAt(tabIndex) as LinearLayout).getChildAt(
                        1
                    ) as TextView
                tabTextView.isAllCaps = false
            }
        }
    }

    override fun layout(): Int = R.layout.fragment_user_state
    override val title: CharSequence by lazy { getString(R.string.states) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}