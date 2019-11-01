package com.example.holders

import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_info_header.*

class EventInfoHeaderItem(
        id: Long,
        private val logo: String?,
        private val organizationName: String?,
        private val dates: String?,
        private val description: String?,
        private val onLogoClick: (ImageView, String) -> Unit
) : Item(id) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            ivLogo.apply {
                isVisible = if (!logo.isNullOrEmpty()) {
                    Picasso.get().load(logo).into(this)
                    setOnClickListener { onLogoClick(ivLogo, logo) }
                    true
                } else false
            }
            tvOrganizationLabel.apply {
                isVisible = !organizationName.isNullOrEmpty()
                text = organizationName
            }
            tvEventDate.apply {
                isVisible = !dates.isNullOrEmpty()
                text = dates
            }
            tvEventInfo.apply {
                isVisible = !description.isNullOrEmpty()
                text = description
            }
        }
    }

    override fun getLayout() = R.layout.item_event_info_header
}