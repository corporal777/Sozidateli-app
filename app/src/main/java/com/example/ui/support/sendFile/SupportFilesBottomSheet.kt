package com.example.ui.support.sendFile

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.example.R
import com.example.data.models.SupportFile
import com.example.databinding.BottomSheetSupportFilesBinding
import com.example.extensions.findItemBy
import com.example.extensions.updateItems
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.gallery.items.GalleryItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SupportFilesBottomSheet () :
    BaseBottomSheetFragment<BottomSheetSupportFilesBinding>(lightDim = true, isTransparent = true),
    SupportFilesContract.View {

    override fun layout(): Int = R.layout.bottom_sheet_support_files

    @InjectPresenter(tag = GALLERY_TAG)
    lateinit var presenter: SupportFilesPresenter

    @Inject
    lateinit var presenterProvider: Provider<SupportFilesPresenter>

    @ProvidePresenter(tag = GALLERY_TAG)
    fun providePresenter(): SupportFilesPresenter = presenterProvider.get()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var onPhotoUpdated: (file : SupportFile) -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            galleryList.adapter = groupAdapter
            tvOpenGallery.setOnClickListener { presenter.onOpenGalleryClick() }
            tvOpenFile.setOnClickListener { presenter.onOpenFileClick() }
            cardCancel.setOnClickListener { dismiss() }
        }
    }

    override fun setGalleryImages(images: List<Uri>) {
        groupAdapter.updateItems(
            SupportCameraPreviewItem(requireContext(), viewLifecycleOwner) {
                presenter.onOpenCameraClick()
            },
            images.mapIndexed { index, uri ->
                GalleryItem(index, uri) { u, _ -> presenter.onOpenImageClick(u) }
            })
    }

    override fun updateCameraPreviewItem() {
        groupAdapter.findItemBy<GroupieViewHolder, SupportCameraPreviewItem> { true }?.apply {
            notifyChanged(true)
        }
    }


    override fun setFileUriReady(file: SupportFile) {
        onPhotoUpdated.invoke(file)
        dismiss()
    }

    fun setFileReadyCallback(block : (file : SupportFile) -> Unit) : SupportFilesBottomSheet {
        onPhotoUpdated = block
        return this
    }


    fun show(fragmentManager: FragmentManager) = show(fragmentManager, "support_files")


    companion object {
        private const val GALLERY_TAG = "support_files_tag"
    }


}