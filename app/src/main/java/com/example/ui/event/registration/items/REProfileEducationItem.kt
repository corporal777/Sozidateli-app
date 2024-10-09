package com.example.ui.event.registration.items

import com.example.app.R
import com.example.data.models.EducationModel
import com.example.app.databinding.ItemRegisterEventProfileEducationBinding
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormatOrDefault
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS
import com.xwray.groupie.databinding.BindableItem
import java.text.SimpleDateFormat
import java.util.*

class REProfileEducationItem(
    val itemId : Int?,
    val education : EducationModel,
    val onClick: () -> Unit
) : BindableItem<ItemRegisterEventProfileEducationBinding>((itemId ?: 0).toLong()) {


    override fun bind(viewBinding: ItemRegisterEventProfileEducationBinding, position: Int) {
        viewBinding.apply {
            btnActionOrg.setOnClickListener { onClick.invoke() }
            btnActionSpec.setOnClickListener { onClick.invoke() }
            btnActionDate.setOnClickListener { onClick.invoke() }

            educationOrgTextView.apply {
                text = education.organization
            }
            educationSpecTextView.apply {
                text = education.speciality
            }
            educationDateTextView.apply {
                val format = SimpleDateFormat(
                    DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS,
                    Locale.getDefault()
                )
                val startYear = education.begin.parseAndFormatOrDefault(
                    defaultServerDateFormatter,
                    format,
                    ""
                )
                val endYear = education.end.parseAndFormatOrDefault(
                    defaultServerDateFormatter,
                    format,
                    resources.getString(R.string.profile_date_present)
                )
                val years = context.getString(R.string.profile_dates, startYear, endYear)
                text = years
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is REProfileEducationItem) return false
        if (education != other.education) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_register_event_profile_education
}