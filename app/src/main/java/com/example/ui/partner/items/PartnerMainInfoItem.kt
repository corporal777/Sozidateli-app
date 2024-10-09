package com.example.ui.partner.items

import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemMainInfoPartnerBinding
import com.example.extensions.markWon
import com.example.util.setImage
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import com.example.extensions.removeUrlUnderline

class PartnerMainInfoItem(
    val id: Int?,
    val logo: String?,
    val back: String?,
    val name: String?,
    val description: String?,
    val links: String?,
    val supportType: String?,
    val onImageClick: (view: ImageView, url: String?) -> Unit
) : BindableItem<ItemMainInfoPartnerBinding>(id?.toLong() ?: 0) {


    override fun bind(viewBinding: ItemMainInfoPartnerBinding, position: Int) {
        viewBinding.apply {
            ivBackground.apply {
                clipToOutline = true
                if (back.isNullOrEmpty()) isVisible = false
                else {
                    setImage(back)
                    setOnClickListener {
                        onImageClick.invoke(this, back)
                    }
                }
            }
            ivLogo.apply {
                clipToOutline = true
                if (logo.isNullOrEmpty()) isVisible = false
                else {
                    setImage(logo)
                    setOnClickListener {
                        onImageClick.invoke(this, logo)
                    }
                }
            }

            tvName.apply {
                isVisible = !name.isNullOrBlank()
                text = name
            }

            tvDescription.apply {
                isVisible = !description.isNullOrBlank()
                if (!description.isNullOrBlank())
                    markWon(context).setMarkdown(this, description)
            }

            tvLinks.apply {
                text = if (links.isNullOrEmpty()) context.getString(R.string.user_profile_no_site) else links
                removeUrlUnderline()
            }
            llSupportType.isVisible = !supportType.isNullOrEmpty()
            tvSupportType.text = supportType
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is PartnerMainInfoItem) return false
        if (logo != other.logo) return false
        if (back != other.back) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (links != other.links) return false
        if (supportType != other.supportType) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_main_info_partner
}