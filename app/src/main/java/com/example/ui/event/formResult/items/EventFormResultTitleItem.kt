package com.example.ui.event.formResult.items

import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemEventFormResultStringBinding
import com.xwray.groupie.databinding.BindableItem

class EventFormResultTitleItem (
    val id: String?,
    val title: String?
) : BindableItem<ItemEventFormResultStringBinding>(id?.toLong() ?: 0) {


    override fun bind(viewBinding: ItemEventFormResultStringBinding, position: Int) {
        viewBinding.apply {
            tvFormTitle.text = "$title"
            tvFormValue.isVisible = false
        }
    }


    override fun getLayout(): Int = R.layout.item_event_form_result_string
}