package com.example.holders

import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.AcademicDegree
import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationLevel
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_education_level.*

class ProfileDataEducationLevelItem(
        private val education: String?,
        private val academicDegrees: List<AcademicDegreeModel>,
        private val availableDegrees: List<EducationLevel>,
        private val availableSpecialities: List<EducationLevel>
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvEducationLevel.text = education

            if ((academicDegrees.size == 1 && academicDegrees[0].degree == 0) || academicDegrees.isEmpty()) {
                tvDegreesTitle.isVisible = false
                tvDegreesLevel.isVisible = false
                tvSpecializationTitle.isVisible = false
                tvSpecializationLevel.isVisible = false
            } else {
                tvDegreesLevel.text = academicDegrees.joinToString(separator = "\n") { degree ->
                    "${availableDegrees.firstOrNull { item -> item.id == degree.degree }?.name}"
                }
                val sp = academicDegrees.joinToString(separator = "") { item ->
                    if (item.speciality != null) "${item.speciality}" else ""
                }
                if (sp.isEmpty()) {
                    tvSpecializationTitle.isVisible = false
                    tvSpecializationLevel.isVisible = false
                } else {
                    tvSpecializationLevel.text = academicDegrees.joinToString(separator = "\n") { degree ->
                        val degre = availableSpecialities.firstOrNull { item -> item.id == degree.speciality }?.name
                        degre ?: ""
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