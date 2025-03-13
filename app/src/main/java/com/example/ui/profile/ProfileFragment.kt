package com.example.ui.profile

import android.content.Intent
import android.content.Intent.ACTION_SENDTO
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_EMAIL
import android.content.Intent.EXTRA_SUBJECT
import android.content.Intent.EXTRA_TEXT
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.app.BuildConfig
import com.example.app.R
import com.example.app.databinding.FragmentProfileBinding
import com.example.data.models.UserDetail
import com.example.extensions.firstLetterToUppercase
import com.example.extensions.startIntent
import com.example.extensions.textColor
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.profile.data.ProfileDataFragment
import com.example.ui.views.dialogs.AddPhoneEmailDialog
import com.example.ui.views.dialogs.ChangeStateBottomDialog
import com.example.ui.views.dialogs.ClickType
import com.example.ui.views.dialogs.ContactsType
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.ui.views.dialogs.StateType
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.getColor
import com.example.util.setLeftDrawable
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider


class ProfileFragment : BaseToolbarFragment<FragmentProfileBinding>(), ProfileContract.View {

    private var isShowPopup = false
    private lateinit var dialog: AddPhoneEmailDialog
    private lateinit var toolbarContent: ToolbarContent
    private val toolbarIconView by lazy {
        createIconView(R.drawable.ic_profile_link, false) { presenter.onShowProfileLink() }
    }

    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    @Inject
    lateinit var presenterProvider: Provider<ProfilePresenter>

    @ProvidePresenter
    fun providePresenter(): ProfilePresenter = presenterProvider.get().apply {
        isShowPopup = try {
            ProfileFragmentArgs.fromBundle(requireArguments()).isShowPopup
        } catch (e: Exception) {
            false
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnEditProfile.setOnClickListener { presenter.onProfileClick() }
            tvSettings.setOnClickListener { presenter.onSettingsClick() }
            tvFavorite.setOnClickListener { presenter.onFavoritesClick() }
            tvScan.setOnClickListener { presenter.onQrScannerToAuthWebClick() }
            tvSessions.setOnClickListener { presenter.onSessionsClick() }
            tvSupport.setOnClickListener { presenter.onSupportClick() }
            tvRate.setOnClickListener { presenter.onRateClick() }
            tvWriteEmail.setOnClickListener { presenter.onWriteEmailClick() }
            tvProblem.setOnClickListener { }
            tvAboutApplication.setOnClickListener { presenter.onAboutApplicationClick() }
            tvChangeAccount.setOnClickListener { presenter.onChangeAccountClick() }
            tvLogout.setOnClickListener { presenter.onLogoutClick() }
        }
    }


    override fun setUser(user: UserDetail) {
        mBinding.apply {
            toolbarIconView.isEnabled = true
            viewAvatar.setImage(user.loadUserImage(), user.avatarIsDefault ?: true)
            tvName.text = user.nameLastName

            tvChangeAccount.apply {
                val sessionsCount = user.binds?.deviceSessionsCount ?: 0
                if (sessionsCount <= 1) {
                    text = getString(R.string.add_account_label)
                    setLeftDrawable(R.drawable.ic_profile_add_account_edit)
                } else {
                    text = getString(R.string.change_account_label)
                    setLeftDrawable(R.drawable.ic_profile_change_account_edit)
                }
            }
        }

        if (isShowPopup && !::dialog.isInitialized) {
            val type = if (user.email?.value.isNullOrEmpty()) ContactsType.EMAIL else ContactsType.PHONE
            dialog = AddPhoneEmailDialog(requireContext(), type)
                .setSelectEmailCallback { presenter.checkEmailIsUnique(true, it) }
                .setSelectPhoneCallback { presenter.checkPhoneIsUnique(true, it) }
                .setNegativeClickCallback { showUserStateDialog() }
        }
    }

    override fun setUserState(hasBase: Boolean, hasMax: Boolean) {
        val newState =
            if (!hasBase && !hasMax) getString(R.string.state_empty_text)
            else if (hasBase && !hasMax) getString(R.string.state_base_text)
            else getString(R.string.state_max_text)

        mBinding.stateTitle.apply {
            textColor = if (!hasBase && !hasMax) R.color.red_new else R.color.main_brown_color_new
            text = newState
            setOnClickListener { showStates() }
        }
    }

    override fun setUserLink(user: UserDetail) {
        var userShortName = SpannableStringBuilder()
        if (!user.shortName.isNullOrEmpty() && user.id.toString() != user.shortName) {
            userShortName = SpannableStringBuilder(user.shortName)
        } else {
            val userId = getString(R.string.user_id, user.id.toString())
//            CustomSpannableString(getString(R.string.put_user_short_name)).apply {
//                setColorSpan(R.color.main_brown_color_new, requireContext())
//                setTextSizeSpan(R.dimen.user_short_name_text_size, requireContext())
//                setFontSpan("fonts/sf_pro_text_medium.ttf", requireContext())
//                setClickSpan(toolbarContent.getToolbarTitleView()) {
//                    presenter.onShowChangeUserShortName()
//                }
//                userShortName = SpannableStringBuilder(userId + "\n").append(this)
//            }
            userShortName = SpannableStringBuilder(userId)
        }
        toolbarContent.getToolbarTitleView().apply {
            highlightColor = getColor(R.color.profile_id_text)
            movementMethod = LinkMovementMethod.getInstance()
            text = userShortName
        }
    }


    override fun showEmailPhoneNotUnique(email: String?, phone: String?) {
        DefaultAlertDialog(
            requireContext(),
            null,
            if (email.isNullOrEmpty()) getString(R.string.confirm_phone_text, phone)
            else getString(R.string.confirm_email_text, email),
            getString(R.string.confirm_phone_positive),
            getString(R.string.event_register_no_form_negative)
        ).setSelectCallback {
            if (email.isNullOrEmpty()) presenter.checkPhoneIsUnique(false, phone!!)
            else presenter.checkEmailIsUnique(false, email)
        }
    }


    override fun hideAddPhoneEmailDialog() = dialog.hideDialog()

    override fun showPhoneConfirmation(phone: String) {
        findNavController().navigate(R.id.phoneCodeConfirmFragment, bundleOf("phone" to phone))
        setFragmentResultListener("confirm") { _, bundle ->
            val emailConfirm = bundle.getString("phone")
            if (!emailConfirm.isNullOrEmpty()) showUserStateDialog()
            clearFragmentResultListener("confirm")
        }
    }

    override fun showEmailConfirmation(email: String) {
        findNavController().navigate(R.id.emailCodeConfirmFragment, bundleOf("email" to email))
        setFragmentResultListener("confirm") { _, bundle ->
            val emailConfirm = bundle.getString("email")
            if (!emailConfirm.isNullOrEmpty()) showUserStateDialog()
            clearFragmentResultListener("confirm")
        }
    }

    private fun showUserStateDialog() = checkIfFragmentAttached {
        ChangeStateBottomDialog(this, StateType.SUCCESS)
            .setClickCallback { if (it == ClickType.INFO) showStates() }
            .show()
    }

    override fun showUserProfileLinkDialog() {
        ProfileDataFragment().show(requireActivity().supportFragmentManager)
    }

    override fun showChangeUserShortName() {
        findNavController().navigate(R.id.changeShortNameFragment)
    }

    override fun showProfile(uid: String) {
        findNavController().navigate(R.id.user_profile_fragment)
    }

    override fun showStates() {
        findNavController().navigate(R.id.userStateFragment)
    }

    override fun showChangeAccount() {
        findNavController().navigate(R.id.change_account_fragment)
    }

    override fun showFavorites() {
        findNavController().navigate(R.id.favorite_fragment)
    }

    override fun showQrScannerToAuthWebSite() {
        findNavController().navigate(R.id.qrScannerToAuthWebSiteFragment)
    }

    override fun showSessions() {
        findNavController().navigate(R.id.user_sessions_fragment)
    }

    override fun showAboutApp() {
        findNavController().navigate(R.id.about_fragment)
    }

    override fun showSettings() {
        findNavController().navigate(R.id.user_profile_settings_fragment)
    }

    override fun showSupport() {
        findNavController().navigate(R.id.supportCenterFragment)
    }


    override fun openSupportEmail(uid: String) {
        val os = "OS: Android" + "\n" + "API: ${android.os.Build.VERSION.SDK_INT}"
        val appVersion = "App version: ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
        val userId = "User id: $uid"
        val postfix = "\n---------------\nПожалуйста, опишите проблему ниже.\n\n"
        val email = listOf(os, appVersion, userId).joinToString("\n", postfix = postfix)
        startIntent(Intent.ACTION_SENDTO){
            data = Uri.parse("mailto:")
            putExtra(EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            putExtra(EXTRA_SUBJECT, getString(R.string.support_email_title))
            putExtra(EXTRA_TEXT, email)
        }
    }


    override fun openPlayMarket() {
        val appPackageName = requireContext().packageName
        try {
            startActivity(Intent(ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (e: android.content.ActivityNotFoundException) {
            val url ="https://play.google.com/store/apps/details?id=$appPackageName"
            startActivity(Intent(ACTION_VIEW, Uri.parse(url)))
        }
    }

    override fun showCustomLoading() {
        mBinding.apply {
            shimmerView.isVisible = true
            clHeader.isVisible = false
        }
    }

    override fun hideCustomLoading() {
        mBinding.apply {
            shimmerView.isVisible = false
            clHeader.isVisible = true
        }
    }


    override fun binding(): Class<FragmentProfileBinding> = FragmentProfileBinding::class.java
    override fun layout() = R.layout.fragment_profile
    override fun scrollingView(): View = mBinding.profileScrollView
    override fun actionIconContainer(view: ViewGroup) { view.addView(toolbarIconView) }
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        this.toolbarContent = toolbarContent
        this.toolbarContent.getBackButton().isInvisible = true
    }
}
