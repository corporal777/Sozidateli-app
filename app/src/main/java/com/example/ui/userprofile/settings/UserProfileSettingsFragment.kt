package com.example.ui.userprofile.settings

import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.app.databinding.FragmentUserProfileSettingsBinding
import com.example.data.models.SnAuth
import com.example.data.models.UserDetail
import com.example.extensions.dp
import com.example.extensions.setArgument
import com.example.extensions.setOnClickListener
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseVBFragment
import com.example.ui.userprofile.common.name.ChangeNameFragment
import com.example.ui.userprofile.common.name.ChangeNameFragment.Companion.CHANGE_NAME_FRAGMENT_TAG
import com.example.ui.views.CustomCheckView
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.ui.views.dialogs.TitleMessageDialog
import com.example.ui.views.loading.CustomCircleLoadingButton
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.setLeftDrawableWithIntrinsicBounds
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class UserProfileSettingsFragment : BaseVBFragment<FragmentUserProfileSettingsBinding>(),
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
            tvUserMiddleName.setText(user.getMiddleName())
            scNoMiddleName.isChecked = user.getMiddleName().isNullOrEmpty()
            tvPhoneMobile.apply {
                showIcon(user.personalPhone?.isConfirmed ?: false)
                if (user.personalPhone?.value.isNullOrEmpty())
                    setHint(getString(R.string.user_profile_files_hint))
                else setText(user.personalPhone?.value)
            }
            tvShortname.setText(user.shortNameFormatted)
            tvEmail.setText(user.personalEmail)
            ivPrivacyProfile.setChecked(user.state?.isHidden.toBoolean())
            ivBlockEvent.setChecked(user.blockedNotifications?.event ?: false)
            ivBlockProject.setChecked(user.blockedNotifications?.projects ?: false)
            ivBlockOrg.setChecked(user.blockedNotifications?.organizations ?: false)
        }
    }

    override fun setUserPassword(isAbsent: Boolean) {
        mBinding.apply {
            tvPassword.apply {
                text = if (isAbsent) getString(R.string.you_does_not_have_password) else "●●●●●●●●●"
                compoundDrawablePadding =
                    if (isAbsent) resources.getDimension(R.dimen.user_profile_settings_absent_password_icon_padding)
                        .toInt() else 0.dp
                setLeftDrawableWithIntrinsicBounds(if (isAbsent) R.drawable.ic_empty_password_icon else 0)
            }
            tvEditPassword.isEnabled = !isAbsent
            btnCreatePassword.apply {
                isVisible = isAbsent
                setOnClickListener { showChangePassword(true) }
            }
        }
    }

    override fun setUserSocialBinds(user: UserDetail) {
        mBinding.apply {
            btnLinkVk.apply {
                val text =
                    if (user.getVkontakteBinds() == null) getString(R.string.link_account) else getString(
                        R.string.unlink_account
                    )
                setActiveWithIcon(user.getVkontakteBinds() == null, text)
                setOnClickListener {
                    if (user.getVkontakteBinds() != null)
                        showUnbindAccountDialog { presenter.onUnbindVkAccount(this) }
                    else presenter.onBindVkAccount(requireContext(), this, null)
                }
            }
        }
    }

    override fun showAccountAlreadyBoundDialog(view: ViewGroup, snAuth: SnAuth?) {
        DefaultAlertDialog(
            requireContext(),
            null,
            "Данный аккаунт привязан к другому профилю. Перепривязать к вашему аккаунту?",
            "Да",
            "Отменить"
        ).setSelectCallback { presenter.onBindVkAccount(requireContext(), view, snAuth) }
    }

    private fun showUnbindAccountDialog(onBind: () -> Unit) {
        DefaultAlertDialog(
            requireContext(),
            "Отменить связь?",
            "После отмены вы не сможете входить в аккаунт этим способом",
            "Отменить",
            "Оставить"
        ).setSelectCallback { onBind.invoke() }
    }

    override fun showChangePassword(isChange: Boolean) {
        findNavController().navigate(
            R.id.changePasswordFragment,
            bundleOf("isChange" to isChange)
        )
    }

    override fun showChangeEmail(email: String?) {
        findNavController().navigate(
            R.id.changeEmailFragment,
            bundleOf("email" to email)
        )
    }

    override fun showChangePhone() {
        findNavController().navigate(R.id.changePhoneFragment)
    }

    override fun showChangeName(user: UserDetail) {
        ChangeNameFragment()
            .setArgument<ChangeNameFragment>(CHANGE_NAME_FRAGMENT_TAG, user)
            .show(requireActivity().supportFragmentManager)
    }

    override fun showChangeShortName(user: UserDetail) {
        findNavController().navigate(R.id.changeShortNameFragment)
    }

    override fun showEmailConfirmation(email: String) {
        findNavController().navigate(
            R.id.emailCodeConfirmFragment,
            bundleOf("email" to email, "fromRegister" to false),
        )
    }

    private fun showDisabledMainInputInfo() {
        val supportEmail = requireContext().getString(R.string.support_email)
        val message =
            requireContext().getString(R.string.profile_edit_name_disabled_message)
                .format(supportEmail)
                .toSpannable()
        Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES)
        DefaultAlertDialog(requireContext(), null, message)
    }

    override fun showDeleteProfile() {
        DefaultAlertDialog(
            requireContext(),
            getString(R.string.user_profile_delete_confirm_title),
            getString(R.string.user_profile_delete_confirm_message),
            getString(R.string.remove),
            getString(R.string.revoke)
        ).setSelectCallback { presenter.onDeleteProfileConfirm() }
    }


    private fun showBlockingInfoDialog(
        status: Boolean,
        title: String,
        message: String,
        onAction: (state: Boolean) -> Unit
    ) {
        if (!status) {
            DefaultAlertDialog(
                requireContext(),
                title,
                message,
                getString(R.string.yes),
                getString(R.string.no)
            ).setSelectCallback { onAction.invoke(true) }
                .setCancelCallback { onAction.invoke(false) }
        } else onAction.invoke(false)
    }

    private fun showEmailInformation(user: UserDetail) {
        TitleMessageDialog(
            requireContext(),
            title = getString(R.string.wait_for_accept_title),
            message = getString(R.string.wait_for_accept_text),
            btnPositiveText = getString(R.string.wait_for_accept_positive_button),
            btnNegativeText = getString(R.string.content_description_delete),
            canShowCancel = true
        ).setPositiveSelectCallback {
            showEmailConfirmation(user.personalEmail ?: "")
        }.setNegativeSelectCallback {
            if (user.email?.value == null) presenter.onDeleteEmail()
            else presenter.onDeleteConfirmEmail(user.email?.onConfirmation ?: "")
        }
    }

    override fun showBlockingLoading(show: Boolean, customView: ViewGroup) {
        when (customView) {
            is CustomCheckView -> customView.showProgressLoading(show)
            is CustomCircleLoadingButton -> customView.showProgressLoading(show)
            else -> return
        }
    }

    override fun animationType(): AnimType = AnimType.AXIS
    override fun binding() = FragmentUserProfileSettingsBinding::class.java
    override fun layout() = R.layout.fragment_user_profile_settings
    override val title: CharSequence by lazy { getString(R.string.profile_settings) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}

