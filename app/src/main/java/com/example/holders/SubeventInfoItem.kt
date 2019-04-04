package com.example.holders

import android.view.View
import com.example.R
import com.example.data.models.SubeventInfo
import com.example.extensions.formatDefaultServerTimeToDefaultTimeInterval
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_subevent_info.view.*

open class SubeventInfoItem(
        private val subevent: SubeventInfo,
        private val onOpenUserListClick: () -> Unit
) : Item(subevent.id.toLong()) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            tvTime.text = subevent.start.formatDefaultServerTimeToDefaultTimeInterval(subevent.finish)
            tvTitle.text = subevent.title
            tvDescription.text = subevent.description
            tvLocation.text = subevent.location

            llListMember.apply {
                visibility = if (subevent.isSpeaker) {
                    setOnClickListener { onOpenUserListClick() }
                    View.VISIBLE
                } else {
                    setOnClickListener(null)
                    View.GONE
                }
            }
        }
    }

    override fun getLayout() = R.layout.item_subevent_info
}