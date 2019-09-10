package com.example.ui.auth.register

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import com.example.util.ClickableSpan
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
//
//        ivClose.setOnClickListener { presenter.onClickBack() }
//
//        etEmail.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
//            presenter.onChangeEmailText(charSequence.toString())
//        })
//
//        etPassword.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
//            presenter.onChangePasswordText(charSequence.toString())
//        })
//
//        etName.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
//            presenter.onChangeNameText(charSequence.toString())
//        })
//
//        etLastName.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
//            presenter.onChangeLastNameText(charSequence.toString())
//        })
//
//        btnRegister.setOnClickListener {
//            presenter.onClickRegister(
//                    email = etEmail.text.toString(),
//                    password = etPassword.text.toString(),
//                    name = etName.text.toString(),
//                    lastName = etLastName.text.toString()
//            )
//        }

        val agreementText = SpannableString(getString(R.string.auth_agree_user_agreement)).apply {
            val linkStart = 11
            val linkEnd = length
            setSpan(ClickableSpan { showToast("ADS") }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }

        tvAgree.apply {
            text = agreementText
            movementMethod = LinkMovementMethod.getInstance()
        }

        flAgree.setOnClickListener {
            cbAgree.apply {
                isChecked = !isChecked
            }
        }
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
//        btnRegister.apply { isEnabled = isEnable }
    }

    override fun passwordCheckColored(isHasSix: Boolean, isOneCap: Boolean, isHasSymbol: Boolean) {
//        tvPasswordHintLength.apply {
//            if (isHasSix) highlightCorrect()
//            else highlightError()
//        }
    }

    override fun goToLoginWithEmailConfirmation(email: String, password: String) {
        findNavController().navigate(RegisterFragmentDirections.emailRegisterToEmailLogin(email, password, true, false))
    }

    override fun layout() = R.layout.fragment_register
}
