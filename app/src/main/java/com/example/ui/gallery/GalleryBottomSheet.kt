package com.example.ui.gallery

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.BottomSheetGalleryBinding
import com.example.extensions.*
import com.example.holders.ProfileContactsEditItem
import com.example.ui.about.AboutFragment
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.gallery.camera.CustomCameraActivity
import com.example.ui.gallery.items.CameraPreviewItem
import com.example.ui.gallery.items.GalleryItem
import com.example.ui.views.WarningDialog
import com.example.util.getMakeSceneTransition
import com.example.util.rxtakephoto.CropActivity
import com.example.util.rxtakephoto.CropCallbackHelper
import com.example.util.rxtakephoto.RxTakePhoto
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.layout_inapp.*
import javax.inject.Inject
import javax.inject.Provider

class GalleryBottomSheet() :
    BaseBottomSheetFragment<BottomSheetGalleryBinding>(lightDim = true, isTransparent = true),
    GalleryBottomContract.View {

    override fun layout(): Int = R.layout.bottom_sheet_gallery

    @InjectPresenter(type = PresenterType.WEAK, tag = GALLERY_TAG)
    lateinit var presenter: GalleryBottomPresenter

    @Inject
    lateinit var presenterProvider: Provider<GalleryBottomPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = GALLERY_TAG)
    fun providePresenter(): GalleryBottomPresenter = presenterProvider.get()

    private val groupAdapter by lazy { GroupAdapter<GroupieViewHolder>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            galleryList.adapter = groupAdapter
            tvOpenGallery.setOnClickListener { presenter.onGalleryClick() }
            tvRemovePhoto.setOnClickListener { showRemovePhotoWarning() }
            cardCancel.setOnClickListener { dismiss() }
        }
    }

    override fun setGalleryImages(images: List<Uri>) {
        groupAdapter.updateItems(
            CameraPreviewItem(requireContext(), viewLifecycleOwner) { uri, view ->
                showCameraActivity(uri, view)
            },
            images.mapIndexed { index, uri ->
                GalleryItem(index, uri) { u, v ->
                    showCropActivity(u, v)
                }
            })
    }

    override fun updateCameraPreviewItem() {
        groupAdapter.findItemBy<GroupieViewHolder, CameraPreviewItem> { true }?.apply {
            notifyChanged(true)
        }
    }

    override fun hideGalleryFragment() = dismiss()
    fun show(fragmentManager: FragmentManager) = show(fragmentManager, "gallery")

    override fun showCameraActivity(uri: Uri?, imageView: ImageView) {
        presenter.observeCropFinished(CropCallbackHelper.createCropFinishedRequest())
        findNavController().navigate(
            R.id.custom_camera_activity,
            bundleOf(
                CropActivity.ARG_URL to uri.toString(),
                CropActivity.ARG_TRANSITION_NAME to imageView.transitionName
            ),
            null,
            ActivityNavigatorExtras(getMakeSceneTransition(imageView))
        )
    }

    override fun showCropActivity(uri: Uri, imageView: ImageView?) {
        presenter.observeCropFinished(CropCallbackHelper.createCropFinishedRequest())
        val opt = if (imageView != null) getMakeSceneTransition(imageView) else null
        findNavController().navigate(
            R.id.image_crop_activity,
            bundleOf(
                CropActivity.ARG_URL to uri.toString(),
                CropActivity.ARG_TRANSITION_NAME to imageView?.transitionName
            ),
            null,
            ActivityNavigatorExtras(opt)
        )
    }

    private fun showRemovePhotoWarning() {
        if (presenter.isHasAnyState()) {
            WarningDialog(requireActivity(), resources.getString(R.string.warning_dialog_text))
                .setSelectCallback { if (it) presenter.onRemovePhotoClick() }
        }
    }

    companion object {
        private const val GALLERY_TAG = "gallery_bottom_sheet_tag"
    }


}