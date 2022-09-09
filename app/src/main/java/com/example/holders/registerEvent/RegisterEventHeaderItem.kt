package com.example.holders.registerEvent

import androidx.core.view.isVisible
import com.example.R
import com.example.util.markWon
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_register_event_header.*

open class RegisterEventHeaderItem(
        id: Long,
        private val eventName: String?,
        private val time: String?,
        private val dates: String?,
        private val finishDate: String?,
        private val formTitle: String?,
        private val formDescription: String?
) : Item(id) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvEventName.apply {
                isVisible = !eventName.isNullOrEmpty()
                text = eventName
            }

            tvEventTime.text = time
            llTime.isVisible = time.isNullOrEmpty().not()

            tvEventDate.text = dates
            llDate.isVisible = dates.isNullOrEmpty().not()

            tvRegistrationFinishDate.text = finishDate
            llRegistrationFinishDate.isVisible = finishDate.isNullOrEmpty().not()

            tvFormLabel.apply {
                isVisible = !formTitle.isNullOrBlank()
                text = formTitle
            }

            tvFormDescription.apply {
                isVisible = !formDescription.isNullOrBlank()
                markWon(context).setMarkdown(this, formDescription?:"")
                //text = formDescription
            }

            llFormTitleContainer.isVisible = tvFormLabel.isVisible || tvFormDescription.isVisible
        }
    }

    override fun getLayout() = R.layout.item_register_event_header
}