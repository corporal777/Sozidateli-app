package com.example.ui.views.galleryView

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.BottomSheetGalleryBinding
import com.example.extensions.*
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.util.rxtakephoto.CropActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
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


    private var onGalleryClick: () -> Unit = { }
    private var onCameraClick: () -> Unit = { }
    private var onImageClick: (uri: Uri) -> Unit = { }
    private var onRemoveClick: (fr: GalleryBottomSheet) -> Unit = { }

    private val groupAdapter by lazy { GroupAdapter<GroupieViewHolder>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            galleryList.adapter = groupAdapter
            tvOpenGallery.setOnClickListener {
                onGalleryClick.invoke()
                dismiss()
            }
            tvRemovePhoto.setOnClickListener {
                onRemoveClick.invoke(this@GalleryBottomSheet)
            }
            cardCancel.setOnClickListener {
                dismiss()
            }
        }
    }


    override fun setGalleryImages(images: List<Uri>) {
        this.groupAdapter.updateItems(
            CameraPreviewItem(requireContext(), viewLifecycleOwner) {
                onCameraClick.invoke()
                dismiss()
            },
            images.map {
                GalleryItem(it) { uri, view ->
                    onImageClick.invoke(it)
                    //showCropActivity(uri, view)
                    dismiss()
                }
            })
    }

    override fun hideGalleryFragment() {
        dismiss()
    }

    fun setGalleryCallback(block: () -> Unit): GalleryBottomSheet {
        onGalleryClick = block
        return this
    }

    fun setImageCallback(block: (uri : Uri) -> Unit): GalleryBottomSheet {
        onImageClick = block
        return this
    }

    fun setCameraCallback(block: () -> Unit): GalleryBottomSheet {
        onCameraClick = block
        return this
    }

    fun setRemoveCallback(block: (fr: GalleryBottomSheet) -> Unit): GalleryBottomSheet {
        onRemoveClick = block
        return this
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, "gallery")
    }

    private fun showCropActivity(uri : Uri, view : ImageView){
        val opt = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            Pair(view, view.transitionName)
        )

        findNavController().navigate(
            R.id.image_crop_activity,
            bundleOf(
                CropActivity.ARG_URL to uri.toString(),
                CropActivity.ARG_TRANSITION_NAME to view.transitionName
            ),
            null,
            ActivityNavigatorExtras(opt)
        )
    }

    companion object {
        private const val GALLERY_TAG = "gallery_bottom_sheet_tag"
    }


}