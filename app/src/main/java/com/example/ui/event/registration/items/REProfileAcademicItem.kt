package com.example.ui.event.registration.items

import com.example.app.R
import com.example.data.models.PrefilledFieldAcademicDegreeModel
import com.example.app.databinding.ItemRegisterEventProfileAcademicDegreeBinding
import com.xwray.groupie.databinding.BindableItem

class REProfileAcademicItem(
    val itemId : Int?,
    val degree : PrefilledFieldAcademicDegreeModel,
    val onClick: () -> Unit
) : BindableItem<ItemRegisterEventProfileAcademicDegreeBinding>((itemId ?: 0).toLong()) {


    override fun bind(viewBinding: ItemRegisterEventProfileAcademicDegreeBinding, position: Int) {
        viewBinding.apply {
            btnActionDegree.setOnClickListener { onClick.invoke() }
            btnActionSpec.setOnClickListener { onClick.invoke() }
            academicDegreeTextView.apply {
                text = degree.degree
            }
            academicSpecTextView.apply {
                text = degree.degree
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is REProfileAcademicItem) return false
        if (degree != other.degree) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_register_event_profile_academic_degree
}