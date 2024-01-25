package com.example.ui.profile

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import coil.transform.RoundedCornersTransformation
import com.example.BuildConfig
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.FragmentProfileBinding
import com.example.extensions.dp
import com.example.extensions.firstLetterToUppercase
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.profile.data.ProfileDataFragment
import com.example.ui.views.*
import com.example.ui.views.toolbar.ToolbarContent
import com.example.ui.views.toolbar.ToolbarIconView
import com.example.util.Utils
import com.example.util.setImage
import com.example.util.setLeftDrawableWithIntrinsicBounds
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider


class ProfileFragment : BaseFragment<FragmentProfileBinding>(), ToolbarFragment,
    ProfileContract.View {

    private var isShowPopup = false
    private lateinit var dialog: AddPhoneEmailDialog

    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    @Inject
    lateinit var presenterProvider: Provider<ProfilePresenter>

    private lateinit var toolbarContent: ToolbarContent


    @ProvidePresenter
    fun providePresenter(): ProfilePresenter = presenterProvider.get().apply {
        isShowPopup = try {
            val args = ProfileFragmentArgs.fromBundle(requireArguments())
            args.isShowPopup
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
            ivAvatar.setImage(
                image = user.loadUserImage() ?: R.drawable.avatar_placeholder_rectangle,
                transformations = listOf(RoundedCornersTransformation(10f.dp))
            )
            tvName.text = user.nameLastName
        }

        if (isShowPopup && !::dialog.isInitialized) {
            dialog = AddPhoneEmailDialog(
                requireContext(),
                if (user.email?.value != null) RegisterDataType.PHONE else RegisterDataType.EMAIL
            )
                .setSelectCallback {
                    when (it.type) {
                        RegisterDataType.PHONE -> {
                            presenter.checkPhoneIsUnique(
                                true,
                                Utils.validatePhoneBeforeSend(it.value)
                            )
                        }

                        RegisterDataType.EMAIL -> {
                            presenter.checkEmailIsUnique(true, it.value)
                        }
                    }
                }.setNegativeClickCallback { showUserStateDialog() }
        }
    }

    override fun setUserState(hasBase: Boolean, hasMax: Boolean) {
        val newState =
            if (!hasBase && !hasMax) getString(R.string.state).firstLetterToUppercase() + " " + getString(
                R.string.state_empty
            )
            else if (hasBase && !hasMax) getString(R.string.state_base).firstLetterToUppercase() + " " + getString(
                R.string.state
            )
            else getString(R.string.state_max).firstLetterToUppercase() + " " + getString(R.string.state)

        mBinding.stateTitle.apply {
            if (!hasBase && !hasMax) setTextColor(getColor(requireContext(), R.color.red_new))
            else setTextColor(getColor(requireContext(), R.color.main_brown_color_new))
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
            CustomSpannableString(getString(R.string.put_user_short_name)).apply {
                setColorSpan(R.color.main_brown_color_new, requireContext())
                setTextSizeSpan(R.dimen.user_short_name_text_size, requireContext())
                setFontSpan("fonts/sf_pro_text_medium.ttf", requireContext())
                setClickSpan(toolbarContent.getToolbarTitleView()) {
                    presenter.onShowChangeUserShortName()
                }
                userShortName = SpannableStringBuilder(userId + "\n").append(this)
            }
        }
        toolbarContent.getToolbarTitleView().apply {
            highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
            movementMethod = LinkMovementMethod.getInstance()
            text = userShortName
        }
    }

    override fun setChangeOrAddNewAccount(sessionsCount: Int) {
        mBinding.tvChangeAccount.apply {
            if (sessionsCount <= 1) {
                text = getString(R.string.add_account_label)
                setLeftDrawableWithIntrinsicBounds(R.drawable.ic_profile_add_account_edit)
            } else {
                text = getString(R.string.change_account_label)
                setLeftDrawableWithIntrinsicBounds(R.drawable.ic_profile_change_account_edit)
            }
        }
    }

    override fun showEmailNotUnique(email: String) {
        ConfirmPhoneDialog(
            requireContext(), getString(R.string.confirm_email_text, email),
            getString(R.string.revoke), getString(R.string.confirm_phone_positive)
        ).setSelectCallback { if (it) presenter.checkEmailIsUnique(false, email) }
    }

    override fun showPhoneNotUnique(phone: String) {
        ConfirmPhoneDialog(
            requireContext(), getString(R.string.confirm_phone_text, phone),
            getString(R.string.revoke), getString(R.string.confirm_phone_positive)
        ).setSelectCallback { if (it) presenter.checkPhoneIsUnique(false, phone) }
    }


    override fun hideAddPhoneEmailDialog() = dialog.hideDialog()

    override fun showPhoneConfirmation(phone: String) {
        findNavController().navigate(
            R.id.phoneCodeConfirmFragment,
            bundleOf("phone" to phone, "fromRegister" to false),
        )
        setFragmentResultListener("confirm") { _, bundle ->
            val emailConfirm = bundle.getString("phone")
            if (!emailConfirm.isNullOrEmpty()) showUserStateDialog()
            clearFragmentResultListener("confirm")
        }
    }

    override fun showEmailConfirmation(email: String) {
        findNavController().navigate(
            R.id.emailCodeConfirmFragment,
            bundleOf("email" to email, "fromRegister" to false),
        )
        setFragmentResultListener("confirm") { _, bundle ->
            val emailConfirm = bundle.getString("email")
            if (!emailConfirm.isNullOrEmpty()) showUserStateDialog()
            clearFragmentResultListener("confirm")
        }
    }

    private fun showUserStateDialog() {
        ChangeStateDialog(requireActivity(), StateType.SUCCESS)
            .setClickCallback {
                if (it == ClickType.INFO) showStates()
            }
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
        try {
            val intent = Intent(ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            intent.putExtra(EXTRA_SUBJECT, getString(R.string.support_email_title))
            intent.putExtra(EXTRA_TEXT, buildEmailText(uid))
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun buildEmailText(uid: String): String {
        val os = "OS: Android"
        val api = "API: ${android.os.Build.VERSION.SDK_INT}"
        val appVersion = "App version: ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
        val userId = "User id: $uid"
        val postfix = "\n---------------\nПожалуйста, опишите проблему ниже.\n\n"
        return listOf(os, api, appVersion, userId).joinToString(separator = "\n", postfix = postfix)
    }

    override fun openPlayMarket() {
        val appPackageName = requireContext().packageName
        try {
            startActivity(Intent(ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
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


    override fun layout() = R.layout.fragment_profile
    override val title: CharSequence = ""
    override fun scrollValue(scroll: Int) {}
    override fun actionIconContainer(view: ViewGroup) {
        view.apply {
            addView(ToolbarIconView(context).apply {
                setImageAsIcon(R.drawable.ic_profile_link_edit)
                setOnClickListener { presenter.onShowUserProfileLink() }
            })
        }
    }

    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        this.toolbarContent = toolbarContent
        this.toolbarContent.getBackButton().isInvisible = true
    }
}
