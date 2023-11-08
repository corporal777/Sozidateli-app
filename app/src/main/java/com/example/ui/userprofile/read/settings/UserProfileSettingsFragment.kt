package com.example.ui.userprofile.read.settings

import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import android.view.ViewGroup
import androidx.core.text.toSpannable
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.databinding.FragmentUserProfileSettingsBinding
import com.example.extensions.parsePhone
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.profile.shortName.ChangeShortNameFragment
import com.example.ui.userprofile.read.settings.change_email.ChangeEmailFragment
import com.example.ui.userprofile.read.settings.change_name.ChangeNameFragment
import com.example.ui.userprofile.read.settings.change_password.ChangePasswordFragment
import com.example.ui.userprofile.read.settings.change_phone.ChangePhoneFragment
import com.example.ui.userprofile.read.settings.confirm_phone_email.ConfirmEmailPhoneFragment
import com.example.ui.views.CustomCheckView
import com.example.ui.views.dialogs.MessageDialogWithBrownButton
import com.example.ui.views.dialogs.TitleMessageDialog
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.PHONE_PERSONAL
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileSettingsFragment :
    BaseFragment<FragmentUserProfileSettingsBinding>(canShowAnim = true),
    UserProfileSettingsContract.View, ToolbarFragment {

    private lateinit var mUser: UserDetail

    @InjectPresenter
    lateinit var presenter: UserProfileSettingsPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileSettingsPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileSettingsPresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnPreDraw { startPostponedEnterTransition() }
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
                if (mUser.state?.isHidden.toBoolean())
                    presenter.onChangePrivacyConfirm(false, ivPrivacyProfile)
                else presenter.onChangePrivacyConfirm(true, ivPrivacyProfile)
            }

            ivBlockProject.setOnClickListener {
                showBlockingInfoDialog(
                    mUser.blockedNotifications?.projects ?: false,
                    getString(R.string.block_notification_project_title),
                    getString(R.string.block_notification_project_message)
                ) {
                    presenter.onBlockProjectNotificationsClick(it, ivBlockProject)
                }
            }
            ivBlockEvent.setOnClickListener {
                showBlockingInfoDialog(
                    mUser.blockedNotifications?.event ?: false,
                    getString(R.string.block_notification_event_title),
                    getString(R.string.block_notification_event_message)
                ) { state ->
                    presenter.onBlockEventNotificationsClick(state, ivBlockEvent)
                }
            }
            ivBlockOrg.setOnClickListener {
                showBlockingInfoDialog(
                    mUser.blockedNotifications?.organizations ?: false,
                    getString(R.string.block_notification_org_title),
                    getString(R.string.block_notification_org_message)
                ) { state ->
                    presenter.onBlockOrganizationNotificationsClick(state, ivBlockOrg)
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

            if (user.middleName?.absent == true || (user.middleName?.value == "-" || user.middleName?.value.isNullOrBlank())) {
                scNoMiddleName.isChecked = true
                tvUserMiddleName.text = ""
            } else {
                scNoMiddleName.isChecked = false
                tvUserMiddleName.text =
                    user.getMiddleName() ?: getString(R.string.user_profile_additional_hint)
            }

            tvPhoneMobile.apply {
                val phone =
                    user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.value?.parsePhone(
                        requireContext()
                    )
                text = phone
            }
            tvShortname.apply {
                text = if (user.shortName.isNullOrEmpty() || user.shortName == user.id.toString()) {
                    "@id" + user.id
                } else "@" + user.shortName
            }

            tvEmail.text = if (!user.email?.onConfirmation.isNullOrEmpty()) {
                user.email?.onConfirmation
            } else {
                user.email?.value
            }

            ivPrivacyProfile.setChecked(user.state?.isHidden.toBoolean())
            ivBlockEvent.setChecked(user.blockedNotifications?.event ?: false)
            ivBlockProject.setChecked(user.blockedNotifications?.projects ?: false)
            ivBlockOrg.setChecked(user.blockedNotifications?.organizations ?: false)


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
                    presenter.onShowEmailConfirm(
                        user.email?.onConfirmation ?: user.email?.value ?: ""
                    )
                }.setNegativeSelectCallback {
                    if (user.email?.value == null) presenter.onDeleteEmail()
                    else presenter.onDeleteConfirmEmail(user.email?.onConfirmation ?: "")
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

    override fun showChangeEmail(email: String?) {
        val changeEmailDialog = ChangeEmailFragment(email)
        changeEmailDialog.show(requireActivity().supportFragmentManager, "change_email_settings")
    }

    override fun showChangePassword() {
        val changePasswordDialog = ChangePasswordFragment(false)
        changePasswordDialog.show(
            requireActivity().supportFragmentManager,
            "change_password_settings"
        )
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

    override fun showBlockingLoading(show: Boolean, checkView: CustomCheckView) {
        checkView.showProgressLoading(show)
    }

    override fun layout() = R.layout.fragment_user_profile_settings
    override val title: CharSequence by lazy { getString(R.string.profile_settings) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: (value: Int) -> Unit) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}

