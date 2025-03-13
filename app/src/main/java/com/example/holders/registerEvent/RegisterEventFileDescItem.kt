package com.example.holders.registerEvent

import android.view.View
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemEventRegistrationFieldDescriptionBinding
import com.example.extensions.textColor
import com.example.util.getColor
import com.xwray.groupie.viewbinding.BindableItem

class RegisterEventFileDescItem(
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
                textColor = if (isErrorShown) R.color.red_new else R.color.profile_data_text_hint
            }
            textViewDescription.apply {
                text = description
                isVisible = !description.isNullOrEmpty()
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (this === other) return true
        if (other !is RegisterEventFileDescItem) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (isErrorShown != other.isErrorShown) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemEventRegistrationFieldDescriptionBinding.bind(view)
    override fun getLayout() = R.layout.item_event_registration_field_description

}