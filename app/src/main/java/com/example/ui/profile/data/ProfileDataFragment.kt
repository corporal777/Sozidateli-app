package com.example.ui.profile.data

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.BottomSheetProfileDataBinding
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.profile.shortName.ChangeShortNameFragment
import com.example.ui.views.CustomSnackBar
import com.example.util.copyTextToBuffer
import com.example.util.setImage
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject
import javax.inject.Provider


class ProfileDataFragment(
    val user: Int,
    val name : String,
    val imageUrl : String?,
    val codeUrl : String?,
) : BaseBottomSheetFragment<BottomSheetProfileDataBinding>(), ProfileDataContract.View {


    @InjectPresenter(type = PresenterType.WEAK, tag = PROFILE_DATA_FRAGMENT_TAG)
    lateinit var presenter: ProfileDataPresenter

    @Inject
    lateinit var presenterProvider: Provider<ProfileDataPresenter>


    @ProvidePresenter(type = PresenterType.WEAK, tag = PROFILE_DATA_FRAGMENT_TAG)
    fun providePresenter(): ProfileDataPresenter = presenterProvider.get().apply {
        userId = user
        userName = name
        userImageUrl = imageUrl?:""
        userCodeUrl = codeUrl?:""
        context = requireContext()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }


    override fun setImage(image: Bitmap) {
        mBinding.apply {
            ivQrCode.setImage(image)
            btnSave.setOnClickListener {
                presenter.saveImageToGalleryClick(requireContext(), image)
            }
            btnShare.setOnClickListener {
                presenter.shareImageClick(requireContext(), image)
            }
        }
    }

    override fun setName(userName: String, userLink: String) {
        mBinding.apply {
            tvUserName.text = userName
            //tvLink.text = userLink
            tvLink.text = StringBuilder(userLink).substring(8, userLink.length)
            clCopy.setOnClickListener {
                copyTextToBuffer(requireContext(), userLink)
                showSnackBarMessage(
                    getString(R.string.link_is_copied),
                    R.drawable.ic_profile_link_edit
                )
            }
            ivShareLink.setOnClickListener {
                presenter.shareLinkClick(userLink)
            }
        }
    }

    override fun showSnackBarMessage(message: String, icon: Int) {
        val snack = CustomSnackBar.make(mBinding.bottomSheet, Snackbar.LENGTH_SHORT)
        snack.setText(message)
        snack.setIcon(icon)
        snack.show()
    }

    override fun showQrCodeLoadingProgress() {
        mBinding.qrProgressBar.isVisible = true
    }

    override fun hideQrCodeLoadingProgress() {
        mBinding.qrProgressBar.isVisible = false
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


    companion object {
        const val PROFILE_DATA_FRAGMENT_TAG = "profile_data_tag"
    }


    override fun layout(): Int = R.layout.bottom_sheet_profile_data
}