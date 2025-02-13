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
import com.example.ui.views.UserSubscribeButton
import com.example.util.setImage
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class OrganizationHeaderItem(
    org: OrganizationNew,
    private val imageClick: (url: String, view: ImageView) -> Unit,
    private val actionClickListener: (org : OrganizationNew) -> Unit,
) : BindableItem<ItemOrganizationHeaderBinding>(org.id ?: 0) {

    private var organization = org
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
                        imageClick.invoke(logo, ivLogo)
                    }
                }
            }

            tvName.text = name
            tvDescription.text = description
            setSubscribed(btnAction)
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
            if (payload is OrganizationNew) {
                organization = payload
                setSubscribed(viewBinding.btnAction)
            }
        }
    }

    private fun setSubscribed(button: UserSubscribeButton) {
        button.setActionNew(organization.binds?.userFavorite != null)
        button.setOnClickListener {
            actionClickListener.invoke(organization)
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