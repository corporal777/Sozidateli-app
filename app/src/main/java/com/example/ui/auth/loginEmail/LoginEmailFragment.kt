package com.example.ui.auth.loginEmail

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnRegister.setOnClickListener { presenter.clickRegister() }
        btnLogin.setOnClickListener { presenter.clickLogin() }
        ivClose.setOnClickListener { presenter.clickOnBack() }
        etEmail.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { presenter.changeEmailText(p0.toString())}
        })

        etPassword.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { presenter.chagnePasswordText(p0.toString())}
        })
    }


    override fun showWelcome() {
        findNavController().navigate(LoginEmailFragmentDirections.loginEmailToWelcomeAction())
    }

    override fun showRegister() {
        findNavController().navigate(LoginEmailFragmentDirections.loginEmailToRegisterAction())
    }

    override fun enableLoginBtn(isEnable: Boolean) {
        btnLogin.isEnabled = isEnable

        if(isEnable){
            btnLogin.setBackgroundResource(R.drawable.background_btn_auth)
            btnLogin.setTextColor(Color.WHITE)
        } else{
            btnLogin.setBackgroundResource(R.drawable.background_edittext_login)
            btnLogin.setTextColor(Color.GRAY)
        }
    }

    override fun layout() = R.layout.fragment_login_email
}
