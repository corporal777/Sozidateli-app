package com.example.holders

import android.view.View
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemProfileDataEducationBinding
import com.example.data.models.EducationModel
import com.example.common.defaultServerDateFormatter
import com.example.common.parseAndFormatOrDefault
import com.example.common.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS
import com.xwray.groupie.viewbinding.BindableItem
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileDataEducationItem(
    private val education: EducationModel
) : BindableItem<ItemProfileDataEducationBinding>() {

    override fun bind(viewBinding: ItemProfileDataEducationBinding, position: Int) {
        viewBinding.apply {
            tvDates.apply {
                val format = SimpleDateFormat(
                    DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS,
                    Locale.getDefault()
                )
                val startYear =
                    education.begin.parseAndFormatOrDefault(defaultServerDateFormatter, format, "")
                val endYear = education.end.parseAndFormatOrDefault(
                    defaultServerDateFormatter,
                    format,
                    resources.getString(R.string.profile_date_present)
                )
                val years = resources.getString(R.string.profile_dates, startYear, endYear)
                text = years
            }

            tvSpecialtyTitle.apply {
                if (!education.speciality.isNullOrEmpty()) {
                    isVisible = true
                    tvSpecialty.text = education.speciality
                } else {
                    isVisible = false
                    tvSpecialty.isVisible = false
                }
            }

            tvInstitution.text = education.organization
        }
    }

    override fun initializeViewBinding(view: View) = ItemProfileDataEducationBinding.bind(view)
    override fun getLayout() = R.layout.item_profile_data_education
}