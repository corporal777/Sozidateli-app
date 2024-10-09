package com.example.holders.registerEvent

import android.util.Log
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemEventRegistrationFieldDescriptionBinding
import com.example.app.databinding.ItemRegisterEventInputBinding
import com.example.util.getColor
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.GroupieViewHolder

class EventRegistrationDescriptionItem(
    val title: String?,
    val description: String?
) : BindableItem<ItemEventRegistrationFieldDescriptionBinding>(
    if (description.isNullOrEmpty()) title.hashCode().toLong() else description.hashCode().toLong()
) {
    var isErrorShown = false

    override fun bind(viewBinding: ItemEventRegistrationFieldDescriptionBinding, position: Int) {
        viewBinding.apply {
            textViewTitle.apply {
                text = title
                isVisible = !title.isNullOrEmpty()

                if (isErrorShown) setTextColor(getColor(R.color.red_new))
                else setTextColor(getColor(R.color.profile_data_text_hint))
            }
            textViewDescription.apply {
                text = description
                isVisible = !description.isNullOrEmpty()
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (this === other) return true
        if (other !is EventRegistrationDescriptionItem) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (isErrorShown != other.isErrorShown) return false
        return true
    }


    override fun getLayout() = R.layout.item_event_registration_field_description

}