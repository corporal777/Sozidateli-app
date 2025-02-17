package com.example.holders

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemOrganizationBinding
import com.example.data.models.OrganizationNew
import com.example.ui.views.UserSubscribeButton
import com.squareup.picasso.Picasso
import com.xwray.groupie.viewbinding.BindableItem


class OrganizationItem(
        private val organization: OrganizationNew,
        private val onOrganizationClick: () -> Unit,
        private val onSubscribeClick: (() -> Unit)? = null
) : BindableItem<ItemOrganizationBinding>(organization.id?.toLong()?: 0) {

    override fun bind(viewBinding: ItemOrganizationBinding, position: Int) {
        viewBinding.apply {
            tvOrganizationName.text = organization.legalInformation?.name?.short
            root.setOnClickListener { onOrganizationClick.invoke() }
            btnAction.apply {
                isVisible = if (onSubscribeClick != null) {
                    setAction(if (organization.binds?.userFavorite != null) UserSubscribeButton.Action.UNFAVORITE else UserSubscribeButton.Action.FAVORITE)
                    setOnClickListener { onSubscribeClick.invoke() }
                    true
                } else {
                    false
                }
            }

            tvOrganizationImageName.apply {
                text = organization.legalInformation?.name?.short
                clipToOutline = true
                if (!organization.backgroundColor?.value.isNullOrEmpty())
                    ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(Color.parseColor(organization.backgroundColor?.value)))
                else
                    ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.colorAccent, null)))
            }

            ivOrganizationImage.apply {
                clipToOutline = true
                Picasso.get().load(organization.logo.let { if (it?.uri.isNullOrBlank()) null else it?.uri }).into(this)
            }
        }
    }

    override fun initializeViewBinding(view: View) = ItemOrganizationBinding.bind(view)
    override fun getLayout() = R.layout.item_organization

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrganizationItem) return false

        if (organization != other.organization) return false

        return true
    }

    override fun hashCode(): Int {
        return organization.hashCode()
    }
}