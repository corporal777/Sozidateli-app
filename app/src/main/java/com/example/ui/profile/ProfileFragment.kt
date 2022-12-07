package com.example.ui.profile

import android.content.Intent
import android.content.Intent.*
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import coil.transform.RoundedCornersTransformation
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.data.models.MyEventsFilter
import com.example.data.models.UserDetail
import com.example.databinding.FragmentProfileBinding
import com.example.extensions.dp
import com.example.ui.accountChange.data.AuthType
import com.example.ui.base.BaseFragmentNew
import com.example.ui.main.MainActivity
import com.example.ui.profile.data.ProfileDataFragment
import com.example.ui.profile.shortName.ChangeShortNameFragment
import com.example.ui.userprofile.read.settings.confirm_phone_email.ConfirmEmailPhoneFragment
import com.example.ui.views.*
import com.example.ui.views.expandableTextView.CustomTypefaceSpan
import com.example.ui.views.toolbar.SimpleTitleToolbar
import com.example.util.Utils
import com.example.util.copyTextToBuffer
import com.example.util.firstLetterToUppercase
import com.example.util.setImage
import onScrolled
import javax.inject.Inject
import javax.inject.Provider


class ProfileFragment : BaseFragmentNew<FragmentProfileBinding>(), ProfileContract.View,
    SimpleTitleToolbar {

    private var isShowPopup = false
    private lateinit var dialog: AddPhoneEmailDialog

    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    @Inject
    lateinit var presenterProvider: Provider<ProfilePresenter>


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
            profileScrollView.onScrolled { scrollY, oldScrollY, _, _ ->
                presenter.changeScrollingOffset(scrollY - oldScrollY)
            }
            //tvBanned.setOnClickListener { presenter.onBannedClick() }
            tvSettings.setOnClickListener { presenter.onSettingsClick() }
            tvSupport.setOnClickListener { presenter.onSupportClick() }
            tvRate.setOnClickListener { presenter.onRateClick() }
            tvAboutApplication.setOnClickListener { presenter.onAboutApplicationClick() }
            tvLogout.setOnClickListener {
                (requireActivity() as MainActivity).setIgnoreTokenListener(false)
                presenter.onLogoutClick()
            }
            //tvAuthToWebSite.setOnClickListener { presenter.onQrScannerToAuthWebClick() }
            tvSessions.setOnClickListener {
                presenter.onSessionsClick()
            }
            tvChangeAccount.setOnClickListener {
                presenter.onChangeAccountClick()
            }

            btnEditProfile.setOnClickListener { presenter.onProfileClick() }
            tvFavorite.setOnClickListener { presenter.onFavoritesClick() }
        }


        //tvSettings.isVisible = BuildConfig.NEW_PROFILE_EDIT
        //showUserStateDialog()
    }


    override fun setUser(user: UserDetail) {
        mBinding.ivAvatar.apply {
            setImage(
                user.image.uri,
                error = R.drawable.avatar_placeholder_rectangle,
                transformations = listOf(RoundedCornersTransformation(10f.dp))
            )
        }
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
        val userId = getString(R.string.user_id, user.id.toString())
        val toolbarTitle: SpannableStringBuilder
        var shortNameClick: (() -> Unit)? = null
        if (user.id.toString() == user.shortName) {
            val userShortName = SpannableString(getString(R.string.put_user_short_name))
            val font =
                Typeface.createFromAsset(requireContext().assets, "fonts/sf_pro_text_medium.ttf")
            userShortName.setSpan(
                CustomTypefaceSpan("", font),
                0,
                userShortName.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val expandColor = ContextCompat.getColor(requireContext(), R.color.main_brown_color_new)
            userShortName.setSpan(
                ForegroundColorSpan(expandColor),
                0,
                userShortName.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val textSize = resources.getDimensionPixelSize(R.dimen.user_short_name_text_size)
            userShortName.setSpan(
                AbsoluteSizeSpan(textSize),
                0,
                userShortName.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            toolbarTitle = SpannableStringBuilder(userId + "\n").append(userShortName)
            shortNameClick = {
                showChangeUserShortNameDialog(user)
            }
        } else {
            toolbarTitle = SpannableStringBuilder(userId)
        }
        val actionIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_profile_link_edit)
        setToolbarTitleAndIcon(toolbarTitle, actionIcon, {
            val link = BuildConfig.SHARE_URL + "portal/user/" + user.id
            //copyTextToBuffer(requireContext(), link)
            //showToast(getString(R.string.link_is_copied))
            presenter.onShowProfileDataBottomSheetDialog(user, requireContext())
        }, {
            shortNameClick?.invoke()
        })
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
                    showEmailConfirmation(email)
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
                    showPhoneConfirmation(phone)
                }
            }
    }

    private fun showChangeUserShortNameDialog(user: UserDetail) {
        val changeShortNameDialog = ChangeShortNameFragment(user)
        changeShortNameDialog.show(requireActivity().supportFragmentManager, "change_short_name")
        changeShortNameDialog.getUpdatedUserShortName {
            setUserLink(it)
        }
    }

    override fun showProfileDataBottomSheetDialog(user: UserDetail) {
        val profileDataDialog = ProfileDataFragment(
            user.id,
            user.nameLastName,
            user.image.uri,
            user.qrCodeLink,
        )
        profileDataDialog.show(requireActivity().supportFragmentManager, "profile_data_dialog")
    }


    override fun showPhoneConfirmation(phone: String) {
        dialog.hideDialog()
        val confirmPhone = ConfirmEmailPhoneFragment(phone)
        confirmPhone.show(requireActivity().supportFragmentManager, "confirm_phone")
        confirmPhone.setConfirmCallback {
            presenter.onPhoneConfirmed(phone)
        }
    }

    override fun showEmailConfirmation(email: String) {
        dialog.hideDialog()
        val confirmEmail = ConfirmEmailPhoneFragment(email)
        confirmEmail.show(requireActivity().supportFragmentManager, "confirm_email")
        confirmEmail.setConfirmCallback {
            presenter.onEmailConfirmed(email)
        }
    }

    override fun showQrScannerToAuthWebSite() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToQrScannerAuthWebsiteFragment())
    }

    override fun codeSuccess() {
        showUserStateDialog()
    }

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

    override fun showEvents() {
        findNavController().navigate(ProfileFragmentDirections.profileToMyEvents(MyEventsFilter.NONE))
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
        val intent = Intent(ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
        intent.putExtra(EXTRA_SUBJECT, getString(R.string.support_email_title))
        intent.putExtra(EXTRA_TEXT, buildEmailText(uid))
//        if (intent.resolveActivity(requireContext().packageManager) != null) {
//            startActivity(intent)
//        }
        startActivity(intent)
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

}
