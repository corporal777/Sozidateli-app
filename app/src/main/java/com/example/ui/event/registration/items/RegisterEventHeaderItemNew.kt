package com.example.ui.event.registration.items

import android.util.Log
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemRegisterEventHeaderNewBinding
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.util.markWon
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem
import java.util.*

class RegisterEventHeaderItemNew(
    id: Long,
    private val image: String,
    private val formTitle: String?,
    private val formDescription: String?,
    private val date: String,
) : BindableItem<ItemRegisterEventHeaderNewBinding>(id) {

    private var eventStartDate = ""

    init {
        val cal = defaultServerDateFormatter.parse(date).time.calendar()
        eventStartDate = "Дата проведения " + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.getDisplayName(
            Calendar.MONTH,
            Calendar.LONG,
            Locale.getDefault()
        ) + " " + cal.get(Calendar.YEAR) + " г."
    }

    override fun bind(viewBinding: ItemRegisterEventHeaderNewBinding, position: Int) {
        viewBinding.apply {

            ivLogo.setImage(image)
            tvFormLabel.apply {
                isVisible = !formTitle.isNullOrBlank()
                text = formTitle
            }
            tvEventDate.text = eventStartDate
            tvFormDescription.apply {
                isVisible = !formDescription.isNullOrBlank()
                markWon(context).setMarkdown(this, formDescription ?: "")
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_register_event_header_new
}