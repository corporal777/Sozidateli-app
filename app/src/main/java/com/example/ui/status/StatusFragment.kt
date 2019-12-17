package com.example.ui.status

import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserStatusDetails
import com.example.data.models.user.User
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.LoadingAlertDialog
import isValidPhoneNumber
import kotlinx.android.synthetic.main.dialog_check_password.view.*
import kotlinx.android.synthetic.main.dialog_remove_status_phone.view.*
import kotlinx.android.synthetic.main.dialog_set_status_phone_code.view.*
import kotlinx.android.synthetic.main.dialog_set_status_phone_number.view.*
import kotlinx.android.synthetic.main.fragment_status.*
import onTextChanged
import setUserStatus
import javax.inject.Inject
import javax.inject.Provider

class StatusFragment : BaseFragment(), StatusContract.View {

    @InjectPresenter
    lateinit var presenter: StatusPresenter

    @Inject
    lateinit var presenterProvider: Provider<StatusPresenter>

    @ProvidePresenter
    fun providePresenter(): StatusPresenter = presenterProvider.get().apply {
        status = arguments!!.getSerializable(ARG_STATUS) as User.Status
    }

    private val completeIcon by lazy {
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_status_complete)
    }

    private val uncompleteIcon by lazy {
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_status_uncomplete)
    }

    private val scrollListener = NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
        statusScrollListener?.run {
            invoke(scrollContainer.scrollY)
        }
    }

    private var phoneVerificationAlertDialog: LoadingAlertDialog? = null
    private var phoneVerificationErrorCallback: ((error: Int) -> Unit)? = null

    var statusScrollListener: ((scrollY: Int) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollContainer.setOnScrollChangeListener(scrollListener)
    }

    override fun setStatus(status: User.Status, isCurrentStatus: Boolean, phone: String?, details: UserStatusDetails?) {
        tvStatus.apply {
            setUserStatus(status)
        }

        tvYourStatus.isInvisible = !isCurrentStatus

        val isSecurityComplete: Boolean
        val isSupportComplete: Boolean
        val isInvitesComplete: Boolean
        val isPhoneComplete: Boolean
        when (status) {
            User.Status.LOW_PROTECTION -> {
                isSecurityComplete = false
                isSupportComplete = false
                isInvitesComplete = false
                isPhoneComplete = false
            }
            User.Status.MID_PROTECTION -> {
                isSecurityComplete = true
                isSupportComplete = true
                isInvitesComplete = false
                isPhoneComplete = false
            }
            User.Status.MAX_PROTECTION -> {
                isSecurityComplete = true
                isSupportComplete = true
                isInvitesComplete = true
                isPhoneComplete = false
            }
        }

        setCompleteIcon(tvSecurity, isSecurityComplete)
        setCompleteIcon(tvSupport, isSupportComplete)
        setCompleteIcon(tvInvites, isInvitesComplete)
        setCompleteIcon(tvPhone, isPhoneComplete)
        setCompleteIcon(tvProfile, details?.params?.let {
            it.birthday.value
                    && it.education.value
                    && it.name.value
                    && it.notes.value
                    && it.socialLinks.value
                    && it.work.value
        } ?: false)
        setCompleteIcon(tvPhone, phone != null)

//        setCounterBackgroundTint(tvNameCounter, details?.params?.name?.value ?: false)
//        setCounterBackgroundTint(tvBirthdayCounter, details?.params?.birthday?.value ?: false)
//        setCounterBackgroundTint(tvSnCounter, details?.params?.socialLinks?.value ?: false)
//        setCounterBackgroundTint(tvEducationCounter, details?.params?.education?.value ?: false)
//        setCounterBackgroundTint(tvWorkCounter, details?.params?.work?.value ?: false)
//        setCounterBackgroundTint(tvAdditionalCounter, details?.params?.notes?.value ?: false)

        tvPhoneNumber.apply {
            text = phone
            isVisible = phone != null
        }

        btnAction.apply {
            val isAccept = phone == null
            text = getString(if (isAccept) R.string.accept else R.string.change)
            setOnClickListener { if (isAccept) presenter.onAddPhoneClick() else presenter.onChangePhoneClick() }
        }

        btnRemove.apply {
            isVisible = phone != null
            setOnClickListener { presenter.onRemovePhoneClick() }
        }
    }

    private fun setCompleteIcon(textView: TextView, isComplete: Boolean) {
        textView.setCompoundDrawablesWithIntrinsicBounds(if (isComplete) completeIcon else uncompleteIcon, null, null, null)
    }

    fun setContentAlpha(alpha: Float) {
        llContent.alpha = alpha
    }

    fun getStatusViewLocation(): Int {
        return tvStatus.let {
            val location = IntArray(2).apply { it.getLocationOnScreen(this) }
            location[1] + it.height
        }
    }

    fun scrollStatusTo(y: Int) {
        scrollContainer.apply {
            setOnScrollChangeListener(null as NestedScrollView.OnScrollChangeListener?)
            scrollTo(0, y)
            setOnScrollChangeListener(scrollListener)
        }
    }

    override fun checkPassword(action: Int) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_check_password, null).apply {
            etPassword.onTextChanged { tilPassword.error = null }
            val message = when (action) {
                StatusPresenter.VERIFICATION_ACTION_CHANGE -> R.string.status_profile_message_change
                StatusPresenter.VERIFICATION_ACTION_REMOVE -> R.string.status_profile_message_remove
                else -> R.string.status_profile_message_set
            }

            tvPasswordDescription.text = getString(message)
        }

        showPhoneVerificationDialog(action, view) {
            val password = view.etPassword.text?.toString()
            if (password.isNullOrEmpty()) {
                view.tilPassword.error = getString(R.string.auth_error_no_password)
            } else {
                presenter.onPasswordInputComplete(password, action)
            }

            false
        }.apply {
            phoneVerificationErrorCallback = {
                if (it == StatusPresenter.VERIFICATION_ERROR_WRONG_PASSWORD) {
                    view.tilPassword.error = getString(R.string.status_profile_check_password_wrong)
                }
            }
        }
    }

    override fun showChangePhone(action: Int) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_set_status_phone_number, null).apply {
            etPhone.onTextChanged { tilPhone.error = null }
            etPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())

            val message = when (action) {
                StatusPresenter.VERIFICATION_ACTION_CHANGE -> R.string.status_profile_status_phone_number_change
                else -> R.string.status_profile_status_phone_number_set
            }

            tvPhoneDescription.text = getString(message)
        }

        showPhoneVerificationDialog(action, view) {
            val phone = view.etPhone.text?.toString()
            if (phone.isValidPhoneNumber(requireContext())) {
                val saveTo = when (view.radioGroup.checkedRadioButtonId) {
                    R.id.rbMobile -> StatusPresenter.VERIFICATION_ADD_PHONE_MOBILE
                    R.id.rbWork -> StatusPresenter.VERIFICATION_ADD_PHONE_WORK
                    else -> StatusPresenter.VERIFICATION_ADD_PHONE_NONE
                }
                presenter.onPhoneInputComplete(phone!!, action, saveTo)
            } else {
                view.tilPhone.error = getString(R.string.invalid_phone_number_error)
            }

            false
        }
    }

    override fun showCode(phone: String, action: Int, saveFlag: Int) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_set_status_phone_code, null).apply {
            etCode.onTextChanged { tilCode.error = null }
            val message = getString(R.string.status_profile_status_code_set)
            val messageFormat = message.format(phone).toSpannable().apply {
                val start = indexOf(phone)
                val end = start + phone.length
                set(start, end, StyleSpan(BOLD))
            }
            tvCodeDescription.text = messageFormat

            btnDoNotReceiveCode.setOnClickListener { presenter.onDoNotReceiveCodeClick(phone) }
        }

        showPhoneVerificationDialog(action, view) {
            val code = view.etCode.text?.toString()
            if (code.isNullOrEmpty()) {
                view.tilCode.error = getString(R.string.status_profile_status_code_empty)
            } else {
                presenter.onCodeInputComplete(phone, code, saveFlag)
            }

            false
        }.apply {
            phoneVerificationErrorCallback = {
                if (it == StatusPresenter.VERIFICATION_ERROR_WRONG_CODE) {
                    view.tilCode.error = getString(R.string.status_profile_status_code_error)
                }
            }
        }
    }

    override fun showSentNewCodeMessage(phone: String) {
        showToast(getString(R.string.status_profile_status_code_resend).format(phone))
    }

    override fun showRemovePhone(phone: String) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_remove_status_phone, null).apply {
            val message = getString(R.string.status_profile_status_phone_remove)
            val messageFormat = message.format(phone).toSpannable().apply {
                val start = indexOf(phone)
                val end = start + phone.length
                set(start, end, StyleSpan(BOLD))
            }
            tvRemoveDescription.text = messageFormat
        }

        showPhoneVerificationDialog(StatusPresenter.VERIFICATION_ACTION_REMOVE, view) {
            presenter.onPhoneRemoveAccept()
            true
        }
    }

    private fun showPhoneVerificationDialog(action: Int, content: View, onAccept: (AlertDialog) -> Boolean): AlertDialog {
        return AlertDialog.Builder(requireContext())
                .setTitle(when (action) {
                    StatusPresenter.VERIFICATION_ACTION_CHANGE -> R.string.status_profile_title_change
                    StatusPresenter.VERIFICATION_ACTION_REMOVE -> R.string.status_profile_title_remove
                    else -> R.string.status_profile_title_set
                })
                .setView(content)
                .setPositiveButton(R.string.accept, null)
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .create()
                .apply {
                    setOnShowListener {
                        with(getButton(AlertDialog.BUTTON_POSITIVE)) {
                            setOnClickListener {
                                if (onAccept(this@apply)) {
                                    dismiss()
                                }
                            }
                        }
                    }
                    phoneVerificationErrorCallback = null
                    phoneVerificationAlertDialog?.sourceDialog?.dismiss()
                    phoneVerificationAlertDialog = LoadingAlertDialog.createFrom(this)
                    show()
                }
    }

    override fun cancelVerification() {
        phoneVerificationAlertDialog?.sourceDialog?.dismiss()
        phoneVerificationAlertDialog = null
        phoneVerificationErrorCallback = null
    }

    override fun showVerificationError(error: Int) {
        phoneVerificationErrorCallback?.invoke(error)
    }

    override fun showLoadingDialog() {
        phoneVerificationAlertDialog?.showLoading(true)
    }

    override fun hideLoadingDialog() {
        phoneVerificationAlertDialog?.showLoading(false)
    }

    override fun hideAllLoadingDialogs() {
        phoneVerificationAlertDialog?.showLoading(false)
    }

    override fun layout() = R.layout.fragment_status

    companion object {
        private const val ARG_STATUS = "status"
        fun initWithStatus(status: User.Status): StatusFragment {
            return StatusFragment().apply {
                arguments = bundleOf(ARG_STATUS to status)
            }
        }
    }
}
