package com.example.ui.event.formResult.items

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemEventFormResultStringBinding
import com.xwray.groupie.viewbinding.BindableItem

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

    override fun initializeViewBinding(view: View) = ItemEventFormResultStringBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_event_form_result_string
}