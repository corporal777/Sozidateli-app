package com.example.ui.userprofile.settings

import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import android.view.ViewGroup
import androidx.core.text.toSpannable
import com.example.R
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.databinding.FragmentUserProfileSettingsBinding
import com.example.extensions.parsePhone
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.userprofile.edit.shortName.ChangeShortNameFragment
import com.example.ui.userprofile.edit.email.ChangeEmailFragment
import com.example.ui.userprofile.edit.name.ChangeNameFragment
import com.example.ui.userprofile.edit.password.ChangePasswordFragment
import com.example.ui.userprofile.edit.phone.ChangePhoneFragment
import com.example.ui.userprofile.edit.confirm.ConfirmEmailPhoneFragment
import com.example.ui.views.CustomCheckView
import com.example.ui.views.dialogs.MessageDialogWithBrownButton
import com.example.ui.views.dialogs.TitleMessageDialog
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.PHONE_PERSONAL
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileSettingsFragment :
    BaseFragment<FragmentUserProfileSettingsBinding>(canShowAnim = true),
    UserProfileSettingsContract.View, ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: UserProfileSettingsPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileSettingsPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileSettingsPresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            tvEditName.setOnClickListener {
                if (presenter.getUserData().state?.nameEdited == true) showDisabledMainInputInfo()
                else presenter.showChangeNameClick()
            }

            tvEditPhone.setOnClickListener(presenter::onChangePhoneClick)
            tvEditPassword.setOnClickListener(presenter::onChangePasswordClick)
            btnDeleteProfile.setOnClickListener(presenter::onDeleteProfileClick)
            tvEditEmail.setOnClickListener(presenter::onChangeEmailClick)
            tvEditShortName.setOnClickListener(presenter::showChangeShortNameClick)

            ivPrivacyProfile.setOnClickListener {
                if (presenter.user.state?.isHidden.toBoolean())
                    presenter.onChangePrivacyConfirm(false, ivPrivacyProfile)
                else presenter.onChangePrivacyConfirm(true, ivPrivacyProfile)
            }

            ivBlockProject.setOnClickListener {
                showBlockingInfoDialog(
                    presenter.user.blockedNotifications?.projects ?: false,
                    getString(R.string.block_notification_project_title),
                    getString(R.string.block_notification_project_message)
                ) {
                    presenter.onBlockProjectNotificationsClick(it, ivBlockProject)
                }
            }
            ivBlockEvent.setOnClickListener {
                showBlockingInfoDialog(
                    presenter.user.blockedNotifications?.event ?: false,
                    getString(R.string.block_notification_event_title),
                    getString(R.string.block_notification_event_message)
                ) { state ->
                    presenter.onBlockEventNotificationsClick(state, ivBlockEvent)
                }
            }
            ivBlockOrg.setOnClickListener {
                showBlockingInfoDialog(
                    presenter.user.blockedNotifications?.organizations ?: false,
                    getString(R.string.block_notification_org_title),
                    getString(R.string.block_notification_org_message)
                ) { state ->
                    presenter.onBlockOrganizationNotificationsClick(state, ivBlockOrg)
                }
            }
        }

    }

    override fun setUserData(user: UserDetail, state: String) {
        startPostponedEnterTransition()
        mBinding.apply {
            tvUserName.setText(user.name)
            tvUserLastName.setText(user.lastName)
            tvUserMiddleName.apply {
                setText(user.getMiddleName())
                initSwitch(user.middleName?.value == "-" || user.middleName?.value.isNullOrBlank()){}
            }

            tvPhoneMobile.apply {
                val phone = user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.value?.parsePhone(requireContext())
                setText(phone)
            }
            tvPassword.setText("●●●●●●●●●")
            tvShortname.setText(user.shortNameFormatted)

            tvEmail.apply {
                setText(user.getUserEmail())
                setIconVisibility(user.isHasEmailOnConfirmation())
                getInputLayout().setEndIconOnClickListener {
                    showEmailInformation(user)
                }
            }

            ivPrivacyProfile.setChecked(user.state?.isHidden.toBoolean())
            ivBlockEvent.setChecked(user.blockedNotifications?.event ?: false)
            ivBlockProject.setChecked(user.blockedNotifications?.projects ?: false)
            ivBlockOrg.setChecked(user.blockedNotifications?.organizations ?: false)
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

    override fun showChangeEmail(email: String?) {
        val changeEmailDialog = ChangeEmailFragment(email)
        changeEmailDialog.show(requireActivity().supportFragmentManager, "change_email_settings")
    }

    override fun showChangePassword() {
        ChangePasswordFragment(false).show(requireActivity().supportFragmentManager)
    }

    override fun showChangeName(user: UserDetail) {
        val changeNameDialog = ChangeNameFragment(user)
        changeNameDialog.show(
            requireActivity().supportFragmentManager,
            "change_name_settings"
        )
    }

    override fun showChangeShortName(user: UserDetail) {
        val changeShortNameDialog = ChangeShortNameFragment(user.id, user.shortName)
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
            )
                .setPositiveSelectCallback { onAction.invoke(true) }
                .setNegativeSelectCallback { onAction.invoke(false) }
        } else {
            onAction.invoke(false)
        }
    }

    private fun showEmailInformation(user: UserDetail){
        TitleMessageDialog(
            requireContext(),
            title = getString(R.string.wait_for_accept_title),
            message = getString(R.string.wait_for_accept_text),
            btnPositiveText = getString(R.string.wait_for_accept_positive_button),
            btnNegativeText = getString(R.string.content_description_delete),
            canShowCancel = true
        ).setPositiveSelectCallback {
            presenter.onShowEmailConfirm(
                user.email?.onConfirmation ?: user.email?.value ?: ""
            )
        }.setNegativeSelectCallback {
            if (user.email?.value == null) presenter.onDeleteEmail()
            else presenter.onDeleteConfirmEmail(user.email?.onConfirmation ?: "")
        }
    }

    override fun showBlockingLoading(show: Boolean, checkView: CustomCheckView) {
        checkView.showProgressLoading(show)
    }

    override fun layout() = R.layout.fragment_user_profile_settings
    override val title: CharSequence by lazy { getString(R.string.profile_settings) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}

