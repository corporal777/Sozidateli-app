package com.example.ui.auth.register

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_register.*
import javax.inject.Inject
import javax.inject.Provider

class RegisterFragment : BaseFragment(), RegisterContract.View {

    @InjectPresenter
    lateinit var presenter: RegisterPresenter

    @Inject
    lateinit var presenterProvider: Provider<RegisterPresenter>

    @ProvidePresenter
    fun providePresenter(): RegisterPresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivClose.setOnClickListener { presenter.clickOnBack() }

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { presenter.changeEmailText(p0.toString())}
        })

        etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { presenter.chagnePasswordText(p0.toString())}
        })

        etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { presenter.changeNameText(p0.toString())}
        })
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
        btnRegister.isEnabled = isEnable

        if (isEnable) {
            btnRegister.setBackgroundResource(R.drawable.background_btn_auth)
            btnRegister.setTextColor(Color.WHITE)
        } else {
            btnRegister.setBackgroundResource(R.drawable.background_edittext_login)
            btnRegister.setTextColor(Color.GRAY)
        }
    }

    override fun passwordCheckColored(isHasSix: Boolean, isOneCap: Boolean, isHasSymbol: Boolean) {
        colorTextPasswordChecker(tvPasswordStrong1,isHasSix)
        colorTextPasswordChecker(tvPasswordStrong2,isOneCap)
        colorTextPasswordChecker(tvPasswordStrong3,isHasSymbol)
    }

    private fun colorTextPasswordChecker(textView: TextView, has: Boolean) {
        val colorRed = Color.RED
        val colorGreen = ContextCompat.getColor(context!!,R.color.auth_accept_green)

        textView.setTextColor(if (has) colorGreen else colorRed)
    }

    override fun showWelcome() {
        findNavController().navigate(RegisterFragmentDirections.registerToWelcomeAction())
    }

    override fun layout() = R.layout.fragment_register
}
