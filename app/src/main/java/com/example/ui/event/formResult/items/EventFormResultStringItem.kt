package com.example.ui.event.formResult.items

import android.text.method.LinkMovementMethod
import androidx.core.content.ContextCompat
import com.example.R
import com.example.databinding.ItemEventFormResultStringBinding
import com.xwray.groupie.databinding.BindableItem

class EventFormResultStringItem(
    val id: String?,
    val title: String?,
    val value: CharSequence?,
) : BindableItem<ItemEventFormResultStringBinding>(id?.toLong() ?: 0) {

    private val fieldText = if (value.isNullOrEmpty()) "Не заполнено" else  value
    private val fieldAlpha = if (value.isNullOrEmpty()) 0.4f else 1.0f

    override fun bind(viewBinding: ItemEventFormResultStringBinding, position: Int) {
        viewBinding.apply {
            tvFormTitle.text = "$title"
            tvFormValue.apply {
                text = fieldText
                alpha = fieldAlpha
            }

        }
    }


    override fun getLayout(): Int = R.layout.item_event_form_result_string
}