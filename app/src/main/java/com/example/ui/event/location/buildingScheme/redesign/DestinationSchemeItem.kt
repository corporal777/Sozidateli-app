package com.example.ui.event.location.buildingScheme.redesign

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import coil.load
import com.bumptech.glide.Glide
import com.example.R
import com.example.databinding.ItemBuildingSchemeBinding
import com.example.util.setImage
import com.example.util.showCustomTabsBrowser
import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import kotlinx.android.synthetic.main.item_building_scheme.*
import okhttp3.OkHttpClient
import okhttp3.Request
import onScrolled
import java.io.File
import java.io.InputStream

class DestinationSchemeItem(
    val id: String,
    val name: String?,
    val description: String?,
    val image: String?,
    val onImageClick: (image: String?) -> Unit,
    val onScrollChange: (scroll: Int) -> Unit
) : BindableItem<ItemBuildingSchemeBinding>(id.toLong()) {

    @SuppressLint("RestrictedApi")
    override fun bind(viewBinding: ItemBuildingSchemeBinding, position: Int) {
        viewBinding.apply {
            ivScheme.apply {
                isVisible = !image.isNullOrEmpty()
                transitionName = image
                Picasso.get()
                    .load(Uri.parse(image))
                    .placeholder(R.drawable.background_image_placeholder)
                    .error(R.drawable.ic_broken_image)
                    .into(this)

                setOnClickListener { onImageClick.invoke(image) }
            }

            tvDescriptionTitle.apply {
                text = name
                isVisible = !name.isNullOrEmpty()
            }

            tvDescription.apply {
                text = description
                isVisible = !description.isNullOrEmpty()
            }

            scrollContainer.apply {
                onScrolled { _, _, _, _ ->
                    onScrollChange.invoke(this.computeVerticalScrollOffset())
                }
            }
        }
    }


    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is DestinationSchemeItem) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (image != other.image) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_building_scheme
}