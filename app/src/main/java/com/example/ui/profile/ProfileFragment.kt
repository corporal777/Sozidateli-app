package com.example.ui.profile

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.data.models.MyEventsFilter
import com.example.data.models.UserDetail
import com.example.databinding.FragmentProfileBinding
import com.example.ui.base.BaseFragmentNew
import com.example.ui.main.MainActivity
import com.example.ui.views.*
import com.example.ui.views.toolbar.SimpleTitleToolbar
import com.example.util.firstLetterToUppercase
import com.squareup.picasso.Picasso
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
            profileScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                presenter.changeScrollingOffset(scrollY - oldScrollY)
            })
            ivAvatar.apply {
                clipToOutline = true
            }
            //tvBanned.setOnClickListener { presenter.onBannedClick() }
            tvSettings.setOnClickListener { presenter.onSettingsClick() }
            tvSupport.setOnClickListener { presenter.onSupportClick() }
            tvRate.setOnClickListener { presenter.onRateClick() }
            tvAboutApplication.setOnClickListener { presenter.onAboutApplicationClick() }
            tvLogout.setOnClickListener { presenter.onLogoutClick() }
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
        setUserLink(user)
        val avatar = user.image?.uri
        Picasso.get().load(if (avatar.isNullOrEmpty()) null else avatar)
            .placeholder(R.drawable.avatar_placeholder_rectangle).into(mBinding.ivAvatar)
        mBinding.tvName.text = user.nameLastName

        if (isShowPopup && !::dialog.isInitialized) {
            dialog = AddPhoneEmailDialog(
                requireActivity(),
                if (user.email?.value != null) RegisterDataType.PHONE else RegisterDataType.EMAIL
            )
                .setSelectCallback {
                    when (it.type) {
                        RegisterDataType.PHONE -> {
                            presenter.checkPhoneIsUnique(it.value)
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
        }else if (hasBase && !hasMax) {
            getString(R.string.state_base).firstLetterToUppercase() +  " " + getString(R.string.state)
        }else {
            getString(R.string.state_max).firstLetterToUppercase() + " " + getString(R.string.state)
        }
        mBinding.stateTitle.apply {
            text = newState
            setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToUserStateFragment())
            }
        }
    }

    private fun setUserLink(user: UserDetail) {
        val userId = getString(R.string.user_id, user.id.toString())
        val actionIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_profile_link_edit)
        setToolbarTitleAndIcon(userId, actionIcon) {
            showToast("Open bottom sheet for link")
        }
    }

    override fun setChangeOrAddNewAccount(size: Int) {
        if (size <= 1){
            mBinding.apply {
                tvChangeAccount.text = getString(R.string.add_account_label)
                tvChangeAccount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_profile_add_account_edit, 0, 0, 0);
            }
        }else {
            mBinding.apply {
                tvChangeAccount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_profile_change_account_edit, 0, 0, 0);
                tvChangeAccount.text = getString(R.string.change_account_label)
            }
        }
    }

    override fun showEmailNotUnique(email: String) {
        ConfirmPhoneDialog(
            requireContext(), getString(R.string.confirm_email_text, email),
            getString(R.string.revoke), getString(R.string.confirm_phone_positive)
        )
            .setSelectCallback {
                if (it) {
                    presenter.sendEmail(email)
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
                    presenter.sendPhone(phone)
                }
            }
    }

    override fun showQrScannerToAuthWebSite() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToQrScannerAuthWebsiteFragment())
    }

    override fun emailSuccess() {
        dialog.hideDialog()
        FinishRegisterDialog(requireContext())
            .setSelectCallback { showUserStateDialog() }
    }

    override fun phoneSuccess(phone: String) {
        dialog.hideDialog()
        dialog = AddPhoneEmailDialog(requireActivity(), RegisterDataType.CODE)
        dialog.setPhoneForCode(phone)
        dialog.setSelectCallback {
            if (it.type == RegisterDataType.CODE) {
                (requireActivity() as MainActivity).setIgnoreTokenListener(true)
                presenter.confirmCode(phone, it.value)
            }
        }
        dialog.setNegativeClickCallback { showUserStateDialog() }
        dialog.setSendCodeCallback {
            presenter.sendPhone(phone)
        }
    }

    override fun hideDialogProgress() {
        dialog.isProgressVisible(false)
    }

    override fun codeSuccess() {
        Toast.makeText(requireContext(), "Code confirmed", Toast.LENGTH_SHORT).show()
        (requireActivity() as MainActivity).setIgnoreTokenListener(false)
        dialog.hideDialog()
        showUserStateDialog()
    }

    override fun showProfile(uid: String) {
        //if (BuildConfig.NEW_PROFILE_EDIT) {
        findNavController().navigate(ProfileFragmentDirections.profileToUserProfile())
        /*} else {
            findNavController().navigate(ProfileFragmentDirections.profileToUser(uid))
        }*/
    }

    override fun showChangeAccount() {
        findNavController().navigate(ProfileFragmentDirections.profileToChangeAccount())
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
