package com.example.holders

import android.view.ViewGroup
import com.example.R
import com.example.data.models.user.SocialRoles
import com.example.extensions.dp
import com.example.extensions.formatServerDateOrDefault
import com.example.util.DATE_FORMAT_FULL_YEAR
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_education.*

class ProfileDataEducationItem(
        private val education: SocialRoles,
        private val compactTopMargin: Boolean
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvDates.apply {
                val startYear = education.begin.formatServerDateOrDefault(DATE_FORMAT_FULL_YEAR, "")
                val endYear = education.end.formatServerDateOrDefault(DATE_FORMAT_FULL_YEAR, resources.getString(R.string.profile_date_present))
                val years = resources.getString(R.string.profile_dates, startYear, endYear)
                text = years

                (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                    setMargins(
                            leftMargin,
                            if (compactTopMargin) 5.dp else 20.dp,
                            rightMargin,
                            bottomMargin
                    )
                }
            }

            tvSpecialty.text = education.specialty
            tvInstitution.text = education.organization
        }
    }

    override fun getLayout() = R.layout.item_profile_data_education
}