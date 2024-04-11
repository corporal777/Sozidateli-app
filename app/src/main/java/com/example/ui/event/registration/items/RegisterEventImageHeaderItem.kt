package com.example.ui.event.registration.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemRegisterEventHeaderNewBinding
import com.example.extensions.formatToDefaultDate
import com.example.extensions.markWon
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem
import com.example.extensions.parseColor

class RegisterEventImageHeaderItem(
    id: Long,
    private val logo: String?,
    private val backgroundColor: String?,
    private val formTitle: String?,
    private val formDescription: String?,
    private val dateFrom: String?,
    private val dateTo: String?,
) : BindableItem<ItemRegisterEventHeaderNewBinding>(id) {

    private val eventStartDate =
        "Дата проведения " + dateFrom?.formatToDefaultDate() + " - " + dateTo?.formatToDefaultDate()
    private val imageColor = ColorDrawable(backgroundColor.parseColor() ?: Color.DKGRAY)

    init {

    }

    override fun bind(viewBinding: ItemRegisterEventHeaderNewBinding, position: Int) {
        viewBinding.apply {
            ivLogo.setImage(logo ?: imageColor)
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