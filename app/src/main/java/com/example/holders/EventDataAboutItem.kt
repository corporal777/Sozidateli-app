package com.example.holders

import androidx.core.view.isVisible
import com.example.R
import com.example.ui.views.UserSubscribeButton
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_data_about.*
import setOnClickListener

class EventDataAboutItem(
        itemId: Long,
        private val organizationName: String?,
        private val eventName: String?,
        private val time: String?,
        private val date: String?,
        private var isFavorite: Boolean,
        private val actionClickListener: (UserSubscribeButton.Action) -> Unit,
        private val organizationClickListener: () -> Unit
) : Item(itemId) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvOrganizationLabel.apply {
                text = organizationName
                isVisible = !organizationName.isNullOrEmpty()
                setOnClickListener(organizationClickListener)
            }
            tvEventName.apply {
                text = eventName
                isVisible = !eventName.isNullOrEmpty()
            }

            if (time != null) {
                llTime.isVisible = true
                tvEventTime.text = time
            } else {
                llTime.isVisible = false
            }

            if (date != null) {
                llDate.isVisible = true
                tvEventDate.text = date
            } else {
                llDate.isVisible = false
            }

            btnAction.apply {
                setAction(getAction())
                setOnClickListener { actionClickListener(this.action) }
            }
        }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull()
        if (payload == null) super.bind(viewHolder, position, payloads)
        else {
            if (payload is Boolean) {
                isFavorite = payload
                viewHolder.btnAction.setAction(getAction())
            }
        }
    }

    private fun getAction(): UserSubscribeButton.Action {
        return if (isFavorite) UserSubscribeButton.Action.UNFAVORITE else UserSubscribeButton.Action.FAVORITE
    }

    override fun getLayout() = R.layout.item_event_data_about
}