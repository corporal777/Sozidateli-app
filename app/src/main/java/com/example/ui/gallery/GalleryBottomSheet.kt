package com.example.ui.gallery

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.data.models.ImageModel
import com.example.app.databinding.BottomSheetGalleryBinding
import com.example.extensions.findItemBy
import com.example.extensions.updateItems
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.gallery.items.CameraPreviewItem
import com.example.ui.gallery.items.GalleryItem
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.util.getMakeSceneTransition
import com.example.util.rxtakephoto.CropActivity
import com.example.util.rxtakephoto.CropCallbackHelper
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class GalleryBottomSheet() :
    BaseBottomSheetFragment<BottomSheetGalleryBinding>(lightDim = true, isTransparent = true),
    GalleryBottomContract.View {

    override fun layout(): Int = R.layout.bottom_sheet_gallery

    @InjectPresenter(tag = GALLERY_TAG)
    lateinit var presenter: GalleryBottomPresenter

    @Inject
    lateinit var presenterProvider: Provider<GalleryBottomPresenter>

    @ProvidePresenter(tag = GALLERY_TAG)
    fun providePresenter(): GalleryBottomPresenter = presenterProvider.get()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var onPhotoUpdated: (photo: ImageModel?) -> Unit = {}

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

    override fun setPhotoUpdated(image: ImageModel?) {
        onPhotoUpdated.invoke(image)
    }

    fun setPhotoUpdated(block: (image: ImageModel?) -> Unit): GalleryBottomSheet {
        onPhotoUpdated = block
        return this
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
            DefaultAlertDialog(
                requireActivity(),
                null,
                getString(R.string.warning_dialog_text),
                getString(R.string.yes),
                getString(R.string.no)
            ).setSelectCallback { presenter.onRemovePhotoClick() }
        }
    }

    companion object {
        private const val GALLERY_TAG = "gallery_bottom_sheet_tag"
    }


}