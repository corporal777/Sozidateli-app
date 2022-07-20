package com.example.ui.userprofile.read.settings

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.UserEditDataType
import com.example.databinding.FragmentUserProfileSettingsBinding
import com.example.extensions.*
import com.example.ui.base.BaseFragmentNew
import com.example.ui.main.MainActivity
import com.example.ui.views.*
import com.example.ui.views.toolbar.SimpleTitleToolbar
import com.example.util.PHONE_PERSONAL
import com.google.android.material.textfield.TextInputLayout
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileSettingsFragment : BaseFragmentNew<FragmentUserProfileSettingsBinding>(),
    UserProfileSettingsContract.View,
    SimpleTitleToolbar {

    private lateinit var dialog: AddPhoneEmailDialog
    private lateinit var passwordDialog: SetPasswordDialog

    override fun layout() = R.layout.fragment_user_profile_settings
    private var isConfirmed = false


    @InjectPresenter
    lateinit var presenter: UserProfileSettingsPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileSettingsPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileSettingsPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //btnPhoneEdit.setOnClickListener(presenter::onChangePhoneClick)
        setToolbarTitle(getString(R.string.profile_settings))
        mBinding.apply {
            nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                presenter.changeScrollingOffset(scrollY - oldScrollY)
            })

            btnPhoneEdit.setOnClickListener {
                dialog = AddPhoneEmailDialog(requireActivity(), RegisterDataType.CHANGE_PHONE)
                    .setSelectCallback {
                        if (it.type == RegisterDataType.CHANGE_PHONE) {
                            if (isConfirmed) {
                                //Toast.makeText(requireContext(), "isConfirmed", Toast.LENGTH_SHORT).show()
                                presenter.checkPhoneIsUnique(it.value)
                            } else {
                                presenter.onChangeNotConfirmedPhone(it.value)
                            }
                        }
                    }
            }
            btnPasswordEdit.setOnClickListener(presenter::onChangePasswordClick)

            btnDeleteProfile.setOnClickListener(presenter::onDeleteProfileClick)
            scPrivacy.setOnCheckedChangeListener { _, b ->
                presenter.onChangePrivacyConfirm(b)
            }
            btnEmailEdit.setOnClickListener(presenter::onChangeEmailClick)
        }

    }

    override fun hideDialogProgress() {
        dialog.isProgressVisible(false)
    }

    override fun phoneSuccess(phone: String) {
        dialog.hideDialog()

        passwordDialog = SetPasswordDialog(requireActivity())
            .setSelectCallback {
                presenter.onPasswordInputComplete(it, phone)
            }


    }

    override fun hideDialogProgress2() {
        passwordDialog.isProgressVisible(false)
    }

    override fun passwordSuccess(phone: String) {

        passwordDialog.hideDialog()
        dialog = AddPhoneEmailDialog(requireActivity(), RegisterDataType.CODE)
        dialog.setPhoneForCode(phone)
        dialog.setSelectCallback {
            if (it.type == RegisterDataType.CODE) {
                (requireActivity() as MainActivity).setIgnoreTokenListener(true)
                presenter.confirmCode(phone, it.value)
            }
        }
        dialog.setSendCodeCallback {
            presenter.sendPhone(phone)
        }
    }

    override fun codeSuccess() {
        //Toast.makeText(requireContext(), "Code confirmed", Toast.LENGTH_SHORT).show()
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
        dialog.hideDialog()
    }

    override fun onUserUpdated(user: UserDetail?, state: String) {
        user ?: return
        mBinding.apply {
            tilSurname.initNameInput(user.lastName)
            tilName.initNameInput(user.name)
            tilMiddleName.initNameInput(user.getMiddleName())
            scNoMiddleName.apply {
                isChecked = user.middleName?.absent ?: false
                isEnabled = false
            }

            isConfirmed = user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.isConfirmed == true

            val phone =
                user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.value?.parsePhone(
                    requireContext()
                )
            //tvPhoneMobile.isVisible = phone != null
            //tvPhoneMobileTitle.isVisible = phone != null
            //btnPhoneEdit.isVisible = phone != null
            tvPhoneMobile.text = phone

            tvEmail.text = user.email?.onConfirmation ?: user.email?.value
            scPrivacy.isChecked = user.state?.isHidden ?: false
            ivInfo.isVisible =
                !user.email?.onConfirmation.isNullOrEmpty() || user.email?.isConfirmed == false
            ivInfo.setOnClickListener {
                WaitForAcceptDialog(
                    requireActivity(),
                    getString(R.string.wait_for_accept_title),
                    getString(R.string.wait_for_accept_text),
                    getString(R.string.wait_for_accept_positive_button),
                    getString(R.string.content_description_delete),
                    true
                )
                    .setSendCodeCallback {
                        if (it) {
                            presenter.registerEmailResend(
                                user.email?.onConfirmation ?: user.email?.value ?: ""
                            )
                        } else {
                            if (user.email?.value == null) {
                                presenter.onDeleteEmail()
                            } else {
                                presenter.onDeleteConfirmEmail(user.email?.onConfirmation ?: "")
                            }
                        }
                    }
            }
        }


    }

    private fun TextInputLayout.initNameInput(text: String?) {
        editText?.setText(text)
        error = null
        isEnabled = true
        editText?.isEnabled = false
        setEndIconDrawable(R.drawable.ic_information)
        setEndIconTintMode(PorterDuff.Mode.MULTIPLY)
        setEndIconOnClickListener { showDisabledMainInputInfo(context) }
    }

    private fun showDisabledMainInputInfo(context: Context) {
        val supportEmail = context.getString(R.string.support_email)
        val message =
            context.getString(R.string.profile_edit_name_disabled_message).format(supportEmail)
                .toSpannable()
        Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES)

        AlertDialog.Builder(context)
            .setTitle(R.string.profile_edit_name_disabled_title)
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
            .show()
            .apply {
                findViewById<TextView>(android.R.id.message)?.let {
                    it.movementMethod = BetterLinkMovementMethod.getInstance()
                }
            }
    }

    override fun showChangeEmail() = showChangeEmailDialog(presenter::checkEmailIsUnique)

    override fun showNewChangeEmail(email: String) =
        showNewChangeEmailDialog(email, presenter::checkEmailIsUnique)

    override fun showChangeEmailComplete(email: String) = showChangeEmailCompleteDialog(email)

    override fun showEmailNotUnique(email: String) {
        ConfirmPhoneDialog(
            requireContext(), getString(R.string.confirm_email_text, email),
            getString(R.string.revoke), getString(R.string.confirm_phone_positive)
        )
            .setSelectCallback {
                if (it) {
                    presenter.registerEmailResend(email)
                }
            }
    }

    override fun showPhoneNotUnique(phone: String) {
        ConfirmPhoneDialog(
            requireContext(), getString(R.string.confirm_phone_text, phone),
            getString(R.string.revoke), getString(R.string.confirm_phone_positive)
        )
            .setSelectCallback {
                if (it) {
                    presenter.sendPhone(phone)
                } else {
                    dialog.isProgressVisible(false)
                }
            }
    }

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
            ?: title, Toast.LENGTH_SHORT).show()
    }

    //override fun showChangePassword() = showChangePasswordDialog(presenter::onChangePasswordClickConfirm)
    var newPassDialog: ChangePasswordDialog? = null

    override fun showChangePassword() {
        newPassDialog = ChangePasswordDialog(requireActivity())
            .setSelectCallback {
                presenter.checkPasswordValid(it.oldPassword, it.newPassword)
            }
    }

    override fun showOldPasswordError() {
        newPassDialog?.showInvalidCurrentPassword()
    }

    override fun hideNewPasswordDialog() {
        newPassDialog?.closeDialog()
        newPassDialog = null
    }

    override fun showPasswordChangeComplete() = showPasswordChangeCompleteDialog()

    override fun showChangePrivacy() {
        BottomDialog(requireContext()).apply {
            setTitle(R.string.user_profile_privacy_title)
            positiveButton {
                text = getString(R.string.user_profile_privacy_visible)
                clickListener = {
                    presenter.onChangePrivacyConfirm(false)
                    true
                }
            }
            negativeButton {
                text = getString(R.string.user_profile_privacy_hidden)
                clickListener = {
                    presenter.onChangePrivacyConfirm(true)
                    true
                }
            }
        }
            .show()
    }

    override fun showDeleteProfile() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.user_profile_delete_confirm_title)
            .setMessage(R.string.user_profile_delete_confirm_message)
            .setNegativeButton(R.string.user_profile_delete_confirm_approve) { _, _ ->
                presenter.onDeleteProfileConfirm()
            }
            .setPositiveButton(R.string.user_profile_delete_confirm_decline) { _, _ -> Unit }
            .show()
    }

    override fun showPhoneEdit() {
        findNavController().navigate(
            UserProfileSettingsFragmentDirections.toEdit(
                UserEditDataType.PHONE
            )
        )
    }
}
