package com.example.ui.userprofile.read.settings

import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.data.models.UserEditDataType
import com.example.databinding.FragmentUserProfileSettingsBinding
import com.example.extensions.parsePhone
import com.example.extensions.showChangeEmailCompleteDialog
import com.example.ui.base.BaseFragmentNew
import com.example.ui.main.MainActivity
import com.example.ui.profile.shortName.ChangeShortNameFragment
import com.example.ui.userprofile.read.settings.change_email.ChangeEmailFragment
import com.example.ui.userprofile.read.settings.change_password.ChangePasswordFragment
import com.example.ui.userprofile.read.settings.change_phone.ChangePhoneFragment
import com.example.ui.userprofile.read.settings.confirm_phone_email.ConfirmEmailPhoneFragment
import com.example.ui.views.*
import com.example.ui.views.dialogs_new.MessageDialogWithBrownButton
import com.example.ui.views.dialogs_new.TitleMessageDialog
import com.example.ui.views.toolbar.SimpleTitleToolbar
import com.example.util.PHONE_PERSONAL
import onScrolled
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileSettingsFragment : BaseFragmentNew<FragmentUserProfileSettingsBinding>(),
    UserProfileSettingsContract.View,
    SimpleTitleToolbar {

    private lateinit var mUser: UserDetail

    @InjectPresenter
    lateinit var presenter: UserProfileSettingsPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileSettingsPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileSettingsPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitleAndIcon(getString(R.string.profile_settings))
        mBinding.apply {
            nestedScrollView.onScrolled { scrollY, oldScrollY, _, _ ->
                presenter.changeScrollingOffset(scrollY - oldScrollY)
            }

            ivInfoName.setOnClickListener {
                showDisabledMainInputInfo()
            }
            ivInfoLastName.setOnClickListener {
                showDisabledMainInputInfo()
            }
            ivInfoMiddleName.setOnClickListener {
                showDisabledMainInputInfo()
            }
            tvEditPhone.setOnClickListener(presenter::onChangePhoneClick)
            tvEditPassword.setOnClickListener(presenter::onChangePasswordClick)
            btnDeleteProfile.setOnClickListener(presenter::onDeleteProfileClick)
            tvEditEmail.setOnClickListener(presenter::onChangeEmailClick)
            tvEditShortName.setOnClickListener(presenter::showChangeShortNameClick)

            ivPrivacyProfile.setOnClickListener {
                if (mUser.state?.isHidden.toBoolean()) {
                    presenter.onChangePrivacyConfirm(false)
                } else {
                    presenter.onChangePrivacyConfirm(true)
                }

            }

            ivBlockProject.setOnClickListener {
                showBlockingInfoDialog(
                    mUser.blockedNotifications?.projects ?: false,
                    getString(R.string.block_notification_project_title),
                    getString(R.string.block_notification_project_message)
                ) {
                    presenter.onBlockProjectNotificationsClick(it)
                }
            }
            ivBlockEvent.setOnClickListener {
                showBlockingInfoDialog(
                    mUser.blockedNotifications?.event ?: false,
                    getString(R.string.block_notification_event_title),
                    getString(R.string.block_notification_event_message)
                ) { state ->
                    presenter.onBlockEventNotificationsClick(state)
                }
            }
            ivBlockOrg.setOnClickListener {
                showBlockingInfoDialog(
                    mUser.blockedNotifications?.organizations ?: false,
                    getString(R.string.block_notification_org_title),
                    getString(R.string.block_notification_org_message)
                ) { state ->
                    presenter.onBlockOrganizationNotificationsClick(state)
                }
            }
        }

    }

    override fun onUserUpdated(user: UserDetail?, state: String) {
        user ?: return
        mUser = user
        mBinding.apply {
            tvUserName.text = user.name
            tvUserLastName.text = user.lastName
            tvUserMiddleName.text = user.getMiddleName()

            ivNoMiddleName.apply {
                isEnabled = false
                if (user.middleName?.absent == true || user.middleName?.value == "-") setImage(true)
                else setImage(false)
            }

            val phone =
                user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.value?.parsePhone(
                    requireContext()
                )

            tvPhoneMobile.text = phone
            if (user.shortName == user.id.toString()) {
                tvShortname.text = "@id" + user.shortName
            } else {
                tvShortname.text = "@" + user.shortName
            }

            tvEmail.text = if (!user.email?.onConfirmation.isNullOrEmpty()) {
                user.email?.onConfirmation
            } else {
                user.email?.value
            }

            ivPrivacyProfile.setImage(user.state?.isHidden.toBoolean())
            ivBlockEvent.setImage(user.blockedNotifications?.event ?: false)
            ivBlockProject.setImage(user.blockedNotifications?.projects ?: false)
            ivBlockOrg.setImage(user.blockedNotifications?.organizations ?: false)


            ivInfo.isVisible =
                !user.email?.onConfirmation.isNullOrEmpty() || user.email?.isConfirmed == false

            ivInfo.setOnClickListener {
                TitleMessageDialog(
                    requireContext(),
                    title = getString(R.string.wait_for_accept_title),
                    message = getString(R.string.wait_for_accept_text),
                    btnPositiveText = getString(R.string.wait_for_accept_positive_button),
                    btnNegativeText = getString(R.string.content_description_delete),
                    canShowCancel = true
                ).setPositiveSelectCallback {
                    showEmailConfirmation(user.email?.onConfirmation ?: user.email?.value ?: "")
                }.setNegativeSelectCallback {
                    if (user.email?.value == null) {
                        presenter.onDeleteEmail()
                    } else {
                        presenter.onDeleteConfirmEmail(user.email?.onConfirmation ?: "")
                    }
                }
            }
        }
    }


    private fun showDisabledMainInputInfo() {
        val supportEmail = requireContext().getString(R.string.support_email)
        val message =
            requireContext().getString(R.string.profile_edit_name_disabled_message)
                .format(supportEmail)
                .toSpannable()
        Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES)
        MessageDialogWithBrownButton(requireContext(), message)
    }

    override fun showChangeEmail() {
        val changeEmailDialog = ChangeEmailFragment("")
        changeEmailDialog.show(requireActivity().supportFragmentManager, "change_email_settings")
    }

    override fun showNewChangeEmail(email: String) {
        val changeEmailDialog = ChangeEmailFragment(email)
        changeEmailDialog.show(requireActivity().supportFragmentManager, "change_email_settings")
    }


    override fun showUpdateError(message: String?) {
        val title = getString(R.string.profile_edit_request_error)
        Toast.makeText(requireContext(), message?.let { "$title: $it" }
            ?: title, Toast.LENGTH_SHORT).show()
    }

    override fun showChangePassword() {
        val changePasswordDialog = ChangePasswordFragment(false, "")
        changePasswordDialog.show(
            requireActivity().supportFragmentManager,
            "change_password_settings"
        )
    }

    override fun showChangeShortName(user: UserDetail) {
        val changeShortNameDialog = ChangeShortNameFragment(user)
        changeShortNameDialog.show(
            requireActivity().supportFragmentManager,
            "change_short_name_settings"
        )
    }

    override fun showPhoneEdit(phone: FieldDetails?) {
        val changePhoneDialog = ChangePhoneFragment(phone)
        changePhoneDialog.show(
            requireActivity().supportFragmentManager,
            "change_phone_settings"
        )
    }

    override fun showEmailConfirmation(email: String) {
        val confirmEmailDialog = ConfirmEmailPhoneFragment(email)
        confirmEmailDialog.show(
            requireActivity().supportFragmentManager,
            "confirm_email_settings"
        )
        confirmEmailDialog.setConfirmCallback {
            presenter.updateEmail(email)
        }

    }

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
        }.show()
    }

    override fun showDeleteProfile() {
        TitleMessageDialog(
            requireContext(),
            getString(R.string.user_profile_delete_confirm_title),
            getString(R.string.user_profile_delete_confirm_message),
            getString(R.string.remove),
            getString(R.string.revoke),
            false
        ).setPositiveSelectCallback {
            presenter.onDeleteProfileConfirm()
        }
    }


    private fun showBlockingInfoDialog(
        status: Boolean,
        title: String,
        message: String,
        onAction: (state: Boolean) -> Unit
    ) {
        if (!status) {
            TitleMessageDialog(
                requireContext(),
                title,
                message
            ).setPositiveSelectCallback {
                onAction.invoke(true)
            }.setNegativeSelectCallback {
                onAction.invoke(false)
            }
        } else {
            onAction.invoke(false)
        }

    }

    override fun layout() = R.layout.fragment_user_profile_settings
}

