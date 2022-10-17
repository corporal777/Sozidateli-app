package com.example.ui.profile.data

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.databinding.BottomSheetProfileDataBinding
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.views.CustomSnackBar
import com.example.util.copyTextToBuffer
import com.example.util.setImage
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject
import javax.inject.Provider


class ProfileDataFragment(
    val name: String,
    val shortName: String,
    val bm: Bitmap
) : BaseBottomSheetFragment<BottomSheetProfileDataBinding>(), ProfileDataContract.View {

    private var userProfileShareLink = ""

    @InjectPresenter
    lateinit var presenter: ProfileDataPresenter

    @Inject
    lateinit var presenterProvider: Provider<ProfileDataPresenter>

    @ProvidePresenter
    fun providePresenter(): ProfileDataPresenter = presenterProvider.get().apply {
        image = bm
        userShortName = shortName
        userName = name
        userProfileShareLink = BuildConfig.SHARE_URL + "portal/user/" + userShortName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            clCopy.setOnClickListener {
                copyTextToBuffer(requireContext(), userProfileShareLink)
                showSnackBarMessage(
                    getString(R.string.link_is_copied),
                    R.drawable.ic_profile_link_edit
                )
            }
            ivShareLink.setOnClickListener {
                presenter.shareLinkClick(userProfileShareLink)
            }
            btnSave.setOnClickListener {
                presenter.saveImageToGalleryClick(requireContext())
            }
            btnShare.setOnClickListener {
                presenter.shareImageClick(requireContext())
            }
            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun setData(image: Bitmap, userName: String, userLink: String) {
        mBinding.apply {
            ivQrCode.setImage(image)
            tvUserName.text = userName
            tvLink.text = userLink
        }
    }

    override fun showSnackBarMessage(message: String, icon: Int) {
        val snack = CustomSnackBar.make(mBinding.bottomSheet, Snackbar.LENGTH_SHORT)
        snack.setText(message)
        snack.setIcon(icon)
        snack.show()
    }

    override fun showShareImage(uri: Uri) {

        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setType("image/png")
            val chooser = Intent.createChooser(intent, "Share File")
            val resInfoList: List<ResolveInfo> = requireActivity().packageManager
                .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

            for (resolveInfo in resInfoList) {
                val packageName: String = resolveInfo.activityInfo.packageName
                requireActivity().grantUriPermission(
                    packageName,
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            startActivity(chooser)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun showShareLink(link: String) {
        try {
            val shareApp = Intent(Intent.ACTION_SEND)
            shareApp.type = "text/plain"
            shareApp.putExtra(Intent.EXTRA_TEXT, link)
            startActivity(Intent.createChooser(shareApp, "Choose one of the:"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun layout(): Int = R.layout.bottom_sheet_profile_data
}