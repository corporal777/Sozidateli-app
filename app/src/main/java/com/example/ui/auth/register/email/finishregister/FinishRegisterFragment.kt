package com.example.ui.auth.register.email.finishregister

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.data.models.SnUser
import com.example.databinding.FragmentFinishRegisterBinding
import com.example.ui.base.BaseFragmentNew
import com.example.ui.main.MainActivity
import com.example.ui.views.AddPhoneEmailDialog.Companion.CODE_SIZE
import com.example.ui.views.dialogs_new.CustomProgressDialog
import com.example.util.*
import com.example.util.Utils.timerFormatter
import onFocusChanged
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class FinishRegisterFragment : BaseFragmentNew<FragmentFinishRegisterBinding>(), FinishRegisterContract.View {

    private var isNoMiddleName = false
    private var loginType = "email"
    private var nameEditable: Boolean? = false
    private lateinit var mProgressDialog: CustomProgressDialog

    private val timerMessage by lazy {
        getString(R.string.auth_register_confirm_email_timer_two)
    }

    private val filter = arrayOf(InputFilter { source, _, _, _, _, _ ->
        source.toString().filter {
            it.isLetter() || it == '-' || it == ' '
        }
    })

    private val emailFilter = arrayOf(InputFilter { source, _, _, _, _, _ ->
        source.toString().filter {
            it.isLetter() || it.isDigit() || it == '.' || it == '@' || it == '_' || it == '+'
        }
    })

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
            val email = FinishRegisterFragmentArgs.fromBundle(it).email ?: ""
            presenter.onChangeEmailText(email)
            presenter.onChangePhoneText(FinishRegisterFragmentArgs.fromBundle(it).phone ?: "")
            presenter.phoneConfirmed(FinishRegisterFragmentArgs.fromBundle(it).isConfirmed)
            presenter.onSaveCode(FinishRegisterFragmentArgs.fromBundle(it).code ?: "")
            isNoMiddleName = FinishRegisterFragmentArgs.fromBundle(it).isNoMiddleName
            loginType = if (email.isEmpty()) "phone" else "email"
            presenter.loginType = loginType
            nameEditable = FinishRegisterFragmentArgs.fromBundle(it).nameEdited
            if (loginType == "email") {
                (requireActivity() as MainActivity).setIgnoreTokenListener(true)
                presenter.getData()
            } else presenter.startTimer()
            presenter.deviceModel = getDeviceName()
        }
        mProgressDialog = CustomProgressDialog(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            if (loginType == "email") {
                ibRegister.apply { isEnabled = true }
            }
            ivClose.setOnClickListener { presenter.onClickClose() }
            //cbAgree.setOnCheckedChangeListener { _, isChecked -> presenter.onClickAgree(isChecked) }
            //cbAgree.isChecked = true
            if (BuildConfig.NEW_PROFILE_EDIT) {
                //tilMiddleName.visibility = View.VISIBLE
                //llAgree.visibility = View.GONE
                //phone_layout.visibility = View.VISIBLE
            } else {
                //tilMiddleName.visibility = View.GONE
                //llAgree.visibility = View.VISIBLE
                //phone_layout.visibility = View.GONE
            }
            //scNoMiddleName.setOnCheckedChangeListener { _, checked -> presenter.onNoMiddleNameChecked(checked) }
            scNoMiddleName.initSwitch(isNoMiddleName) {
                presenter.onNoMiddleNameChecked(it)
            }
            /*etMobilePhone.getPhoneCallback { it.let { text ->
                presenter.onChangePhoneText(text)
            } }*/
            etFirstName.apply {
                filters = filter
                onTextChanged { it?.toString()?.let { text -> presenter.onChangeNameText(text) } }
                onFocusChanged { hasFocus ->
                    if (!hasFocus) {
                        setText(removeFirstAndLastSpaces(text.toString()))
                    }
                }
            }

            etLastName.apply {
                filters = filter
                onTextChanged { it?.toString()?.let { text -> presenter.onChangeLastNameText(text) } }
                onFocusChanged { hasFocus ->
                    if (!hasFocus) {
                        setText(removeFirstAndLastSpaces(text.toString()))
                    }
                }
            }

            etMiddleName.apply {
                filters = filter
                onTextChanged { it?.toString()?.let { text -> presenter.onChangeMiddleNameText(text) } }
                onFocusChanged { hasFocus ->
                    if (!hasFocus) {
                        setText(removeFirstAndLastSpaces(text.toString()))
                    }
                }
            }
            etEmail.filters = emailFilter
            etCode.onTextChanged {
                tilCode.error = null
                it?.toString()?.let { text -> presenter.onChangeCodeText(text) }
            }
            val agreementText = SpannableString(getString(R.string.auth_agree_user_agreement)).apply {
                val linkStart = 11
                val linkEnd = length
                setSpan(ClickableSpan(drawUnderline = false) {
                    presenter.onClickUserAgreement()
                }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }

            /*tvAgree.apply {
                text = agreementText
                movementMethod = LinkMovementMethod.getInstance()
            }

            flAgree.setOnClickListener {
                cbAgree.apply {
                    isChecked = !isChecked
                }
            }*/
            btnResend.apply {
                setTextColor(ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_enabled), intArrayOf(-android.R.attr.state_enabled)),
                    intArrayOf(ContextCompat.getColor(requireContext(), R.color.colorAccent), ContextCompat.getColor(requireContext(), R.color.action_button_disabled_text_color))
                ))
                setOnClickListener { presenter.sendCodeAgain() }
            }
            ibCancel.setOnClickListener {
                hideKeyboard()
                (requireActivity() as MainActivity).setIgnoreTokenListener(true)
                presenter.logout()
            }
            ibRegister.setOnClickListener {
                hideKeyboard()
                when (loginType) {
                    "phone" -> {
                        if (etCode.text?.length != CODE_SIZE) {
                            tilCode.error = resources.getString(R.string.auth_error_no_code)
                        } else {
                            (requireActivity() as MainActivity).setIgnoreTokenListener(true)
                            presenter.onHandleAuthLink()
                        }
                    }
                    "email" -> {
                        (requireActivity() as MainActivity).setIgnoreTokenListener(true)
                        presenter.onHandleAuthLink()
                    }
                }

                /*if (etMobilePhone.getNumberWithoutCode() == "" || etMobilePhone.getIsValid()) {
                presenter.onHandleAuthLink()
                } else {
                    showWrongPhoneError(true)
                }*/
            }
            //btnPhoneConfirm.setOnClickListener { presenter.onPhoneConfirmClick() }
            //scNoMiddleName.isChecked = isNoMiddleName
            when (loginType) {
                "phone" -> {
                    tvText.text = requireContext().resources.getString(R.string.code_dialog_text, presenter.phone)
                    layPhoneConfirm.isVisible = true
                }
                "email" -> {
                    layPhoneConfirm.isVisible = false
                }
            }
        }

    }

    override fun logedout() {
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
        findNavController().navigate(R.id.register_email_new_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build())
    }

    override fun codeError() {
        mBinding.tilCode.error = resources.getString(R.string.auth_error_code)
    }

    override fun openHome() {
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
        findNavController().navigate(FinishRegisterFragmentDirections.registerToMail(true))
    }

    override fun codeSuccess() {
        Toast.makeText(requireContext(), "Код был отправлен повторно", Toast.LENGTH_SHORT).show()
    }

    override fun enableMiddleNameInput(enable: Boolean) {
        mBinding.etMiddleName.isEnabled = enable
        if (!enable) {
            mBinding.etMiddleName.setText("")
            presenter.onChangeMiddleNameText("")
        }
    }

    override fun setData(email: String?, firstName: String?, middleName: String?, lastName: String?, phone: String?, isAgree: Boolean, phoneVerified: Boolean) {
        mBinding.apply {
            when (loginType) {
                "phone" -> {
                    etEmail.setText(phone)
                }
                "email" -> {
                    etEmail.setText(email)
                }
            }
            etFirstName.setText(firstName)
            etFirstName.isEnabled = !(nameEditable?: false)
            etLastName.setText(lastName)
            etLastName.isEnabled = !(nameEditable?: false)
            if (middleName == "-") {
                etMiddleName.isEnabled = false
                scNoMiddleName.isChecked = true
            } else {
                etMiddleName.setText(middleName)
                scNoMiddleName.isChecked = false
            }
            etMiddleName.isEnabled = !(nameEditable?: false)
            scNoMiddleName.isEnabled = !(nameEditable?: false)
            updatePhoneConfirmationStatus(phoneVerified)
        }
    }

    override fun unblockTokenListener() {
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
    }

    override fun showAgreementError(show: Boolean) {
        //tvAgreeError.isInvisible = !show
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun updatePhoneConfirmationStatus(confirmed: Boolean) {
        /*btnPhoneConfirm.isVisible = !confirmed
        tvPhoneConfirmed.isVisible = confirmed*/
    }

    override fun phoneConfirmEnabled(enabled: Boolean) {
        /*btnPhoneConfirm.isEnabled = enabled
        btnPhoneConfirm.isVisible = enabled*/
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
        mBinding.ibRegister.apply { isEnabled = isEnable }
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

    override fun setTimeLeft(seconds: Int) {
        //val quantity = resources.getQuantityString(R.plurals.seconds_timer, seconds, seconds)
        val quantity = timerFormatter(seconds, requireContext())
        mBinding.tvTimer.text = String.format(timerMessage, quantity)
    }

    override fun setCanResend(canResend: Boolean) {
        mBinding.btnResend.isEnabled = canResend
        mBinding.tvTimer.isVisible = !canResend
    }

    override fun showAlertLoadingDialog() {
        mProgressDialog.showDialog()
    }

    override fun hideAlertLoadingDialog() {
        mProgressDialog.hideDialog()
    }

    override fun showWrongPhoneError(show: Boolean) {
        /*etMobilePhone.showError(show)
        tilMobilePhone.apply {
            error = if (show) getString(R.string.register_phone_error) else null
        }*/
    }

    override fun showPhoneConfirm(phone: String) {
        findNavController().navigate(FinishRegisterFragmentDirections.emailRegisterToPhoneConfirmFragment(phone, "", null))
    }

    override fun layout() = R.layout.fragment_finish_register
}