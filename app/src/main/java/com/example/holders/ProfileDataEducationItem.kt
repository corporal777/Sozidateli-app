package com.example.holders

import com.example.R
import com.example.data.models.EducationModel
import com.example.data.models.user.SocialRoles
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormatOrDefault
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_education.*
import java.text.SimpleDateFormat
import java.util.*

class ProfileDataEducationItem(
        private val education: EducationModel
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvDates.apply {
                val format = SimpleDateFormat(DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS, Locale.getDefault())
                val startYear = education.begin.parseAndFormatOrDefault(defaultServerDateFormatter, format, "")
                val endYear = education.end.parseAndFormatOrDefault(defaultServerDateFormatter, format, resources.getString(R.string.profile_date_present))
                val years = resources.getString(R.string.profile_dates, startYear, endYear)
                text = years
            }

            tvSpecialty.text = education.speciality
            tvInstitution.text = education.organization
        }
    }

    override fun getLayout() = R.layout.item_profile_data_education
}