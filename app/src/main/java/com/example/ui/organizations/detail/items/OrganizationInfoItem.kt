package com.example.ui.organizations.detail.items

import android.view.View
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemOrganizationInfoBinding
import com.example.extensions.removeUrlUnderline
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

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

    override fun hasSameContentAs(other: Item<*>): Boolean {
        if (other !is OrganizationInfoItem) return false
        if (links != other.links) return false
        if (socialLinks != other.socialLinks) return false
        if (emails != other.emails) return false
        if (phones != other.phones) return false
        if (address != other.address) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemOrganizationInfoBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_organization_info
}