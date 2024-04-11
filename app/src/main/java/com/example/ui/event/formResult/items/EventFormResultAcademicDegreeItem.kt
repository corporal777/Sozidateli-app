package com.example.ui.event.formResult.items

import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemEventFormResultAcademicDegreeBinding
import com.example.databinding.ItemEventFormResultPassportBinding
import com.xwray.groupie.databinding.BindableItem

class EventFormResultAcademicDegreeItem(
    val degree: String?,
    val spec: String?
) : BindableItem<ItemEventFormResultAcademicDegreeBinding>() {


    override fun bind(viewBinding: ItemEventFormResultAcademicDegreeBinding, position: Int) {
        viewBinding.apply {
            tvAcademicDegree.apply {
                if (degree.isNullOrEmpty()) {
                    alpha = 0.4f
                    text = "Не заполнено"
                } else text = degree
            }
            tvAcademicSpecialization.apply {
                if (spec.isNullOrEmpty()) {
                    alpha = 0.4f
                    text = "Не заполнено"
                } else text = spec
            }
        }
    }


    override fun getLayout(): Int = R.layout.item_event_form_result_academic_degree
}