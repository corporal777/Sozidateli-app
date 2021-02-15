package com.example.holders

import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.AcademicDegree
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_education_level.*

class ProfileDataEducationLevelItem(
        private val education: String?,
        private val academicDegrees: List<AcademicDegree>
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvEducationLevel.text = education

            if ((academicDegrees.size == 1 && academicDegrees[0].degree == "") || academicDegrees.isEmpty()) {
                tvDegreesTitle.isVisible = false
                tvDegreesLevel.isVisible = false
                tvSpecializationTitle.isVisible = false
                tvSpecializationLevel.isVisible = false
            } else {
                tvDegreesLevel.text = academicDegrees.joinToString(separator = "\n") { degree ->
                    "${degree.degree}"
                }
                val sp = academicDegrees.joinToString() { item ->
                    "${item.specialisation}"
                }
                if (sp.isEmpty()) {
                    tvSpecializationTitle.isVisible = false
                    tvSpecializationLevel.isVisible = false
                } else {
                    tvSpecializationLevel.text = academicDegrees.joinToString(separator = "\n") { degree ->
                        "${degree.specialisation}"
                    }
                    tvSpecializationTitle.isVisible = true
                    tvSpecializationLevel.isVisible = true
                }
                
                tvDegreesTitle.isVisible = true
                tvDegreesLevel.isVisible = true
            }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_education_level
}