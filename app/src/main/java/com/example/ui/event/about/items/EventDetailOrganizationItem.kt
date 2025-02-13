package com.example.ui.event.about.items

import android.graphics.Color
import android.view.View
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemEventDetailOrganizationBlockBinding
import com.example.data.models.OrganizationNew
import com.example.extensions.parseColor
import com.example.ui.views.UserSubscribeButton
import com.example.util.setImagePicasso
import com.xwray.groupie.viewbinding.BindableItem

class EventDetailOrganizationItem(
    private val organization: OrganizationNew?,
    private val actionClickListener: (UserSubscribeButton.Action) -> Unit,
    private val onOrganizationClick: (id: String) -> Unit
) : BindableItem<ItemEventDetailOrganizationBlockBinding>(organization?.id ?: 0) {


    private val organizationName = organization?.getOrganizationName()

    private var isFavorite = organization?.binds?.userFavorite != null
    private val organizationLogo = organization?.logo?.uri
    private val backgroundColor = organization?.backgroundColor?.value.parseColor() ?: Color.DKGRAY

    override fun bind(viewBinding: ItemEventDetailOrganizationBlockBinding, position: Int) {
        viewBinding.apply {
            tvOrganizationLabel.text = organizationName
            ivOrganizationLogo.apply {
                if (organizationLogo.isNullOrEmpty()) {
                    tvOrganizationNameNoImage.apply {
                        isVisible = true
                        text = organizationName
                    }
                    setBackgroundColor(backgroundColor)
                } else setImagePicasso(organizationLogo, R.drawable.background_image_placeholder)

            }
            btnActionFavorite.apply {
                setAction(getAction())
                setOnClickListener { actionClickListener(this.action) }
            }
            organizationCl.setOnClickListener {
                onOrganizationClick.invoke(organization?.id.toString())
            }

        }
    }

    override fun bind(
        viewBinding: ItemEventDetailOrganizationBlockBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val payload = payloads.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is Boolean) {
                isFavorite = payload
                viewBinding.btnActionFavorite.setAction(getAction())
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is EventDetailOrganizationItem) return false
        if (organizationName != other.organizationName) return false
        if (isFavorite != other.isFavorite) return false
        if (organizationLogo != other.organizationLogo) return false
        if (backgroundColor != other.backgroundColor) return false
        return true
    }

    private fun getAction(): UserSubscribeButton.Action {
        return if (isFavorite) UserSubscribeButton.Action.UNFAVORITE else UserSubscribeButton.Action.FAVORITE
    }

    override fun initializeViewBinding(view: View) = ItemEventDetailOrganizationBlockBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_event_detail_organization_block

}