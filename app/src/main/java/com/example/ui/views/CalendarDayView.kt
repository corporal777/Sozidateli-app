package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.app.R
import com.example.data.models.EventScheduleDay
import com.example.app.databinding.ItemDayBinding
import com.example.extensions.textColor

class CalendarDayView : LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private var onDaySelect: (day: EventScheduleDay) -> Unit = {}
    private val binding = ItemDayBinding.inflate(LayoutInflater.from(context), this, true)

    var isDaySelected = false
    private var day: EventScheduleDay? = null


    init {

    }

    fun setDay(scheduleDay : EventScheduleDay){
        day = scheduleDay
        updateView()
    }
    fun getDay() = day

    fun updateView(){
        binding.apply {
            if (day?.hasEvents == false){
                tvDayName.textColor = R.color.input_text_color_disabled
                tvDayNumber.setTextColor(context.getColorStateList(R.color.text_color_calendar_day_disabled))
            } else {
                tvDayName.textColor = R.color.black
                tvDayNumber.setTextColor(context.getColorStateList(R.color.text_color_calendar_day))
            }

            tvDayName.text = day?.dayOfWeek
            tvDayNumber.apply {
                isSelected = isDaySelected
                text = day?.dayOfMonth.toString()
            }
            root.setOnClickListener { performSelectClick(day!!) }
        }
    }

    private fun performSelectClick(day : EventScheduleDay) {
        if (isDaySelected) return
        isDaySelected = true
        onDaySelect.invoke(day)
    }


    fun setOnDaySelected(block: (day: EventScheduleDay) -> Unit) {
        onDaySelect = block
    }
}