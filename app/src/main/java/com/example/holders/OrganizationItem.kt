package com.example.holders

import com.example.R
import com.example.data.models.Organization
import com.example.extensions.dp
import com.example.ui.views.UserSubscribeButton
import com.example.util.RoundedCornersTransformation
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_organization.*

class OrganizationItem(
        private val organization: Organization,
        private val onOrganizationClick: () -> Unit,
        private val onRemoveClick: () -> Unit
) : Item(organization.id.toLong()) {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvOrganizationName.text = organization.name
            itemView.setOnClickListener { onOrganizationClick.invoke() }
            btnAction.apply {
                setAction(UserSubscribeButton.ACTION_UNSUBSCRIBE)
                setOnClickListener { onRemoveClick() }
            }

            Picasso.get().load(organization.logo.let { if (it.isNullOrBlank()) null else it })
                    .transform(RoundedCornersTransformation(16.dp, 0))
                    .into(ivOrganizationImage)
        }
    }

    override fun getLayout() = R.layout.item_organization
}