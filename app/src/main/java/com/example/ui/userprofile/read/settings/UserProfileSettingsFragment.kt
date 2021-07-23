package com.example.ui.userprofile.read.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.UserEditDataType
import com.example.extensions.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.main.MainActivity
import com.example.ui.views.AddPhoneEmailDialog
import com.example.ui.views.BottomDialog
import com.example.ui.views.RegisterDataType
import com.example.ui.views.SetPasswordDialog
import com.example.util.PHONE_PERSONAL
import kotlinx.android.synthetic.main.fragment_user_profile_settings.*
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileSettingsFragment : BaseFragment(), UserProfileSettingsContract.View, ToolbarFragment {

    private lateinit var dialog: AddPhoneEmailDialog
    private lateinit var passwordDialog: SetPasswordDialog
    override val title: String?
        get() = getString(R.string.profile_settings)

    override fun layout() = R.layout.fragment_user_profile_settings

    @InjectPresenter
    lateinit var presenter: UserProfileSettingsPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileSettingsPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileSettingsPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //btnPhoneEdit.setOnClickListener(presenter::onChangePhoneClick)
        btnPhoneEdit.setOnClickListener {
            dialog = AddPhoneEmailDialog(requireActivity(), RegisterDataType.CHANGE_PHONE)
                    .setSelectCallback {
                        if (it.type == RegisterDataType.CHANGE_PHONE)
                            presenter.sendPhone(it.value)
                    }
        }
        btnPasswordEdit.setOnClickListener(presenter::onChangePasswordClick)
        btnEmailEdit.setOnClickListener(presenter::onChangeEmailClick)
        btnDeleteProfile.setOnClickListener(presenter::onDeleteProfileClick)
        scPrivacy.setOnCheckedChangeListener { _, b ->
            presenter.onChangePrivacyConfirm(b)
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
        Toast.makeText(requireContext(), "Code confirmed", Toast.LENGTH_SHORT).show()
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
        dialog.hideDialog()
    }

    override fun onUserUpdated(user: UserDetail?, state: String) {
        user ?: return

        val phone = user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.value?.parsePhone(requireContext())
        //tvPhoneMobile.isVisible = phone != null
        //tvPhoneMobileTitle.isVisible = phone != null
        //btnPhoneEdit.isVisible = phone != null
        tvPhoneMobile.text = phone

        tvEmail.text = user.email?.value
        scPrivacy.isChecked = user.state?.isHidden?: false

    }

    override fun showChangeEmail() = showChangeEmailDialog(presenter::onChangeEmailConfirm)

    override fun showChangeEmailComplete(email: String) = showChangeEmailCompleteDialog(email)

    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
                ?: title, Toast.LENGTH_SHORT).show()
    }

    override fun showChangePassword() = showChangePasswordDialog(presenter::onChangePasswordClickConfirm)

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
        findNavController().navigate(UserProfileSettingsFragmentDirections.toEdit(UserEditDataType.PHONE))
    }
}
