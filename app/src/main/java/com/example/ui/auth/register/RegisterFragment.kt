package com.example.ui.auth.register

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.util.SimpleTextWatcher
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivClose.setOnClickListener { presenter.onClickBack() }

        etEmail.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            presenter.onChangeEmailText(charSequence.toString())
        })

        etPassword.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            presenter.onChangePasswordText(charSequence.toString())
        })

        etName.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            presenter.onChangeNameText(charSequence.toString())
        })

        etLastName.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            presenter.onChangeLastNameText(charSequence.toString())
        })

        btnRegister.setOnClickListener {
            presenter.onClickRegister(
                    email = etEmail.text.toString(),
                    password = etPassword.text.toString(),
                    name = etName.text.toString(),
                    lastName = etLastName.text.toString()
            )
        }
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
        btnRegister.apply {
            isEnabled = isEnable
            setBackgroundResource(if (isEnable) R.drawable.background_btn_auth else R.drawable.background_disabled_btn_login)
            setTextColor(if (isEnable) Color.WHITE else ContextCompat.getColor(requireContext(), R.color.disabled_color))
        }
    }

    override fun passwordCheckColored(isHasSix: Boolean, isOneCap: Boolean, isHasSymbol: Boolean) {
        colorTextPasswordChecker(tvPasswordStrong1, isHasSix)
//        colorTextPasswordChecker(tvPasswordStrong2, isOneCap)
//        colorTextPasswordChecker(tvPasswordStrong3, isHasSymbol)
    }

    private fun colorTextPasswordChecker(textView: TextView, has: Boolean) {
        val colorRed = Color.RED
        val colorGreen = ContextCompat.getColor(requireContext(), R.color.auth_accept_green)

        textView.setTextColor(if (has) colorGreen else colorRed)
    }

    override fun goToLoginWithEmailConfirmation(email: String, password: String) {
        findNavController().navigate(RegisterFragmentDirections.emailRegisterToEmailLogin(email, password, true))
    }

    override fun layout() = R.layout.fragment_register
}
