package com.example.ui.auth.register

import android.os.Bundle
import android.view.View
import android.view.WindowManager
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
        btnRegister.apply { isEnabled = isEnable }
    }

    override fun passwordCheckColored(isHasSix: Boolean, isOneCap: Boolean, isHasSymbol: Boolean) {
        tvPasswordHintLength.apply {
            if (isHasSix) highlightCorrect()
            else highlightError()
        }
    }

    override fun goToLoginWithEmailConfirmation(email: String, password: String) {
        findNavController().navigate(RegisterFragmentDirections.emailRegisterToEmailLogin(email, password, true, false))
    }

    override fun layout() = R.layout.fragment_register
}
