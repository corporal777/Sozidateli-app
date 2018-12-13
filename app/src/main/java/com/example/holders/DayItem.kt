package com.example.holders

import android.graphics.Color
import android.widget.TextView
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_day.view.*

open class DayItem(private val dayNumber: Int, private val dayString: String,private val date:Long, var selectedPosition:Int, private val onSelectItem: (positionSelected: Int,date:Long) -> Unit) : Item() {

    var isSelected = false

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            this@DayItem.isSelected  = selectedPosition == position
            tvDayName.text = dayString
            tvDayNumber.text = dayNumber.toString()
            select(tvDayNumber)
            tvDayNumber.setOnClickListener {
                onSelectItem(position,date)
                this@DayItem.isSelected = !this@DayItem.isSelected
                select(tvDayNumber)
            }
        }
    }

    private fun select(textView: TextView) {
        if (isSelected) {
            textView.setBackgroundResource(R.drawable.background_selected_date)
            textView.setTextColor(Color.WHITE)
        } else {
            textView.background = null
            textView.setTextColor(Color.BLACK)
        }
    }

    override fun getLayout() = R.layout.item_day
}