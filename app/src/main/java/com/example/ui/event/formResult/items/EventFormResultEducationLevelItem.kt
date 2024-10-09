package com.example.ui.event.formResult.items

import android.text.method.LinkMovementMethod
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.app.R
import com.example.data.models.EventPassport
import com.example.app.databinding.ItemEventFormResultEducationLevelBinding
import com.example.app.databinding.ItemEventFormResultPassportBinding
import com.example.extensions.formatToDefaultDate
import com.xwray.groupie.databinding.BindableItem

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


    override fun getLayout(): Int = R.layout.item_event_form_result_education_level
}