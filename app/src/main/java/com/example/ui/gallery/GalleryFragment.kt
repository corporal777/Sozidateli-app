package com.example.ui.gallery

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.BottomSheetGalleryBinding
import com.example.extensions.findItemBy
import com.example.extensions.updateItems
import com.example.ui.base.BaseFragment
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.gallery.items.CameraPreviewItem
import com.example.ui.gallery.items.GalleryItem
import com.example.ui.views.WarningDialog
import com.example.util.getMakeSceneTransition
import com.example.util.rxtakephoto.CropActivity
import com.example.util.rxtakephoto.CropCallbackHelper
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import javax.inject.Inject
import javax.inject.Provider

class GalleryFragment : BaseFragment<BottomSheetGalleryBinding>() {

    override fun layout(): Int = R.layout.bottom_sheet_gallery



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            tvOpenGallery.setOnClickListener {  }
            tvRemovePhoto.setOnClickListener {  }
            cardCancel.setOnClickListener {  }
        }
    }



    companion object {
        private const val GALLERY_TAG = "gallery_bottom_sheet_tag"
    }


}