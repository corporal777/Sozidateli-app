package com.example.holders

import android.view.ViewGroup
import com.example.R
import com.example.data.models.user.SocialRoles
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.dp
import com.example.util.DATE_FORMAT_FULL_YEAR
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_education_data.*
import java.text.SimpleDateFormat
import java.util.*

class ProfileEducationDataItem(
        private val education: SocialRoles,
        private val compactTopMargin: Boolean
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvDates.apply {
                val startYear = findDate(education.begin, "")
                val endYear = findDate(education.end, resources.getString(R.string.profile_education_date_present))
                val years = resources.getString(R.string.profile_education_dates, startYear, endYear)
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

    private fun findDate(date: String?, default: String): String {
        if (date == null) return default
        val parsed = with(defaultServerDateFormatter) {
            try {
                parse(date)
            } catch (e: Throwable) {
                null
            }
        } ?: return default

        return SimpleDateFormat(DATE_FORMAT_FULL_YEAR, Locale.getDefault()).format(parsed)
    }

    override fun getLayout() = R.layout.item_profile_education_data
}