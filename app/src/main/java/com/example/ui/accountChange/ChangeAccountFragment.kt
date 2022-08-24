package com.example.ui.accountChange

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.databinding.FragmentChangeAccountBinding
import com.example.extensions.dp
import com.example.extensions.findGroupBy
import com.example.extensions.forEachGroups
import com.example.holders.CalendarHorizontalListItem
import com.example.ui.accountChange.items.*
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.CustomProgressView
import com.example.ui.accountChange.items.ChangeAccountBottomDialog
import com.example.ui.views.toolbar.SimpleTitleToolbar
import com.example.util.getDeviceId
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import onScrolled
import javax.inject.Inject
import javax.inject.Provider

class ChangeAccountFragment : BaseFragmentNew<FragmentChangeAccountBinding>(),
    ChangeAccountContract.View, SimpleTitleToolbar {

    @InjectPresenter
    lateinit var presenter: ChangeAccountPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangeAccountPresenter>

    @ProvidePresenter
    fun providePresenter(): ChangeAccountPresenter = presenterProvider.get().apply {
        mDeviceId = getDeviceId(requireContext())
    }

    private val logoSection by lazy {
        Section().apply {
            update(listOf(LogoItem()))
        }
    }
    private val accountsSection = Section()
    private val unLoggedAccountsSection by lazy {
        Section().apply {
            setHeader(UnLoggedAccountsHeader())
            setHideWhenEmpty(true)
        }
    }
    private val loginButtonSection = Section()

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(logoSection)
            add(accountsSection)
            add(unLoggedAccountsSection)
            add(loginButtonSection)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle("")
        mBinding.accountsList.apply {
            startPostponedEnterTransition()
            adapter = groupAdapter
            onScrolled { _, dy ->
                presenter.changeAppBarElevation(dy)
            }
        }
    }

    override fun setAccounts(canShow: Boolean, sessions: List<UserSessionModel>) {
        accountsSection.update(
            sessions.map { x ->
                AccountItem(canShow, x, presenter.getUserId(), {
                    showAccountActionDialog(x)
                }, {
                    presenter.switchAccount(it)
                })
            }
        )

    }

    override fun setUnLoggedAccounts(canShow: Boolean, sessions: List<UserSessionModel>) {
        unLoggedAccountsSection.update(
            sessions.map { m ->
                AccountItem(canShow, m, presenter.getUserId(), {
                    showAccountActionDialog(it)
                }, {
                    presenter.loginToAccountClick(it.binds.user)
                })
            }
        )
    }

    override fun setLoginToAnotherAccountButton() {
        loginButtonSection.update(listOf(LoginButtonItem {
            presenter.authToAccountClick()
        }))
    }

    override fun showMessage(message: String) {
        showToast(message)
    }

    override fun showAuthorizationFragment() {
        findNavController().navigate(ChangeAccountFragmentDirections.changeAccountFragmentToAuthFragment())
    }

    override fun showLoginFragment(login: String) {
        findNavController().navigate(
            ChangeAccountFragmentDirections.changeAccountFragmentToLoginFragment(login)
        )
    }

    private fun showAccountActionDialog(session: UserSessionModel) {
        val dialog = ChangeAccountBottomDialog(session)
        dialog.show(requireActivity().supportFragmentManager, "dialog")
        dialog.setLogoutCallback { s ->
            presenter.logoutFromAccount(s)
        }
        dialog.setLogoutAndKillCallback { s ->
            presenter.logoutFromAccountAndKill(s)
        }
        dialog.setKillCallback { s ->
            presenter.killSession(s)
        }
    }

    override fun showProgressLoading() {
        val progressBar = CustomProgressView(requireContext())
        progressBar.setSize(35.dp)
        progressBar.setProgressColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.main_brown_color_new
            )
        )
        mBinding.progressContainer.apply {
            isVisible = true
            addView(progressBar, 0)
        }
    }

    override fun hideProgressLoading() {
        mBinding.progressContainer.apply {
            isVisible = false
            removeAllViews()
        }
    }

    override fun onStop() {
        super.onStop()
        hideProgressLoading()
    }

    override fun layout(): Int = R.layout.fragment_change_account

}