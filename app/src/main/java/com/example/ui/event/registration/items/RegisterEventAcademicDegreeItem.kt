package com.example.ui.event.registration.items

import com.example.R
import com.example.databinding.ItemRegisterEventProfileAcademicDegreeBinding
import com.xwray.groupie.databinding.BindableItem

class RegisterEventAcademicDegreeItem(
    val academicDegree: String?,
    val academicSpeciality: String?
) : BindableItem<ItemRegisterEventProfileAcademicDegreeBinding>() {


    override fun bind(viewBinding: ItemRegisterEventProfileAcademicDegreeBinding, position: Int) {
        viewBinding.apply {
            tvAcademicDegree.text = academicDegree
            tvAcademicSpeciality.text = academicSpeciality
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is RegisterEventAcademicDegreeItem) return false
        if (academicDegree != other.academicDegree) return false
        if (academicSpeciality != other.academicSpeciality) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_register_event_profile_academic_degree
}