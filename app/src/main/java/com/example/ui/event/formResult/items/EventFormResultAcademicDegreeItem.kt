package com.example.ui.event.formResult.items

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemEventFormResultAcademicDegreeBinding
import com.xwray.groupie.viewbinding.BindableItem

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

    override fun initializeViewBinding(view: View) = ItemEventFormResultAcademicDegreeBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_event_form_result_academic_degree
}