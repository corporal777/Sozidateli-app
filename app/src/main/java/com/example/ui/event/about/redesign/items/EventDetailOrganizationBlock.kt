package com.example.ui.event.about.redesign.items

import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventNew
import com.example.databinding.ItemEventDetailOrganizationBlockBinding
import com.example.ui.views.UserSubscribeButton
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem
import parseColor

class EventDetailOrganizationBlock(
    private val eventData: EventNew?,
    private val actionClickListener: (UserSubscribeButton.Action) -> Unit,
    private val onOrganizationClick: (id: String) -> Unit
) : BindableItem<ItemEventDetailOrganizationBlockBinding>() {


    private val organizationName = eventData?.binds?.organization?.legalInformation?.name?.short
        ?: eventData?.binds?.organization?.legalInformation?.name?.full

    private var isFavorite: Boolean = eventData?.binds?.organization?.binds?.userFavorite != null


    private val organizationLogo = eventData?.binds?.organization?.logo?.uri ?: ""
    private val backgroundColor = eventData?.binds?.organization?.backgroundColor?.value

    override fun bind(viewBinding: ItemEventDetailOrganizationBlockBinding, position: Int) {
        viewBinding.apply {

            tvOrganizationLabel.text = organizationName

            if (organizationLogo.isNullOrEmpty()) {
                tvOrganizationNameNoImage.apply {
                    isVisible = true
                    text = organizationName
                }
                ivOrganizationLogo.apply {
                    setBackgroundColor(
                        backgroundColor.parseColor() ?: ResourcesCompat.getColor(
                            resources,
                            R.color.event_item_no_image_background,
                            null
                        )
                    )
                }
            }else {
                ivOrganizationLogo.setImage(organizationLogo)
            }

            btnActionFavorite.apply {
                setAction(getAction())
                setOnClickListener { actionClickListener(this.action) }
            }
            organizationCl.setOnClickListener {
                onOrganizationClick.invoke(eventData?.organization.toString())
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


    private fun getAction(): UserSubscribeButton.Action {
        return if (isFavorite) UserSubscribeButton.Action.UNFAVORITE else UserSubscribeButton.Action.FAVORITE
    }

    override fun getLayout(): Int = R.layout.item_event_detail_organization_block

}