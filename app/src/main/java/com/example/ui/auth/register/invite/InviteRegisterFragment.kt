package com.example.ui.auth.register.invite

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentInviteRegisterBinding
import com.example.extensions.showChangeEmailCompleteDialog
import com.example.ui.base.BaseFragment
import com.example.ui.main.MainActivity
import com.example.ui.userprofile.read.settings.confirm_phone_email.ConfirmEmailPhoneFragment
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class InviteRegisterFragment : BaseFragment<FragmentInviteRegisterBinding>(),
    InviteRegisterContract.View {

    private val filter = arrayOf(InputFilter { source, _, _, _, _, _ ->
        source.toString().filter {
            it.isLetter() || it == '-'
        }
    })

    @InjectPresenter
    lateinit var presenter: InviteRegisterPresenter

    @Inject
    lateinit var presenterProvider: Provider<InviteRegisterPresenter>

    @ProvidePresenter
    fun providePresenter(): InviteRegisterPresenter = presenterProvider.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            presenter.onSaveEmailText(
                InviteRegisterFragmentArgs.fromBundle(it).email,
                InviteRegisterFragmentArgs.fromBundle(it).name,
                InviteRegisterFragmentArgs.fromBundle(it).lastName,
                InviteRegisterFragmentArgs.fromBundle(it).middleName,
                InviteRegisterFragmentArgs.fromBundle(it).invite
            )
            presenter.onSaveCode(InviteRegisterFragmentArgs.fromBundle(it).code)
            (requireActivity() as MainActivity).setIgnoreTokenListener(true)
            presenter.getData()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            ivClose.setOnClickListener { presenter.onClickClose() }

            etFirstName.onTextChanged {
                it?.toString()?.let { text -> presenter.onChangeFirstNameText(text) }
            }
            etFirstName.filters = filter
            etLastName.onTextChanged {
                it?.toString()?.let { text -> presenter.onChangeLastNameText(text) }
            }
            etLastName.filters = filter
            etMiddleName.onTextChanged {
                it?.toString()?.let { text -> presenter.onChangeMiddleNameText(text) }
            }
            etMiddleName.filters = filter
            scNoMiddleName.setOnCheckedChangeListener { _, checked ->
                presenter.onNoMiddleNameChecked(
                    checked
                )
            }

            password.setShowAgree(true)

            password.setHyperlinkClickCallback {
                showUserAgreement()
            }
            password.setPasswordValidCallback {
                presenter.onChangePasswordText(it.password ?: "", it.isValid)
            }
            password.setChangedSelectionCallback {
                presenter.onAgreeChecked(it)
            }

            ibRegister.setOnClickListener {
                presenter.onClickRegister(
                    etEmail.text?.toString(),
                    etFirstName.text?.toString(),
                    etLastName.text?.toString(),
                    password.etPassword.text?.toString(),
                    password.cbAgree.isChecked
                )
            }
            ibCancel.setOnClickListener {
                (requireActivity() as MainActivity).setIgnoreTokenListener(true)
                presenter.logout()
            }

            ibRegistered.setOnClickListener {
                findNavController().navigate(
                    InviteRegisterFragmentDirections.actionToInviteRegisterToLoginFragment(
                        ""
                    ).setIsRegistered(true).setInviteId(presenter.invite ?: 0)
                )
            }
        }

    }

    override fun blockTokenListener() {
        (requireActivity() as MainActivity).setIgnoreTokenListener(true)
    }

    override fun unblockTokenListener() {
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
    }

    override fun logedout() {
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
        findNavController().navigate(
            R.id.register_email_new_fragment, null, NavOptions.Builder()
                .setPopUpTo(R.id.main_navigation, true)
                .build()
        )
    }

    override fun openHome() {
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
        findNavController().navigate(InviteRegisterFragmentDirections.inviteRegisterToMail(true))
    }

    override fun enableMiddleNameInput(enable: Boolean) {
        mBinding.etMiddleName.isEnabled = enable
        if (!enable) {
            mBinding.etMiddleName.setText("")
            presenter.onChangeMiddleNameText("")
        }
    }

    override fun updateFieldsInUI(
        firstName: String,
        lastName: String,
        middleName: String,
        email: String
    ) {
        mBinding.apply {
            etFirstName.setText(firstName)
            etLastName.setText(lastName)
            etMiddleName.setText(middleName)
            etEmail.setText(email)
        }
    }

    override fun showWrongPhoneError(show: Boolean) {
    }

    override fun showPhoneConfirm(phone: String) {
        val confirmPhone = ConfirmEmailPhoneFragment(phone)
        confirmPhone.show(requireActivity().supportFragmentManager, "confirm_phone")
        confirmPhone.setConfirmCallback {

        }
    }

    override fun phoneConfirmEnabled(enabled: Boolean) {
    }

    override fun updatePhoneConfirmationStatus(confirmed: Boolean) {
    }



    override fun enableRegisterBtn(isEnable: Boolean) {
        mBinding.ibRegister.apply { isEnabled = isEnable }
    }

    override fun showPasswordConfirmError(show: Boolean) {
    }

    override fun showPasswordError(show: Boolean) {
    }

    override fun showFirstNameError(show: Boolean) {
        mBinding.tilFirstName.error = if (show) getString(R.string.auth_error_no_first_name) else null
    }

    override fun showLastNameError(show: Boolean) {
        mBinding.tilLastName.error = if (show) getString(R.string.auth_error_no_last_name) else null
    }

    override fun showEmailError(show: Boolean) {
        mBinding.tilEmail.error = if (show) getString(R.string.auth_error_wrong_email) else null
    }

    override fun showUserAgreement() {
        try {
            val viewIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.auth_agree_address)))
            startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(requireContext(), R.string.error_title, Toast.LENGTH_LONG).show()
        }
    }

    override fun showEmailDialog(email: String) {
        showChangeEmailCompleteDialog(email)
    }

    override fun layout(): Int = R.layout.fragment_invite_register
}