package com.example.holders

import android.view.ViewGroup
import com.example.app.R
import com.example.data.models.WorkExperience
import com.example.app.databinding.ItemProfileDataWorkExperienceBinding
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.dp
import com.example.extensions.parseAndFormatOrDefault
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS
import com.xwray.groupie.databinding.BindableItem
import java.text.SimpleDateFormat
import java.util.*

class ProfileDataWorkExperienceItem(
    private val work: WorkExperience
) : BindableItem<ItemProfileDataWorkExperienceBinding>() {

    override fun bind(viewBinding: ItemProfileDataWorkExperienceBinding, position: Int) {
        viewBinding.apply {
            tvDates.apply {
                val format = SimpleDateFormat(
                    DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS,
                    Locale.getDefault()
                )
                val startYear =
                    work.begin.parseAndFormatOrDefault(defaultServerDateFormatter, format, "")
                val endYear = work.end.parseAndFormatOrDefault(
                    defaultServerDateFormatter,
                    format,
                    resources.getString(R.string.profile_date_present)
                )
                val years = resources.getString(R.string.profile_dates, startYear, endYear)
                text = years
            }

            tvProject.text = work.organization?.trim() ?: "-"
            tvPosition.text = work.position?.trim() ?: "-"
        }
    }

    override fun getLayout() = R.layout.item_profile_data_work_experience
}