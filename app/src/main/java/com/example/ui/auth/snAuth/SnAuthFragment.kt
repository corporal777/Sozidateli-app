package com.example.ui.auth.snAuth

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.data.models.SnAuth
import com.example.data.models.SnUser
import com.example.app.databinding.FragmentRegistrationUserBinding
import com.example.app.databinding.FragmentSnAuthBinding
import com.example.interfaces.ToolbarFragment
import com.example.ui.auth.confirm.email.ConfirmEmailCodeFragmentArgs
import com.example.ui.auth.confirm.phone.ConfirmPhoneCodeFragmentArgs
import com.example.ui.auth.login.LoginFragmentArgs
import com.example.ui.auth.register.UserRegistrationContract
import com.example.ui.auth.register.UserRegistrationPresenter
import com.example.ui.auth.register.sn.SnRegisterFragment
import com.example.ui.auth.register.sn.SnRegisterFragmentArgs
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.Utils
import com.example.util.setTint
import com.example.util.showCustomTabsBrowser
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SnAuthFragment : BaseFragment<FragmentSnAuthBinding>(),
    SnAuthContract.View, ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: SnAuthPresenter

    @Inject
    lateinit var presenterProvider: Provider<SnAuthPresenter>

    @ProvidePresenter
    fun providePresenter(): SnAuthPresenter = presenterProvider.get().apply {
        snUser = SnAuthFragmentArgs.fromBundle(requireArguments()).snUser
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnLogin.setOnClickListener { presenter.onClickLogin() }
            btnCreate.setOnClickListener { presenter.onClickRegister() }
        }
    }

    override fun showLogin(snAuth: SnAuth) {
        val args = LoginFragmentArgs.Builder().setSnAuth(snAuth).build().toBundle()
        findNavController().navigate(R.id.login_fragment, args)
    }

    override fun showSnRegistration(snUser: SnUser) {
        val args = SnRegisterFragmentArgs.Builder(snUser).build().toBundle()
        findNavController().navigate(R.id.snRegisterFragment, args)
    }

    override fun layout(): Int = R.layout.fragment_sn_auth
    override val title: CharSequence by lazy { "" }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        toolbarContent.getBackButton().setTint(R.color.main_brown_color_new)
    }

}