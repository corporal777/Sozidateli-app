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

            if (academicDegrees.isEmpty()) {
                tvDegreesTitle.isVisible = false
                tvDegreesLevel.isVisible = false
            } else {
                tvDegreesLevel.text = academicDegrees.joinToString(separator = "\n") { degree ->
                    "${degree.degree}, ${degree.specialisation}"
                }
                tvDegreesTitle.isVisible = true
                tvDegreesLevel.isVisible = true
            }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_education_level
}