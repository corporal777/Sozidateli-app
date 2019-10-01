package com.example.holders

import com.example.R
import com.example.data.models.user.SocialRoles
import com.example.extensions.formatServerDateOrDefault
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_education.*

class ProfileDataEducationItem(
        private val education: SocialRoles
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvDates.apply {
                val startYear = education.begin.formatServerDateOrDefault(DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE, "")
                val endYear = education.end.formatServerDateOrDefault(DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE, resources.getString(R.string.profile_date_present))
                val years = resources.getString(R.string.profile_dates, startYear, endYear)
                text = years
            }

            tvSpecialty.text = education.specialty
            tvInstitution.text = education.organization
        }
    }

    override fun getLayout() = R.layout.item_profile_data_education
}