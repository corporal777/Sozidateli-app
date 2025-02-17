package com.example.ui.accountChange

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.app.databinding.FragmentChangeAccountBinding
import com.example.data.models.UserSessionModel
import com.example.extensions.onBackPressedCallback
import com.example.extensions.updateItem
import com.example.holders.PlaceholderItem
import com.example.ui.accountChange.items.AccountItem
import com.example.ui.accountChange.items.ChangeAccountBottomDialog
import com.example.ui.accountChange.items.LoginButtonItem
import com.example.ui.accountChange.items.LogoItem
import com.example.ui.accountChange.items.UnLoggedAccountsHeader
import com.example.ui.auth.login.LoginFragmentArgs
import com.example.ui.base.BaseToolbarFragment
import com.example.util.showCustomTabsBrowser
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class ChangeAccountFragment : BaseToolbarFragment<FragmentChangeAccountBinding>(), ChangeAccountContract.View {

    @InjectPresenter
    lateinit var presenter: ChangeAccountPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangeAccountPresenter>

    @ProvidePresenter
    fun providePresenter(): ChangeAccountPresenter = presenterProvider.get().apply {
        val args = ChangeAccountFragmentArgs.fromBundle(requireArguments())
        this.redirectLink = args.deepLink
    }

    private val logoSection by lazy { Section().apply { updateItem(LogoItem()) } }
    private val accountsSection by lazy { Section() }
    private val unLoggedAccountsSection by lazy {
        Section().apply {
            setHeader(UnLoggedAccountsHeader())
            setHideWhenEmpty(true)
        }
    }
    private val loginButtonSection by lazy { Section() }

    private val groupAdapter by lazy {
        GroupieAdapter().apply {
            add(logoSection)
            add(accountsSection)
            add(unLoggedAccountsSection)
            add(loginButtonSection)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressedCallback(true) {
            presenter.onClickClose()
        }
        mBinding.accountsList.apply {
            adapter = groupAdapter
            doOnPreDraw { startPostponedEnterTransition() }
        }
    }

    override fun setAccounts(canShow: Boolean, sessions: List<UserSessionModel?>) {
        accountsSection.update(
            sessions.map { x ->
                if (x == null) PlaceholderItem(PlaceholderItem.Type.ACCOUNTS)
                else {
                    AccountItem(canShow, x, presenter.getUserId(),
                        { showAccountActionDialog(x) },
                        { presenter.switchAccount(it) }
                    )
                }
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
        loginButtonSection.updateItem(
            LoginButtonItem { presenter.authToAccountClick() }
        )
    }

    override fun showAuthorizationFragment() {
        findNavController().navigate(R.id.authorization_fragment)
    }

    override fun showLoginFragment(login: String) {
        val args = LoginFragmentArgs.Builder().setEmail(login).build().toBundle()
        findNavController().navigate(R.id.login_fragment, args)
    }

    private fun showAccountActionDialog(session: UserSessionModel) {
        ChangeAccountBottomDialog(requireContext(), session).apply {
            setLogoutCallback { s -> presenter.logoutFromAccount(s) }
            setLogoutAndKillCallback { s -> presenter.logoutFromAccountAndKill(s) }
            setKillCallback { s -> presenter.killSession(s) }
        }.show()
    }

    override fun showBrowser(url: String) = showCustomTabsBrowser(requireContext(), url)


    override fun scrollingView(): View = mBinding.accountsList
    override fun animationType(): AnimType = AnimType.AXIS
    override fun layout(): Int = R.layout.fragment_change_account
    override fun binding() = FragmentChangeAccountBinding::class.java

}