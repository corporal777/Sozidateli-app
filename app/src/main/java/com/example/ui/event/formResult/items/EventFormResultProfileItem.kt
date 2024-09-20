package com.example.ui.event.formResult.items

import android.text.method.LinkMovementMethod
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemEventFormResultProfileBinding
import com.xwray.groupie.databinding.BindableItem

class EventFormResultProfileItem(
    val id : Int,
    val header : String?,
    val title: String?,
    val value: CharSequence?,
) : BindableItem<ItemEventFormResultProfileBinding>(id.toLong()) {

    private val fieldText = if (value.isNullOrEmpty()) "Не заполнено" else  value
    private val fieldAlpha = if (value.isNullOrEmpty()) 0.4f else 1.0f

    override fun bind(viewBinding: ItemEventFormResultProfileBinding, position: Int) {
        viewBinding.apply {
            tvFormTitle.apply {
                text = header
                isVisible = !header.isNullOrEmpty()
            }

            tvProfileFormTitle.apply {
                isVisible = header.isNullOrEmpty()
                text = title
            }
            tvProfileFormField.apply {
                highlightColor = ContextCompat.getColor(context, R.color.event_tabs_text_unchecked)
                movementMethod = LinkMovementMethod.getInstance()

                isVisible = header.isNullOrEmpty()
                text = fieldText
                alpha = fieldAlpha
            }
        }
    }


    override fun getLayout(): Int = R.layout.item_event_form_result_profile
}