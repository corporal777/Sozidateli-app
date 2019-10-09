package com.example.holders

import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Organization
import com.example.ui.views.UserSubscribeButton
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_organization.*

class OrganizationItem(
        private val organization: Organization,
        private val onOrganizationClick: () -> Unit,
        private val onSubscribeClick: (() -> Unit)? = null
) : Item(organization.id.toLong()) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvOrganizationName.text = organization.name
            itemView.setOnClickListener { onOrganizationClick.invoke() }
            btnAction.apply {
                isVisible = if (onSubscribeClick != null) {
                    setAction(if (organization.isSubscribed == true) UserSubscribeButton.Action.UNSUBSCRIBE else UserSubscribeButton.Action.SUBSCRIBE)
                    setOnClickListener { onSubscribeClick.invoke() }
                    true
                } else {
                    false
                }

            }

            ivOrganizationImage.apply {
                clipToOutline = true
                Picasso.get().load(organization.logo.let { if (it.isNullOrBlank()) null else it })
                        .into(this)
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