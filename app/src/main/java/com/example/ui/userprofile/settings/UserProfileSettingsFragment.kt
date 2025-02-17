package com.example.ui.userprofile.settings

import android.os.Bundle
import android.text.util.Linkify
import android.view.View
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
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.userprofile.common.name.ChangeNameFragment
import com.example.ui.userprofile.common.name.ChangeNameFragment.Companion.CHANGE_NAME_FRAGMENT_TAG
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.util.Utils
import com.example.util.setLeftDrawable
import com.example.util.setRightDrawable
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class UserProfileSettingsFragment : BaseToolbarFragment<FragmentUserProfileSettingsBinding>(),
    UserProfileSettingsContract.View {

    @InjectPresenter
    lateinit var presenter: UserProfileSettingsPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileSettingsPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileSettingsPresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startPostponedEnterTransition()
        mBinding.apply {
            tvEditName.setOnClickListener {
                if (presenter.getUserData().state?.nameEdited == true) showDisabledMainInputInfo()
                else presenter.showChangeNameClick()
            }
            tvEditPhone.setOnClickListener(presenter::onChangePhoneClick)
            tvEditEmail.setOnClickListener(presenter::onChangeEmailClick)
            tvEditPassword.setOnClickListener(presenter::onChangePasswordClick)
            //tvEditShortName.setOnClickListener(presenter::showChangeShortNameClick)

            btnDeleteProfile.setOnClickListener(presenter::onDeleteProfileClick)
            btnCreatePassword.setOnClickListener { showCreatePassword() }

            ivPrivacyProfile.setOnClickListener {
                if (presenter.user.state?.isHidden.toBoolean())
                    presenter.onChangePrivacyConfirm(false)
                else presenter.onChangePrivacyConfirm(true)
            }

            ivBlockProject.setOnClickListener {
                showBlockingInfoDialog(
                    presenter.user.blockedNotifications?.projects ?: false,
                    getString(R.string.block_notification_project_title),
                    getString(R.string.block_notification_project_message)
                ) { presenter.onBlockProjectNotificationsClick(it) }
            }
            ivBlockEvent.setOnClickListener {
                showBlockingInfoDialog(
                    presenter.user.blockedNotifications?.event ?: false,
                    getString(R.string.block_notification_event_title),
                    getString(R.string.block_notification_event_message)
                ) { state -> presenter.onBlockEventNotificationsClick(state) }
            }
            ivBlockOrg.setOnClickListener {
                showBlockingInfoDialog(
                    presenter.user.blockedNotifications?.organizations ?: false,
                    getString(R.string.block_notification_org_title),
                    getString(R.string.block_notification_org_message)
                ) { state -> presenter.onBlockOrganizationNotificationsClick(state) }
            }

            btnLinkVk.setOnClickListener {
                if (presenter.user.getVkBinds() != null)
                    showUnbindAccountDialog { presenter.onUnbindVkAccount() }
                else presenter.onBindVkAccount(requireContext(), null)
            }

        }
    }

    override fun setUserData(user: UserDetail) {
        mBinding.apply {
            tvUserName.text = user.name
            tvUserLastName.text = user.lastName
            tvUserMiddleName.apply {
                text = user.getMiddleName()
                scNoMiddleName.isChecked = user.getMiddleName().isNullOrEmpty()
            }
            tvPhoneMobile.apply {
                text = Utils.formatMobilePhone(user.personalPhone?.value)
                if (user.personalPhone?.isConfirmed == false) setRightDrawable(0)
                else setRightDrawable(R.drawable.ic_confirmed_phone_icon)
            }
            //tvShortname.setText(user.shortNameFormatted)
            tvEmail.text = user.personalEmail

            ivPrivacyProfile.setChecked(user.state?.isHidden.toBoolean())
            ivBlockEvent.setChecked(user.blockedNotifications?.event ?: false)
            ivBlockProject.setChecked(user.blockedNotifications?.projects ?: false)
            ivBlockOrg.setChecked(user.blockedNotifications?.organizations ?: false)

            tvPassword.apply {
                val isAbsent = user.state?.isEmptyPassword ?: false
                text = if (isAbsent) getString(R.string.you_does_not_have_password) else "●●●●●●●●●"
                compoundDrawablePadding =
                    if (isAbsent) resources.getDimension(R.dimen.password_icon_padding).toInt() else 0.dp
                setLeftDrawable(if (isAbsent) R.drawable.ic_empty_password_icon else 0)

                tvEditPassword.isEnabled = !isAbsent
                btnCreatePassword.isVisible = isAbsent
            }

            btnLinkVk.apply {
                val text =
                    if (user.getVkBinds() == null) getString(R.string.link_account) else getString(R.string.unlink_account)
                setActiveWithIcon(user.getVkBinds() == null, text)
            }
        }
    }


    override fun showAccountAlreadyBoundDialog(snAuth: SnAuth?) {
        DefaultAlertDialog(
            requireContext(),
            null,
            "Данный аккаунт привязан к другому профилю. Перепривязать к вашему аккаунту?",
            "Да",
            "Отменить"
        ).setSelectCallback { presenter.onBindVkAccount(requireContext(), snAuth) }
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

    override fun showChangePassword() {
        findNavController().navigate(R.id.checkPasswordFragment)
    }

    override fun showCreatePassword() {
        findNavController().navigate(R.id.resetPasswordFragment)
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
            DefaultAlertDialog(requireContext(), title, message, "Да", "Нет")
                .setSelectCallback { onAction.invoke(true) }
                .setCancelCallback { onAction.invoke(false) }
        } else onAction.invoke(false)
    }

    override fun showBlockingLoading(show: Boolean) = mBinding.btnLinkVk.showProgressLoading(show)

    override fun animationType(): AnimType = AnimType.AXIS
    override fun binding() = FragmentUserProfileSettingsBinding::class.java
    override fun layout() = R.layout.fragment_user_profile_settings
    override val title: CharSequence by lazy { getString(R.string.profile_settings) }
    override fun scrollingView(): View = mBinding.profileSettingsScrollView
}

