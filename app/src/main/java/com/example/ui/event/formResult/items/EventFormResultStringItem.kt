package com.example.ui.event.formResult.items

import android.text.method.LinkMovementMethod
import androidx.core.content.ContextCompat
import com.example.R
import com.example.databinding.ItemEventFormResultBinding
import com.xwray.groupie.databinding.BindableItem

class EventFormResultStringItem(
    val id: String?,
    val title: String?,
    val value: CharSequence?,
) : BindableItem<ItemEventFormResultBinding>(id?.toLong() ?: 0) {


    override fun bind(viewBinding: ItemEventFormResultBinding, position: Int) {
        viewBinding.apply {
            tvFormTitle.text = "$title"
            tvFormValue.apply {
                highlightColor = ContextCompat.getColor(context, R.color.event_tabs_text_unchecked)
                movementMethod = LinkMovementMethod.getInstance()
                text = value
            }

        }
    }


    override fun getLayout(): Int = R.layout.item_event_form_result
}