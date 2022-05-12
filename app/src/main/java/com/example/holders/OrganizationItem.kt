package com.example.holders

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Organization
import com.example.data.models.OrganizationNew
import com.example.ui.views.UserSubscribeButton
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_organization.*
import parseColor

class OrganizationItem(
        private val organization: OrganizationNew/*Organization*/,
        private val onOrganizationClick: () -> Unit,
        private val onSubscribeClick: (() -> Unit)? = null
) : Item(organization.id?.toLong()?: 0) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            /*tvOrganizationName.text = organization.name
            itemView.setOnClickListener { onOrganizationClick.invoke() }
            btnAction.apply {
                isVisible = if (onSubscribeClick != null) {
                    setAction(if (organization.isSubscribed == true) UserSubscribeButton.Action.UNFAVORITE else UserSubscribeButton.Action.FAVORITE)
                    setOnClickListener { onSubscribeClick.invoke() }
                    true
                } else {
                    false
                }
            }

            tvOrganizationImageName.apply {
                text = organization.name
                clipToOutline = true
                ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(organization.backgroundColor.parseColor()
                        ?: ResourcesCompat.getColor(resources, R.color.colorAccent, null)))
            }

            ivOrganizationImage.apply {
                clipToOutline = true
                Picasso.get().load(organization.logo.let { if (it.isNullOrBlank()) null else it }).into(this)
            }*/
            tvOrganizationName.text = organization.legalInformation?.name?.short
            itemView.setOnClickListener { onOrganizationClick.invoke() }
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