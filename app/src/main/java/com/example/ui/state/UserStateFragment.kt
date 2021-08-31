package com.example.ui.state

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.state.max.MaxStateScreenType
import com.example.util.Utils
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_user_state.*
import javax.inject.Inject
import javax.inject.Provider

class UserStateFragment: BaseFragment(), UserStateContract.View, ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: UserStatePresenter

    @Inject
    lateinit var presenterProvider: Provider<UserStatePresenter>

    @ProvidePresenter
    fun providePresenter(): UserStatePresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                presenter.onClickClose()
            }
        })
        //ivClose.setOnClickListener { presenter.onClickClose() }
    }

    override fun setStatesUI(states: List<StateItemModel>) {
        val tabsList = listOf(getString(R.string.base), getString(R.string.max))
        val adapter = UserStateAdapter {
            when (it) {
                UserState.BASE ->
                    findNavController().navigate(UserStateFragmentDirections.actionUserStateFragmentToMainInfoFragment().setType(it).setScreen(2))
                UserState.MAX -> {
                    val base = states.firstOrNull { st -> st.state == UserState.BASE }
                    if (base?.isDone == true)
                        when (Utils.maxStateScreen(presenter.getUserData())) {
                            MaxStateScreenType.BASE ->
                                findNavController().navigate(UserStateFragmentDirections.actionUserStateFragmentToMaxStateMainInfoFragment().setScreen(2))
                            MaxStateScreenType.INTERESTS ->
                                findNavController().navigate(UserStateFragmentDirections.actionUserStateFragmentToBaseStateInterestsFragment().setScreen(2))
                            MaxStateScreenType.WORK ->
                                findNavController().navigate(UserStateFragmentDirections.actionUserStateFragmentToMaxStateWorkFragment().setScreen(2))
                            MaxStateScreenType.EDUCATION ->
                                findNavController().navigate(UserStateFragmentDirections.actionUserStateFragmentToMaxStateEducationFragment().setScreen(2))
                        }
                    else findNavController().navigate(UserStateFragmentDirections.actionUserStateFragmentToMainInfoFragment().setType(it).setScreen(2))
                }
            }
        }
        adapter.submitList(states)
        /*viewPager.clipToPadding = false
        viewPager.setPadding(50, 0, 50, 0)
        viewPager.setPageTransformer(MarginPageTransformer(20))
        viewPager.offscreenPageLimit = 3
        viewPager.clipChildren = false*/
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

    override fun layout(): Int = R.layout.fragment_user_state

    override val title: CharSequence?
        get() = getString(R.string.states)

}