package com.example.ui.event.list.recommendations

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.MyEventsFilter
import com.example.data.models.OrganizationsFilter
import com.example.holders.RecommendationsHeaderItem
import com.example.holders.ScreenLabelItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.event.list.EventListFragment
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.chatView.ChatView
import com.example.ui.views.toolbar.ToolbarContentActionBar
import javax.inject.Inject
import javax.inject.Provider

class RecommendationsFragment : EventListFragment<RecommendationsPresenter>(), RecommendationsContract.View, ToolbarFragment {

    override val title: String? = null

    @InjectPresenter(type = PresenterType.WEAK, tag = "RecommendationsPresenter")
    override lateinit var presenter: RecommendationsPresenter

    @Inject
    lateinit var presenterProvider: Provider<RecommendationsPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = "RecommendationsPresenter")
    fun providePresenter(): RecommendationsPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headGroup.update(listOf(
                ScreenLabelItem(getString(R.string.tab_recommended_title)),
                RecommendationsHeaderItem(
                        { presenter.onSearchClick() },
                        { presenter.onOrganizationsClick() },
                        { presenter.onMyEventsClick() }
                )
        ))
    }

    override fun showSearch() {
        findNavController().navigate(RecommendationsFragmentDirections.recommendationsFragmentToSearchFragment(null))
    }

    override fun showOrganizations() {
        findNavController().navigate(RecommendationsFragmentDirections.recommendationsFragmentToFavoriteOrganizationsFragment(OrganizationsFilter.NONE))
    }

    override fun showMyEvents() {
        findNavController().navigate(RecommendationsFragmentDirections.recommendationsFragmentToMyEventsFragment(MyEventsFilter.NONE))
    }

    override fun showChat() {
        findNavController().navigate(RecommendationsFragmentDirections.recommendationsFragmentToChatListTabsFragment())
    }

    override fun showAccount() {
        findNavController().navigate(RecommendationsFragmentDirections.recommendationsFragmentToProfileFragment())
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        toolbarContentActionBar.apply {
            addRightView(AccountView(requireContext()).also { it.setOnClickListener { presenter.onMenuAccountClick() } })
            addRightView(ChatView(requireContext()).also { it.setOnClickListener { presenter.onMenuChatClick() } })
        }
    }
}