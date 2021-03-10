package com.example.ui.auth.register.email.finishregister

import android.content.Context
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
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.data.models.SnUser
import com.example.ui.base.BaseFragment
import com.example.util.ClickableSpan
import com.example.util.initSwitch
import kotlinx.android.synthetic.main.fragment_finish_register.*
import kotlinx.android.synthetic.main.fragment_finish_register.btnPhoneConfirm
import kotlinx.android.synthetic.main.fragment_finish_register.cbAgree
import kotlinx.android.synthetic.main.fragment_finish_register.etEmail
import kotlinx.android.synthetic.main.fragment_finish_register.etFirstName
import kotlinx.android.synthetic.main.fragment_finish_register.etLastName
import kotlinx.android.synthetic.main.fragment_finish_register.etMiddleName
import kotlinx.android.synthetic.main.fragment_finish_register.etMobilePhone
import kotlinx.android.synthetic.main.fragment_finish_register.flAgree
import kotlinx.android.synthetic.main.fragment_finish_register.ibRegister
import kotlinx.android.synthetic.main.fragment_finish_register.ivClose
import kotlinx.android.synthetic.main.fragment_finish_register.scNoMiddleName
import kotlinx.android.synthetic.main.fragment_finish_register.tvAgree
import kotlinx.android.synthetic.main.fragment_finish_register.tvAgreeError
import kotlinx.android.synthetic.main.fragment_finish_register.tvPhoneConfirmed
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class FinishRegisterFragment : BaseFragment(), FinishRegisterContract.View {

    private var isNoMiddleName = false

    @InjectPresenter
    lateinit var presenter: FinishRegisterPresenter

    @Inject
    lateinit var presenterProvider: Provider<FinishRegisterPresenter>

    @ProvidePresenter
    fun providePresenter(): FinishRegisterPresenter = presenterProvider.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            presenter.onChangeNameText(FinishRegisterFragmentArgs.fromBundle(it).name ?: "")
            presenter.onChangeLastNameText(FinishRegisterFragmentArgs.fromBundle(it).lastName ?: "")
            presenter.onChangeMiddleNameText(FinishRegisterFragmentArgs.fromBundle(it).middleName ?: "")
            presenter.onChangeEmailText(FinishRegisterFragmentArgs.fromBundle(it).email ?: "")
            presenter.onChangePhoneText(FinishRegisterFragmentArgs.fromBundle(it).phone ?: "")
            presenter.phoneConfirmed(FinishRegisterFragmentArgs.fromBundle(it).isConfirmed)
            presenter.onSaveCode(FinishRegisterFragmentArgs.fromBundle(it).code ?: "")
            isNoMiddleName = FinishRegisterFragmentArgs.fromBundle(it).isNoMiddleName
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivClose.setOnClickListener { presenter.onClickClose() }
        if (BuildConfig.NEW_PROFILE_EDIT) {
            tilMiddleName.visibility = View.VISIBLE
            phone_layout.visibility = View.VISIBLE
        } else {
            tilMiddleName.visibility = View.GONE
            phone_layout.visibility = View.GONE
        }
        //scNoMiddleName.setOnCheckedChangeListener { _, checked -> presenter.onNoMiddleNameChecked(checked) }
        scNoMiddleName.initSwitch(isNoMiddleName) {
            presenter.onNoMiddleNameChecked(it)
        }
        etMobilePhone.getPhoneCallback { it.let { text ->
            presenter.onChangePhoneText(text)
        } }
        etMiddleName.onTextChanged { it?.toString()?.let { text -> presenter.onChangeMiddleNameText(text) } }
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

        ibCancel.setOnClickListener {
            presenter.onHandleAuthLink()
        }
        ibRegister.setOnClickListener {
            if (etMobilePhone.getNumberWithoutCode() == "" || etMobilePhone.getIsValid()) {
                presenter.onHandleAuthLink()
            } else {
                showWrongPhoneError(true)
            }
        }
        btnPhoneConfirm.setOnClickListener { presenter.onPhoneConfirmClick() }
        //scNoMiddleName.isChecked = isNoMiddleName
    }

    override fun onDestroyView() {
        tvAgree.text.toSpannable().clearSpans()
        super.onDestroyView()
    }

    override fun enableMiddleNameInput(enable: Boolean) {
        etMiddleName.isEnabled = enable
        if (!enable) {
            etMiddleName.setText("")
            presenter.onChangeMiddleNameText("")
        }
    }

    override fun setData(email: String?, firstName: String?, middleName: String?, lastName: String?, phone: String?, isAgree: Boolean, phoneVerified: Boolean) {
        etEmail.setText(email)
        etFirstName.setText(firstName)
        etLastName.setText(lastName)
        if (middleName == "-") {
            etMiddleName.isEnabled = false
            scNoMiddleName.isChecked = true
        } else {
            etMiddleName.setText(middleName)
            scNoMiddleName.isChecked = false
        }
        //etMobilePhone.setText(phone)
        etMobilePhone.setPhone(phone?: "")
        cbAgree.isChecked = isAgree
        updatePhoneConfirmationStatus(phoneVerified)
    }

    override fun showAgreementError(show: Boolean) {
        tvAgreeError.isInvisible = !show
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun updatePhoneConfirmationStatus(confirmed: Boolean) {
        btnPhoneConfirm.isVisible = !confirmed
        tvPhoneConfirmed.isVisible = confirmed
    }

    override fun phoneConfirmEnabled(enabled: Boolean) {
        btnPhoneConfirm.isEnabled = enabled
        btnPhoneConfirm.isVisible = enabled
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
        ibRegister.apply { isEnabled = isEnable }
    }

    override fun showSnRegistration(snUser: SnUser) { 

    }

    override fun showUserAgreement() {
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.auth_agree_address)))
            startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(requireContext(), R.string.error_title, Toast.LENGTH_LONG).show()
        }
    }

    override fun showWrongPhoneError(show: Boolean) {
        etMobilePhone.showError(show)
        /*tilMobilePhone.apply {
            error = if (show) getString(R.string.register_phone_error) else null
        }*/
    }

    override fun showPhoneConfirm(phone: String) {
        findNavController().navigate(FinishRegisterFragmentDirections.emailRegisterToPhoneConfirmFragment(phone, "", null))
    }

    override fun layout() = R.layout.fragment_finish_register
}