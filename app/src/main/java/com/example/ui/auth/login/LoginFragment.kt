package com.example.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.ui.snAuth.SnAuthManager
import com.example.ui.snAuth.SnType
import com.example.util.AuthValidateUtil
import com.example.util.SimpleTextWatcher
import com.vk.sdk.VKScope
import kotlinx.android.synthetic.main.dialog_email_set_social_network.view.*
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject
import javax.inject.Provider

class LoginFragment : BaseFragment(), LoginContract.View {

    @InjectPresenter
    lateinit var presenter: LoginPresenter

    @Inject
    lateinit var presenterProvider: Provider<LoginPresenter>

    @ProvidePresenter
    fun providePresenter(): LoginPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flFbAuth.setOnClickListener { presenter.onClickFb() }
        flVkAuth.setOnClickListener { presenter.onClickVk() }
        flOkAuth.setOnClickListener { presenter.onClickOk() }
        flEmailAuth.setOnClickListener { presenter.onClickEmail() }
    }

    override fun startVkAuthorization() {
        SnAuthManager.startAuthVk(requireContext(), arrayOf(VKScope.EMAIL))
    }

    override fun startFbAuthorization() {
        SnAuthManager.startAuthFacebook(requireContext())
    }

    override fun startOkAuthorization() {
        SnAuthManager.startAuthOk(requireContext())
    }

    override fun showSocialNetworkSetEmail(snType: SnType, email: String?,token:String) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_email_set_social_network, null, false)
        val alert = AlertDialog.Builder(context!!)
                .setTitle(R.string.auth_sn_set_email_title)
                .setView(view)
                .setCancelable(false)
                .create()

        view.btnCancel.setOnClickListener { alert.dismiss() }

        view.btnSave.setOnClickListener {
            presenter.onClickSetSocialNetworkEmail(snType, view.etEmail.text.toString(),token)
            alert.dismiss()
        }

        view.etEmail.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            view.btnSave.isEnabled = AuthValidateUtil.isValidEmail(charSequence)
        })

        view.etEmail.setText(email)

        alert.show()
    }

    override fun showNeedConfirmEmailDialog(email: String?) {
        showDialog(getString(R.string.auth_register_confirm_email_message).format(email ?: ""))
    }

    override fun showLogin() {
        findNavController().navigate(LoginFragmentDirections.loginToLoginEmailAction(null, null, false, false))
    }

    override fun layout() = R.layout.fragment_login
}
