package com.example.ui.gallery.items

import android.net.Uri
import android.widget.ImageView
import com.example.app.R
import com.example.app.databinding.ItemGalleryBinding
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class GalleryItem(
    private val id : Int,
    private val image: Uri,
    val onCameraClick: (uri : Uri, view : ImageView) -> Unit
) : BindableItem<ItemGalleryBinding>(id.toLong()) {


    override fun bind(viewBinding: ItemGalleryBinding, position: Int) {
        viewBinding.apply {
            cvImage.setOnClickListener {
                onCameraClick.invoke(image, ivGalleryImage)
            }
            ivGalleryImage.apply {
                transitionName = image.toString()
                setImage(image)
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is GalleryItem) return false
        if (image != other.image) return false
        return true
    }


    override fun getLayout(): Int = R.layout.item_gallery
}