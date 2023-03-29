package com.example.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.*
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import coil.transform.RoundedCornersTransformation
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.FragmentProfileBinding
import com.example.extensions.dp
import com.example.interfaces.ToolbarFragment
import com.example.ui.accountChange.data.AuthType
import com.example.ui.base.BaseFragmentNew
import com.example.ui.profile.data.ProfileDataFragment
import com.example.ui.profile.shortName.ChangeShortNameFragment
import com.example.ui.userprofile.read.settings.confirm_phone_email.ConfirmEmailPhoneFragment
import com.example.ui.views.*
import com.example.ui.views.expandableTextView.CustomTypefaceSpan
import com.example.ui.views.toolbar.ToolbarContent
import com.example.ui.views.toolbar.ToolbarIconView
import com.example.util.ClickableSpanNew
import com.example.util.Utils
import com.example.util.firstLetterToUppercase
import com.example.util.setImage
import com.shakebugs.shake.Shake
import com.shakebugs.shake.ShakeScreen
import onScrolled
import javax.inject.Inject
import javax.inject.Provider


class ProfileFragment : BaseFragmentNew<FragmentProfileBinding>(), ToolbarFragment,
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
//            profileScrollView.onScrolled { scrollY, oldScrollY, _, _ ->
//                presenter.changeScrollingOffset(scrollY - oldScrollY)
//            }
            //tvBanned.setOnClickListener { presenter.onBannedClick() }
            tvSettings.setOnClickListener { presenter.onSettingsClick() }
            tvSupport.setOnClickListener { presenter.onSupportClick() }
            tvProblem.setOnClickListener { Shake.show(ShakeScreen.HOME) }
            tvRate.setOnClickListener { presenter.onRateClick() }
            tvAboutApplication.setOnClickListener { presenter.onAboutApplicationClick() }
            tvLogout.setOnClickListener { presenter.onLogoutClick() }
            tvSessions.setOnClickListener { presenter.onSessionsClick() }
            tvChangeAccount.setOnClickListener { presenter.onChangeAccountClick() }
            btnEditProfile.setOnClickListener { presenter.onProfileClick() }
            tvFavorite.setOnClickListener { presenter.onFavoritesClick() }
            tvScan.setOnClickListener { presenter.onQrScannerToAuthWebClick() }
        }


        //tvSettings.isVisible = BuildConfig.NEW_PROFILE_EDIT
        //showUserStateDialog()
    }


    override fun setUser(user: UserDetail) {
        mBinding.ivAvatar.setImage(
            image = user.image.uri ?: R.drawable.avatar_placeholder_rectangle,
            transformations = listOf(RoundedCornersTransformation(10f.dp))
        )
        mBinding.tvName.text = user.nameLastName

        if (isShowPopup && !::dialog.isInitialized) {
            dialog = AddPhoneEmailDialog(
                requireActivity(),
                if (user.email?.value != null) RegisterDataType.PHONE else RegisterDataType.EMAIL
            )
                .setSelectCallback {
                    when (it.type) {
                        RegisterDataType.PHONE -> {
                            presenter.checkPhoneIsUnique(Utils.validatePhoneBeforeSend(it.value))
                        }
                        RegisterDataType.EMAIL -> {
                            presenter.checkEmailIsUnique(it.value)
                        }
                    }
                }.setNegativeClickCallback { showUserStateDialog() }
        }
    }

    override fun setUserState(hasBase: Boolean, hasMax: Boolean) {
        val newState = if (!hasBase && !hasMax) {
            getString(R.string.state).firstLetterToUppercase() + " " + getString(R.string.state_empty)
        } else if (hasBase && !hasMax) {
            getString(R.string.state_base).firstLetterToUppercase() + " " + getString(R.string.state)
        } else {
            getString(R.string.state_max).firstLetterToUppercase() + " " + getString(R.string.state)
        }
        mBinding.stateTitle.apply {
            text = newState
            setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToUserStateFragment())
            }
        }
    }

    override fun setUserLink(user: UserDetail) {
        var userShortName = SpannableStringBuilder()
        if (!user.shortName.isNullOrEmpty() && user.id.toString() != user.shortName) {
            userShortName = SpannableStringBuilder(user.shortName)
        } else {
            val userId = getString(R.string.user_id, user.id.toString())
            SpannableString(getString(R.string.put_user_short_name)).apply {
                val font = Typeface.createFromAsset(requireContext().assets, "fonts/sf_pro_text_medium.ttf")
                setSpan(CustomTypefaceSpan("", font), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                val expandColor = ContextCompat.getColor(requireContext(), R.color.main_brown_color_new)
                setSpan(ForegroundColorSpan(expandColor), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                val textSize = resources.getDimensionPixelSize(R.dimen.user_short_name_text_size)
                setSpan(AbsoluteSizeSpan(textSize), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                val clickableSpan = ClickableSpanNew(toolbarContent.getToolbarTitleView()) {
                    presenter.onShowChangeUserShortName()
                }
                setSpan(clickableSpan, 0, length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                userShortName = SpannableStringBuilder(userId + "\n").append(this)
            }
        }
        toolbarContent.getToolbarTitleView().apply {
            highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
            movementMethod = LinkMovementMethod.getInstance()
            text = userShortName
        }
    }


    override fun showShimmerView() {
        mBinding.apply {
            shimmerView.isVisible = true
            clHeader.isVisible = false
        }
    }

    override fun hideShimmerView() {
        mBinding.apply {
            shimmerView.isVisible = false
            clHeader.isVisible = true
        }
    }

    override fun setChangeOrAddNewAccount(description: Int, icon: Int) {
        mBinding.apply {
            tvChangeAccount.text = getString(description)
            tvChangeAccount.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        }
    }

    override fun showEmailNotUnique(email: String) {
        ConfirmPhoneDialog(
            requireContext(), getString(R.string.confirm_email_text, email),
            getString(R.string.revoke), getString(R.string.confirm_phone_positive)
        )
            .setSelectCallback {
                if (it) {
                    presenter.onShowEmailConfirm(email)
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
                    presenter.onShowPhoneConfirm(phone)
                }
            }
    }

    override fun showChangeUserShortNameDialog(user: UserDetail) {
        val changeShortNameDialog = ChangeShortNameFragment(user.id, user.shortName)
        changeShortNameDialog.show(requireActivity().supportFragmentManager, "change_short_name")
        changeShortNameDialog.getUpdatedUserShortName {
            setUserLink(it)
        }
    }

    override fun showUserProfileLinkDialog(user: UserDetail) {
        val profileDataDialog = ProfileDataFragment(
            user.id,
            user.nameLastName,
            user.image.uri,
            user.qrCodeLink,
            user.shortName
        )
        profileDataDialog.show(requireActivity().supportFragmentManager, "profile_data_dialog")
    }

    override fun hideAddPhoneEmailDialog() {
        dialog.hideDialog()
    }


    override fun showPhoneConfirmation(phone: String) {
        val confirmPhone = ConfirmEmailPhoneFragment(phone)
        confirmPhone.show(requireActivity().supportFragmentManager, "confirm_phone")
        confirmPhone.setConfirmCallback {
            presenter.onConfirmPhoneSuccess(phone)
        }
    }

    override fun showEmailConfirmation(email: String) {
        val confirmEmail = ConfirmEmailPhoneFragment(email)
        confirmEmail.show(requireActivity().supportFragmentManager, "confirm_email")
        confirmEmail.setConfirmCallback {
            codeSuccess()
        }
    }

    override fun showQrScannerToAuthWebSite() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToQrScannerAuthWebsiteFragment())
    }

    override fun codeSuccess() = showUserStateDialog()

    override fun showProfile(uid: String) {
        findNavController().navigate(ProfileFragmentDirections.profileToUserProfile())
    }

    override fun showChangeAccount() {
        findNavController().navigate(
            ProfileFragmentDirections.profileToChangeAccount(
                "",
                AuthType.NONE,
                false
            )
        )
    }

    private fun showUserStateDialog() {
        ChangeStateDialog(requireActivity(), StateType.SUCCESS)
            .setClickCallback {
                if (it == ClickType.INFO) {
                    findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToUserStateFragment())
                }
            }
    }

    override fun showFavorites() {
        findNavController().navigate(ProfileFragmentDirections.profileToFavorite())
    }

    override fun showAboutApp() {
        findNavController().navigate(ProfileFragmentDirections.profileToAbout())
    }

    override fun showBanned() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToBannedFragment())
    }

    override fun showSettings() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToUserProfileSettingsFragment())
    }

    override fun showSessions() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToUserSessionsFragment())
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

    override fun layout() = R.layout.fragment_profile

    override val title: CharSequence = ""

    @SuppressLint("RestrictedApi")
    override fun scrollValue(scroll: (value: Int) -> Unit) {
        mBinding.profileScrollView.apply {
            scroll.invoke(computeVerticalScrollOffset())
            onScrolled { _, _, _, _ -> scroll.invoke(computeVerticalScrollOffset()) }
        }
    }

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
    }
}
