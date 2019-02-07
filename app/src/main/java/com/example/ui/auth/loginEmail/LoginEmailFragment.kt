package com.example.ui.auth.loginEmail

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.util.SimpleTextWatcher
import kotlinx.android.synthetic.main.fragment_login_email.*
import javax.inject.Inject
import javax.inject.Provider

class LoginEmailFragment : BaseFragment(), LoginEmailContract.View {

    @InjectPresenter
    lateinit var presenter: LoginEmailPresenter

    @Inject
    lateinit var presenterProvider: Provider<LoginEmailPresenter>

    @ProvidePresenter
    fun providePresenter(): LoginEmailPresenter = presenterProvider.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnRegister.setOnClickListener { presenter.onClickRegister() }
        btnLogin.setOnClickListener { presenter.onClickLogin(etEmail.text.toString(), etPassword.text.toString()) }
        ivClose.setOnClickListener { presenter.onClickBack() }

        etEmail.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            presenter.onChangeEmailText(charSequence.toString())
        })

        etPassword.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            presenter.onChangePasswordText(charSequence.toString())
        })
    }


    override fun showWelcome() {
        findNavController().navigate(LoginEmailFragmentDirections.loginEmailToWelcomeAction())
    }

    override fun showRegister() {
        findNavController().navigate(LoginEmailFragmentDirections.loginEmailToRegisterAction())
    }

    override fun enableLoginBtn(isEnable: Boolean) {
        btnLogin.apply {
            isEnabled = isEnable

            val background: Int
            val textColor: Int
            if (isEnable) {
                background = R.drawable.background_btn_auth
                textColor = Color.WHITE
            } else {
                background = R.drawable.background_disabled_btn_login
                textColor = ContextCompat.getColor(context, R.color.disabled_color)
            }

            setBackgroundResource(background)
            setTextColor(textColor)
        }
    }

    override fun layout() = R.layout.fragment_login_email
}
