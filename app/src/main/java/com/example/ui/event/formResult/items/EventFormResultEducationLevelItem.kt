package com.example.ui.event.formResult.items

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemEventFormResultEducationLevelBinding
import com.xwray.groupie.viewbinding.BindableItem

class EventFormResultEducationLevelItem (
    val educationLevel: String?,
) : BindableItem<ItemEventFormResultEducationLevelBinding>() {


    override fun bind(viewBinding: ItemEventFormResultEducationLevelBinding, position: Int) {
        viewBinding.apply {
            tvEducationLevel.apply {
                if (educationLevel.isNullOrEmpty()){
                    alpha = 0.4f
                    text = "Не заполнено"
                } else text = educationLevel
            }
        }
    }

    override fun initializeViewBinding(view: View) = ItemEventFormResultEducationLevelBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_event_form_result_education_level
}