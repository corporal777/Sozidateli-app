package com.example.ui.organizations.detail.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemOrganizationHeaderBinding
import com.example.data.models.OrganizationNew
import com.example.extensions.parseColor
import com.example.holders.redesign.CustomBindingItem
import com.example.ui.views.UserSubscribeButton
import com.example.util.setImage
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class OrganizationHeaderItem(
    private val organization: OrganizationNew,
    private val imageClick: (url: String, view: ImageView) -> Unit,
    private val actionClickListener: (org : OrganizationNew) -> Unit,
) : CustomBindingItem<ItemOrganizationHeaderBinding>(organization.id ?: 0) {


    private val image = organization.image?.uri
    private val logo = organization.logo?.uri
    private val backgroundColor = organization.backgroundColor?.value
    private val name = organization.legalInformation?.name?.short ?: organization.legalInformation?.name?.full
    private val description = organization.description
    private val imageColor = ColorDrawable(backgroundColor.parseColor() ?: Color.DKGRAY)

    override fun bind(viewBinding: ItemOrganizationHeaderBinding, position: Int) {
        viewBinding.apply {
            ivBackground.apply {
                clipToOutline = true
                isVisible = !image.isNullOrEmpty()
                setImage(image, 300)
                setOnClickListener {
                    imageClick.invoke(image ?: "", ivBackground)
                }
            }
            ivLogo.apply {
                clipToOutline = true
                if (logo.isNullOrEmpty()) {
                    setImage(imageColor, 300)
                    tvOrganizationImageName.text = name
                } else {
                    setImage(logo, 300)
                    setOnClickListener {
                        imageClick.invoke(logo, ivLogo)
                    }
                }
            }

            tvName.text = name
            tvDescription.text = description
            btnAction.apply {
                setActionNew(organization.binds?.userFavorite != null)
                setOnClickListener { actionClickListener.invoke(organization) }
            }

        }
    }


    override fun bind(binding: ItemOrganizationHeaderBinding, payload: Any) {
        if (payload is OrganizationNew) {
            organization.binds?.userFavorite = payload.binds?.userFavorite
            binding.btnAction.setActionNew(organization.binds?.userFavorite != null)
        }
    }


    override fun hasSameContentAs(other: Item<*>): Boolean {
        if (other !is OrganizationHeaderItem) return false
        if (organization != other.organization) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemOrganizationHeaderBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_organization_header
}