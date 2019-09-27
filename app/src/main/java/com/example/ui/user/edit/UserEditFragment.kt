package com.example.ui.user.edit

import android.graphics.Bitmap
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserAddress
import com.example.data.models.asOptional
import com.example.data.models.user.User
import com.example.holders.ProfileDataEducationEditGroup
import com.example.holders.ProfileDataPersonalEditItem
import com.example.holders.ProfileDataUserEditItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.util.AuthValidateUtil
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.dialog_change_password.view.*
import kotlinx.android.synthetic.main.fragment_chat_list.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class UserEditFragment : BaseFragment(), UserEditContract.View, ToolbarFragment {

    override val title
        get() = "Редактирование"

    @InjectPresenter
    lateinit var presenter: UserEditPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserEditPresenter>

    @ProvidePresenter
    fun providePresenter(): UserEditPresenter = presenterProvider.get().apply {
        editType = UserEditFragmentArgs.fromBundle(arguments!!).type
    }

    private val adapter = GroupAdapter<ViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@UserEditFragment.adapter
        }
    }

    override fun setMainData(user: User, avatar: Bitmap?) {
        adapter.update(listOf(ProfileDataUserEditItem(
                avatar,
                user.user_name,
                user.user_last_name,
                user.user_middle_name,
                { presenter.onRemoveAvatarClick() },
                { presenter.onEditAvatarClick() },
                { presenter.onSaveClick(it) },
                { presenter.onCancelClick() },
                { presenter.onDisabledMainInputInfoClick() }
        )))
    }

    override fun showDisabledMainInputInfo() {
        val message = SpannableString(getString(R.string.profile_edit_name_disabled_message))
        Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES)
        val dialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.profile_edit_name_disabled_title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show()

        (dialog.findViewById(android.R.id.message) as? TextView)?.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun showTakePictureChooser() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.photo_alert_title)
                .setPositiveButton(R.string.photo_alert_gallery) { _, _ -> presenter.onTakePhotoFromGalleryRequest() }
                .setNegativeButton(R.string.photo_alert_camera) { _, _ -> presenter.onTakePhotoFromCameraRequest() }
                .show()
    }

    override fun changeUserAvatar(avatar: Bitmap?) {
        adapter.notifyItemChanged(0, avatar.asOptional())
    }

    override fun setPersonalData(user: User) {
        adapter.update(listOf(ProfileDataPersonalEditItem(
                requireContext(),
                user.user_email,
                user.user_email_show,
                user.user_phone_work,
                user.user_phone_work_show,
                user.user_phone,
                user.user_phone_show,
                user.user_gender,
                user.user_birthday,
                user.user_birthday_show,
                UserAddress.fromUser(user),
                user.social_links,
                { presenter.onSaveClick(it) },
                { presenter.onCancelClick() },
                { presenter.onChangeEmailClick() },
                { presenter.onChangePasswordClick() }
        )))
    }

    override fun showChangeEmail() {
        val view = layoutInflater.inflate(R.layout.dialog_change_email, null)
        val til = view.findViewById<TextInputLayout>(R.id.tilEmail)
        val et = view.findViewById<EditText>(R.id.etEmail)
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.profile_email_change)
                .setView(view)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .apply {
                    setOnShowListener {
                        getButton(AlertDialog.BUTTON_POSITIVE).apply {
                            setOnClickListener {
                                val email = et.text.toString()
                                if (AuthValidateUtil.isValidEmail(email)) {
                                    presenter.onChangeEmailConfirm(email)
                                    dismiss()
                                } else til.error = getString(R.string.profile_edit_email_invalid)
                            }
                        }
                    }
                }
                .show()
    }

    override fun showChangeEmailComplete(email: String) {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.email_change_title)
                .setMessage(String.format(getString(R.string.email_change_msg, email)))
                .setPositiveButton(R.string.ok, null)
                .show()
    }

    override fun showChangePassword() {
        val view = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val tilOldPassword = view.findViewById<TextInputLayout>(R.id.tilOldPassword)
        val etOldPassword = view.findViewById<EditText>(R.id.etOldPassword).apply {
            onTextChanged { tilOldPassword.error = null }
        }
        val tilNewPassword = view.findViewById<TextInputLayout>(R.id.tilNewPassword)
        val etNewPassword = view.findViewById<EditText>(R.id.etNewPassword).apply {
            onTextChanged { tilNewPassword.error = null }
        }
        val tilNewPasswordConfirm = view.findViewById<TextInputLayout>(R.id.tilNewPasswordConfirm)
        val etNewPasswordConfirm = view.findViewById<EditText>(R.id.etNewPasswordConfirm).apply {
            onTextChanged {
                tilNewPasswordConfirm.error = if (etNewPassword.text.toString() != etNewPasswordConfirm.text.toString()) {
                    getString(R.string.auth_error_password_do_not_match)
                } else {
                    null
                }
            }
        }

        val emptyFieldError = getString(R.string.profile_edit_empty_field_error)
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.profile_password_change)
                .setView(view)
                .setPositiveButton(R.string.ok, null)
                .create()
                .apply {
                    setOnShowListener {
                        getButton(AlertDialog.BUTTON_POSITIVE).apply {
                            setOnClickListener {
                                var hasError = false
                                val oldPassword = etOldPassword.text?.toString()
                                val newPassword = etNewPassword.text?.toString()
                                val newPasswordConfirm = etNewPasswordConfirm.text?.toString()

                                if (oldPassword.isNullOrEmpty()) {
                                    tilOldPassword.error = emptyFieldError
                                    hasError = true
                                }

                                if (newPassword != newPasswordConfirm) {
                                    tilNewPasswordConfirm.error = getString(R.string.auth_error_password_do_not_match)
                                    hasError = true
                                } else {
                                    if (newPassword.isNullOrEmpty()) {
                                        tilNewPassword.error = emptyFieldError
                                        hasError = true
                                    }
                                    if (newPasswordConfirm.isNullOrEmpty()) {
                                        tilNewPasswordConfirm.error = emptyFieldError
                                        hasError = true
                                    }
                                }

                                if (!hasError && oldPassword != null && newPassword != null && newPasswordConfirm != null) {
                                    presenter.onChangePasswordClickConfirm(oldPassword, newPassword, newPasswordConfirm)
                                    dismiss()
                                }
                            }
                        }
                    }
                }
                .show()
    }

    override fun showPasswordChangeComplete() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.profile_password_change)
                .setMessage(R.string.profile_password_change_complete)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .show()
    }


    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
                ?: title, Toast.LENGTH_SHORT).show()
    }

    override fun setEducationData(user: User) {
        val education = user.education ?: emptyList()
        adapter.update(listOf(ProfileDataEducationEditGroup(
                user.user_education,
                education,
                { presenter.onSaveClick(it) },
                { presenter.onCancelClick() }
        )))
    }

    override fun layout() = R.layout.fragment_user
}