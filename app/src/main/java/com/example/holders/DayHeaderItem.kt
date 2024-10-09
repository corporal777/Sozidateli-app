package com.example.holders

import com.example.app.R
import com.example.app.databinding.ItemDayHeaderBinding
import com.example.extensions.firstLetterToUppercase
import com.xwray.groupie.databinding.BindableItem
import java.text.SimpleDateFormat
import java.util.*

open class DayHeaderItem(
    val date: Long,
    val isDay: Boolean = true
) : BindableItem<ItemDayHeaderBinding>(date) {

    private val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())

    private val dateFormatWithoutDay = SimpleDateFormat("d.MM.yyyy", Locale.getDefault())

    override fun bind(viewBinding: ItemDayHeaderBinding, position: Int) {
        viewBinding.apply {
            tvDate.text = dateFormat.format(date).firstLetterToUppercase()
//            if (isDay) tvDate.text = dateFormat.format(date).capitalize()
//            else tvDate.text = dateFormatWithoutDay.format(date).capitalize()
        }
    }


    override fun getLayout() = R.layout.item_day_header
}