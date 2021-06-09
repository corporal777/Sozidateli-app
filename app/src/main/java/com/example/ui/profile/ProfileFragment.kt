package com.example.ui.profile

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.data.models.MyEventsFilter
import com.example.data.models.UserDetail
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.main.MainActivity
import com.example.ui.views.AddPhoneEmailDialog
import com.example.ui.views.FinishRegisterDialog
import com.example.ui.views.RegisterDataType
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject
import javax.inject.Provider

class ProfileFragment : BaseFragment(), ProfileContract.View, ToolbarFragment {

    private var isShowPopup = false
    private lateinit var dialog: AddPhoneEmailDialog
    override val title: String? = null

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
        (screenTittle as TextView).text = getString(R.string.profile_label)

        ivAvatar.apply {
            clipToOutline = true
        }

        btnEdit.setOnClickListener { presenter.onProfileClick() }

        tvFavorite.setOnClickListener { presenter.onFavoritesClick() }
        tvEvents.setOnClickListener { presenter.onEventsClick() }
        tvBanned.setOnClickListener { presenter.onBannedClick() }
        tvSupport.setOnClickListener { presenter.onSupportClick() }
        tvRate.setOnClickListener { presenter.onRateClick() }
        tvAboutApplication.setOnClickListener { presenter.onAboutApplicationClick() }
        tvLogout.setOnClickListener { presenter.onLogoutClick() }
        tvSettings.setOnClickListener { presenter.onSettingsClick() }
        //tvSettings.isVisible = BuildConfig.NEW_PROFILE_EDIT
    }

    override fun setUser(user: UserDetail) {
        val avatar = user.image?.uri
        Picasso.get().load(if (avatar.isNullOrEmpty()) null else avatar).placeholder(R.drawable.avatar_placeholder_rectangle).into(ivAvatar)
        tvName.text = user.fullName
        if (isShowPopup && !::dialog.isInitialized) {
            dialog = AddPhoneEmailDialog(requireActivity(),
                    if (user.email?.value != null) RegisterDataType.PHONE else RegisterDataType.EMAIL)
            .setSelectCallback {
                when (it.type) {
                    RegisterDataType.PHONE -> {
                        presenter.sendPhone(it.value)
                    }
                    RegisterDataType.EMAIL -> {
                        presenter.sendEmail(it.value)
                    }
                }
            }
        }
    }

    override fun emailSuccess() {
        dialog.hideDialog()
        FinishRegisterDialog(requireContext())
                .setSelectCallback {  }
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
    }

    override fun showProfile(uid: String) {
        if (BuildConfig.NEW_PROFILE_EDIT) {
            findNavController().navigate(ProfileFragmentDirections.profileToUserProfile())
        } else {
            findNavController().navigate(ProfileFragmentDirections.profileToUser(uid))
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

    override fun openSupportEmail(uid: String) {
        val intent = Intent(ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
        intent.putExtra(EXTRA_SUBJECT, getString(R.string.support_email_title))
        intent.putExtra(EXTRA_TEXT, buildEmailText(uid))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
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
            startActivity(Intent(ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    override fun layout() = R.layout.fragment_profile
}
