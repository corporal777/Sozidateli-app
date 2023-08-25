package com.example.ui.views.galleryView

import android.net.Uri
import coil.transform.RoundedCornersTransformation
import com.example.R
import com.example.databinding.ItemGalleryBinding
import com.example.extensions.dp
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class GalleryItem(
    private val image: Uri,
    val onCameraClick: () -> Unit
) : BindableItem<ItemGalleryBinding>() {


    override fun bind(viewBinding: ItemGalleryBinding, position: Int) {
        viewBinding.apply {
            cvImage.setOnClickListener {
                onCameraClick.invoke()
            }
            ivGalleryImage.apply {
                setImage(image)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_gallery
}