package com.example.holders

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.GridLayout
import com.example.R
import com.example.data.models.Subevent
import com.example.ui.mySchedule.MySchedulePresenter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_action_button.view.*
import kotlinx.android.synthetic.main.item_sub_event.view.*

open class SubEventItem(private val subEvent: Subevent, private val isInMySchedule: Boolean, private val presenter: MySchedulePresenter) : Item() {

    private val groupAdapterTags = GroupAdapter<ViewHolder>()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            tvTime.text = subEvent.time
            groupAdapterTags.update(
                    subEvent.tags.map {
                        TagItem(it, false, !isInMySchedule) { tag, isSelected -> }
                    }
            )
            rvTags.apply {
                layoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(2, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL)
                adapter = groupAdapterTags
            }

            if (subEvent.isInSchedule) {
                btnAdd.setBackgroundResource(R.drawable.background_corners_border)
                btnAdd.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                btnAdd.setText(context.getString(R.string.remove))
                btnAdd.layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.remove_from_schedule_height)
                btnAdd.isAllCaps = false
            } else {
                btnAdd.setBackgroundResource(R.drawable.background_corners)
                btnAdd.setTextColor(Color.WHITE)
                btnAdd.setText(context.getString(R.string.sub_event_add_to_schedule))
                btnAdd.layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.add_schedule_height)
                btnAdd.isAllCaps = true
            }

            val visibilityIsSpeaker: Int
            if (subEvent.isSpeaker) {
                visibilityIsSpeaker = View.VISIBLE
            } else {
                visibilityIsSpeaker = View.GONE
            }
            tvIsSpeaker.visibility = visibilityIsSpeaker
            ivStar.visibility = visibilityIsSpeaker

            btnAdd.setOnClickListener {
                if (subEvent.isInSchedule) {
                    presenter.removeFromeSchedule(subEvent)
                } else {
                    presenter.addToSchedule(subEvent)
                }
            }

            setOnClickListener { presenter.onSubeventClick(subEvent) }
        }
    }

    override fun getLayout() = R.layout.item_sub_event
}