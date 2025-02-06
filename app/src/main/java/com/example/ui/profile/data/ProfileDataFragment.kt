package com.example.ui.profile.data

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.example.app.R
import com.example.app.databinding.BottomSheetProfileDataBinding
import com.example.data.models.Argument
import com.example.extensions.parcelableArgument
import com.example.ui.base.bottomSheet.BaseBSFragment
import com.example.ui.views.CustomSnackBar
import com.example.util.copyTextToBuffer
import com.example.util.setImage
import com.google.android.material.snackbar.Snackbar
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider
import dev.androidbroadcast.vbpd.viewBinding


class ProfileDataFragment : BaseBSFragment(), ProfileDataContract.View {

    private val viewBinding by viewBinding(BottomSheetProfileDataBinding::bind)


    @InjectPresenter(tag = PROFILE_DATA_FRAGMENT_TAG)
    lateinit var presenter: ProfileDataPresenter

    @Inject
    lateinit var presenterProvider: Provider<ProfileDataPresenter>

    @ProvidePresenter(tag = PROFILE_DATA_FRAGMENT_TAG)
    fun providePresenter(): ProfileDataPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnClose.setOnClickListener {
            dismiss()
        }
    }


    override fun setImage(image: Bitmap) {
        viewBinding.apply {
            ivQrCode.setImage(image, 300)
            btnSave.setOnClickListener {
                presenter.saveImageToGalleryClick(requireContext(), image)
            }
            btnShare.setOnClickListener {
                presenter.shareImageClick(requireContext(), image)
            }
        }
    }

    override fun setName(userName: String, userLink: String) {
        viewBinding.apply {
            tvUserName.text = userName
            tvLink.text = StringBuilder(userLink).substring(8, userLink.length)
            clCopy.setOnClickListener {
                copyTextToBuffer(requireContext(), userLink)
                showSnackBarMessage(R.string.link_is_copied, R.drawable.ic_profile_link_edit)
            }
            ivShareLink.setOnClickListener {
                presenter.shareLinkClick(userLink)
            }
        }
    }

    override fun showSnackBarMessage(message: Int, icon: Int) {
        val snack = CustomSnackBar.make(viewBinding.bottomSheet, Snackbar.LENGTH_SHORT)
        snack.setText(getString(message))
        snack.setIcon(icon)
        snack.show()
    }

    override fun showCustomLoading() {
        viewBinding.apply {
            ivQrCode.isVisible = false
            shimmerQrCode.isVisible = true
        }
    }

    override fun hideCustomLoading() {
        viewBinding.apply {
            ivQrCode.isVisible = true
            shimmerQrCode.isVisible = false
        }
    }

    override fun showShareImage(uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/png"
            }
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
            val shareApp = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, link)
            }
            startActivity(Intent.createChooser(shareApp, "Choose one of the:"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, PROFILE_DATA_FRAGMENT_TAG)

    companion object {
        const val PROFILE_DATA_FRAGMENT_TAG = "profile_data_dialog"
    }


    override fun layout(): Int = R.layout.bottom_sheet_profile_data
}