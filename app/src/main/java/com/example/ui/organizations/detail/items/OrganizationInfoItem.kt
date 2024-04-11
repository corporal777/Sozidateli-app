package com.example.ui.organizations.detail.items

import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemOrganizationInfoBinding
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import com.example.extensions.removeUrlUnderline

class OrganizationInfoItem(
    val orgId: Long?,
    val links: String?,
    val socialLinks: String?,
    val emails: String?,
    val phones: String?,
    val address: String?
) : BindableItem<ItemOrganizationInfoBinding>(orgId?: 0) {


    override fun bind(viewBinding: ItemOrganizationInfoBinding, position: Int) {
        viewBinding.apply {
            tvLinksTitle.isVisible = !links.isNullOrEmpty()
            tvLinks.isVisible = !links.isNullOrEmpty()
            tvLinks.text = links

            tvSnLinksTitle.isVisible = !socialLinks.isNullOrEmpty()
            tvSnLinks.apply {
                isVisible = !socialLinks.isNullOrEmpty()
                text = socialLinks
                removeUrlUnderline()
            }

            tvEmailTitle.isVisible = !emails.isNullOrEmpty()
            tvEmail.apply {
                isVisible = !emails.isNullOrEmpty()
                text = emails
                removeUrlUnderline()
            }

            tvPhoneTitle.isVisible = !phones.isNullOrEmpty()
            tvPhone.apply {
                isVisible = !phones.isNullOrEmpty()
                text = phones
                removeUrlUnderline()
            }

            tvAddressTitle.isVisible = !address.isNullOrEmpty()
            tvAddress.isVisible = !address.isNullOrEmpty()
            tvAddress.text = address
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is OrganizationInfoItem) return false
        if (links != other.links) return false
        if (socialLinks != other.socialLinks) return false
        if (emails != other.emails) return false
        if (phones != other.phones) return false
        if (address != other.address) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_organization_info
}