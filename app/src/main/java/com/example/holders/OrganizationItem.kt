package com.example.holders

import com.example.R
import com.example.data.models.Organization
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_organization.*

class OrganizationItem(
        private val organization: Organization,
        private val onOrganizationClick: () -> Unit,
        private val onRemoveClick: () -> Unit
) : Item(organization.id.toLong()) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvOrganizationName.text = organization.name
            itemView.setOnClickListener { onOrganizationClick.invoke() }
            btnAction.apply {
                setOnClickListener { onRemoveClick() }
            }

            ivOrganizationImage.apply {
                clipToOutline = true
                Picasso.get().load(organization.logo.let { if (it.isNullOrBlank()) null else it })
                        .into(this)
            }
        }
    }

    override fun getLayout() = R.layout.item_organization
}