package com.example.holders.registerEvent

import androidx.core.view.isVisible
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_register_event_header.*

open class RegisterEventHeaderItem(
        id: Long,
        private val organizationName: String?,
        private val dates: String?,
        private val description: String?,
        private val formTitle: String?,
        private val formDescription: String?
) : Item(id) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
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
            tvFormLabel.apply {
                isVisible = !formTitle.isNullOrEmpty()
                text = formTitle
            }
            tvFormDescription.apply {
                isVisible = !formDescription.isNullOrEmpty()
                text = formDescription
            }

            llFormTitleContainer.isVisible = tvFormLabel.isVisible || tvFormDescription.isVisible
        }
    }

    override fun getLayout() = R.layout.item_register_event_header
}