package com.example.ui.event.formResult.items

import com.example.app.R
import com.example.app.databinding.ItemEventFormResultEducationBinding
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormatOrDefault
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS
import com.xwray.groupie.databinding.BindableItem
import java.text.SimpleDateFormat
import java.util.Locale

class EventFormResultEducationItem(
    val organization: String?,
    val spec: String?,
    val educationBegin: String?,
    val educationEnd: String?
) : BindableItem<ItemEventFormResultEducationBinding>() {


    override fun bind(viewBinding: ItemEventFormResultEducationBinding, position: Int) {
        viewBinding.apply {

            tvEducationOrganization.apply {
                if (organization.isNullOrEmpty()) {
                    alpha = 0.4f
                    text = "Не заполнено"
                } else text = organization
            }

            tvEducationSpeciality.apply {
                if (spec.isNullOrEmpty()) {
                    alpha = 0.4f
                    text = "Не заполнено"
                } else text = spec
            }

            tvEducationDate.apply {
                val format = SimpleDateFormat(
                    DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS,
                    Locale.getDefault()
                )
                val startYear = educationBegin.parseAndFormatOrDefault(
                    defaultServerDateFormatter,
                    format,
                    ""
                )
                val endYear = educationEnd.parseAndFormatOrDefault(
                    defaultServerDateFormatter,
                    format,
                    resources.getString(R.string.profile_date_present)
                )
                val years = context.getString(R.string.profile_dates, startYear, endYear)
                text = years
            }
        }
    }


    override fun getLayout(): Int = R.layout.item_event_form_result_education
}