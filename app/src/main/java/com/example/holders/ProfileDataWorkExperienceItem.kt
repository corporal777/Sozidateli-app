package com.example.holders

import android.view.ViewGroup
import com.example.R
import com.example.data.models.user.SocialRoles
import com.example.extensions.dp
import com.example.extensions.formatServerDateOrDefault
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_profile_data_work_experience.*
import setTextDataOrHide

class ProfileDataWorkExperienceItem(
        private val work: SocialRoles,
        private val compactTopMargin: Boolean
) : Item() {

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvDates.apply {
                val startYear = work.begin.formatServerDateOrDefault(DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE, "")
                val endYear = work.end.formatServerDateOrDefault(DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE, resources.getString(R.string.profile_date_present))
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

            tvProject.text = work.organization ?: "-"
            tvPosition.text = work.position ?: "-"
            groupDescription.setTextDataOrHide(tvDescription, work.description)
        }
    }

    override fun getLayout() = R.layout.item_profile_data_work_experience
}