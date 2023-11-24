package com.example.holders

import com.example.R
import com.example.data.models.EventScheduleDay
import com.example.databinding.ItemDayBinding
import com.xwray.groupie.databinding.BindableItem
import kotlinx.android.synthetic.main.item_day.*


class EventDayItem(
    val day: EventScheduleDay,
    private val onDaySelect: (date: EventScheduleDay) -> Unit
) : BindableItem<ItemDayBinding>() {

    var isDaySelected = false

    override fun bind(viewBinding: ItemDayBinding, position: Int) {
        viewBinding.apply {
            if (!day.hasEvents){
                tvDayName.setTextColor(root.resources.getColorStateList(R.color.input_text_color_disabled))
                tvDayNumber.setTextColor(root.resources.getColorStateList(R.color.text_color_calendar_day_disabled))
            }else {
                tvDayName.setTextColor(root.resources.getColorStateList(R.color.black))
                tvDayNumber.setTextColor(root.resources.getColorStateList(R.color.text_color_calendar_day))
            }

            tvDayName.text = day.dayOfWeek
            tvDayNumber.apply {
                isSelected = isDaySelected
                text = day.dayOfMonth.toString()
                setOnClickListener { performSelectClick() }
            }
        }
    }

    private fun performSelectClick() {
        if (isDaySelected) return
        isDaySelected = true
        onDaySelect.invoke(day)
        notifyChanged()
    }

    override fun getLayout() = R.layout.item_day
}