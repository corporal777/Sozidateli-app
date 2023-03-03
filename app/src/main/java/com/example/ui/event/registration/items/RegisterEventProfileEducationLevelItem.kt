package com.example.ui.event.registration.items

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemRegisterEventProfileEducationLevelBinding
import com.example.ui.event.about.redesign.items.EventDetailActionItem
import com.xwray.groupie.databinding.BindableItem

class RegisterEventProfileEducationLevelItem(
    val itemId : Long,
    val educationLevel: String?,
    val isRequired: Boolean,
    val educationIsNull: Boolean,
    val isAcademicDegreeNull: Boolean
) : BindableItem<ItemRegisterEventProfileEducationLevelBinding>(itemId) {

    override fun bind(viewBinding: ItemRegisterEventProfileEducationLevelBinding, position: Int) {
        viewBinding.apply {
            lnEducationLevel.isVisible = !educationLevel.isNullOrEmpty()
            tvEducationLevel.apply {
                text = educationLevel
            }

            tvNoEducation.apply {
                isVisible = educationIsNull && isAcademicDegreeNull
                if (isRequired) {
                    tvNoEducation.setTextColor(ContextCompat.getColor(context, R.color.red_new))
                } else {
                    tvNoEducation.setTextColor(ContextCompat.getColor(context, R.color.vk_black))
                }
            }

        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is RegisterEventProfileEducationLevelItem) return false
        if (educationLevel != other.educationLevel) return false
        if (isRequired != other.isRequired) return false
        if (educationIsNull != other.educationIsNull) return false
        if (isAcademicDegreeNull != other.isAcademicDegreeNull) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_register_event_profile_education_level
}