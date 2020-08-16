package com.example.ui.auth.register.email

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.core.text.clearSpans
import androidx.core.text.toSpannable
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.SnUser
import com.example.ui.base.BaseFragment
import com.example.util.ClickableSpan
import kotlinx.android.synthetic.main.fragment_register_email.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class RegisterEmailFragment : BaseFragment(), RegisterEmailContract.View {

    @InjectPresenter
    lateinit var presenter: RegisterEmailPresenter

    @Inject
    lateinit var presenterProvider: Provider<RegisterEmailPresenter>

    @ProvidePresenter
    fun providePresenter(): RegisterEmailPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivClose.setOnClickListener { presenter.onClickClose() }

        etFirstName.onTextChanged { it?.toString()?.let { text -> presenter.onChangeFirstNameText(text) } }
        etLastName.onTextChanged { it?.toString()?.let { text -> presenter.onChangeLastNameText(text) } }
        etEmail.onTextChanged { it?.toString()?.let { text -> presenter.onChangeEmailText(text) } }
        etPassword.onTextChanged { it?.toString()?.let { text -> presenter.onChangePasswordText(text) } }
        etPasswordConfirm.onTextChanged { it?.toString()?.let { text -> presenter.onChangePasswordConfirmText(text) } }

        val agreementText = SpannableString(getString(R.string.auth_agree_user_agreement)).apply {
            val linkStart = 11
            val linkEnd = length
            setSpan(ClickableSpan(drawUnderline = false) {
                presenter.onClickUserAgreement()
            }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
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

        cbAgree.setOnCheckedChangeListener { _, isChecked -> presenter.onClickAgree(isChecked) }

        ibRegister.setOnClickListener {
            presenter.onClickRegister(
                    etEmail.text?.toString(),
                    etFirstName.text?.toString(),
                    etLastName.text?.toString(),
                    etPassword.text?.toString(),
                    etPasswordConfirm.text?.toString(),
                    cbAgree.isChecked
            )
        }

        ibFacebook.setOnClickListener { presenter.authFb() }
        ibVk.setOnClickListener { presenter.authVk() }
        ibOk.setOnClickListener { presenter.authOk() }
    }

    override fun onDestroyView() {
        tvAgree.text.toSpannable().clearSpans()
        super.onDestroyView()
    }

    override fun setData(email: String?, firstName: String?, lastName: String?, password: String?, passwordConfirm: String?, isAgree: Boolean) {
        etEmail.setText(email)
        etFirstName.setText(firstName)
        etLastName.setText(lastName)
        etPassword.setText(password)
        etPasswordConfirm.setText(passwordConfirm)
        cbAgree.isChecked = isAgree
    }

    override fun showFirstNameError(show: Boolean) {
        tilFirstName.error = if (show) getString(R.string.auth_error_no_first_name) else null
    }

    override fun showLastNameError(show: Boolean) {
        tilLastName.error = if (show) getString(R.string.auth_error_no_last_name) else null
    }

    override fun showEmailError(show: Boolean) {
        tilEmail.error = if (show) getString(R.string.auth_error_wrong_email) else null
    }

    override fun showPasswordError(show: Boolean) {
        tilPassword.error = if (show) getString(
                if (etPassword.text.isNullOrEmpty()) R.string.auth_error_no_password
                else R.string.auth_error_short_password
        ) else null
    }

    override fun showPasswordConfirmError(show: Boolean) {
        tilPasswordConfirm.error = if (show) getString(R.string.auth_error_password_do_not_match) else null
    }

    override fun showAgreementError(show: Boolean) {
        tvAgreeError.isInvisible = !show
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
        ibRegister.apply { isEnabled = isEnable }
    }

    override fun showEmailConfirmation(email: String, password: String) {
        findNavController().navigate(RegisterEmailFragmentDirections.emailRegisterToEmailConfirm(email, password, null))
    }

    override fun showSnRegistration(snUser: SnUser) {
        findNavController().navigate(RegisterEmailFragmentDirections.emailRegisterToSnRegister(snUser))
    }

    override fun showUserAgreement() {
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.auth_agree_address)))
            startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(requireContext(), R.string.error_title, Toast.LENGTH_LONG).show()
        }
    }

    override fun layout() = R.layout.fragment_register_email
}
