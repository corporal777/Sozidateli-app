package com.example.holders

import android.view.View
import com.example.R
import com.example.data.models.Subevent
import com.example.ui.mySchedule.subevent.SubeventPresenter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_subevent_header.view.*

open class SubeventHeaderItem(private val subevent: Subevent,private val presenter:SubeventPresenter) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.apply {
                tvTime.text = subevent.time
                tvTitle.text = subevent.title
                tvDescription.text = subevent.description
                tvLocation.text = subevent.location

                llListMember.setOnClickListener { presenter.onOpenUserListClick() }

                if(subevent.isSpeaker){
                    llListMember.visibility = View.VISIBLE
                } else{
                    llListMember.visibility = View.GONE
                }
                topDivider.visibility = llListMember.visibility
                bottomDivider.visibility = llListMember.visibility
            }
    }

    override fun getLayout() = R.layout.item_subevent_header
}