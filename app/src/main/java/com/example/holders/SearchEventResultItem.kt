package com.example.holders

import android.support.v4.content.ContextCompat
import android.text.InputType
import android.view.View
import com.example.R
import com.example.data.models.Event
import com.example.data.models.Status
import com.example.ui.search.SearchContract
import com.example.ui.search.SearchPresenter
import com.example.util.Utils
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_event.*
import kotlinx.android.synthetic.main.item_search_event_result.view.*

open class SearchEventResultItem(private val event: Event, private val presenter: SearchContract.Presenter) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            tvOrganizationName.text = event.organizationName
            tvEventName.text = event.name
            tvEventDate.text = Utils.getDatesInterval(event.startDate, event.finishDate)

            setOnClickListener { presenter.onEventClick(event) }
        }
    }

    override fun getLayout() = R.layout.item_search_event_result
}