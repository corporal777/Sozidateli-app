package com.example.ui.organizations.detail.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemOrganizationHeaderBinding
import com.example.ui.views.UserSubscribeButton
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem
import parseColor

class OrganizationHeaderItem(
    val image: String?,
    val logo: String?,
    val backgroundColor: String?,
    val name: String?,
    val description: String?,
    val userFavorite: Boolean,
    private val imageClick: (url: String, view:  ImageView) -> Unit,
    private val actionClickListener: (UserSubscribeButton.Action) -> Unit,
) : BindableItem<ItemOrganizationHeaderBinding>() {

    private var imageColor = ColorDrawable(Color.DKGRAY)

    private var isSubscribed = userFavorite

    init {
        if (!backgroundColor.isNullOrEmpty()) {
            val color = backgroundColor.parseColor() ?: Color.DKGRAY
            imageColor = ColorDrawable(color)
        }
    }

    override fun bind(viewBinding: ItemOrganizationHeaderBinding, position: Int) {
        viewBinding.apply {
            ivBackground.apply {
                clipToOutline = true
                isVisible = !image.isNullOrEmpty()
                setImage(image)
                setOnClickListener {
                    imageClick.invoke(image ?: "", ivBackground)
                }
            }
            ivLogo.apply {
                clipToOutline = true
                if (logo.isNullOrEmpty()) {
                    setImage(imageColor)
                    tvOrganizationImageName.text = name
                } else {
                    setImage(logo)
                    setOnClickListener {
                        imageClick.invoke(logo , ivLogo)
                    }
                }
            }

            tvName.text = name
            tvDescription.text = description
            setSubscribed(isSubscribed, btnAction)
        }
    }

    override fun bind(
        viewBinding: ItemOrganizationHeaderBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val payload = payloads.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Boolean) {
                isSubscribed = payload
                setSubscribed(isSubscribed, viewBinding.btnAction)
            }
        }
    }

    private fun setSubscribed(isSubscribed: Boolean, button: UserSubscribeButton) {
        button.setActionNew(isSubscribed)
        button.setOnClickListener {
            actionClickListener.invoke(button.action)
        }
    }

    override fun getLayout(): Int = R.layout.item_organization_header
}