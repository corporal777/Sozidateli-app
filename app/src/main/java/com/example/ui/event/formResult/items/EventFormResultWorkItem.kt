package com.example.ui.event.formResult.items

import androidx.core.view.isVisible
import com.example.app.R
import com.example.data.models.WorkExperience
import com.example.app.databinding.ItemEventFormResultWorkBinding
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormatOrDefault
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS
import com.xwray.groupie.databinding.BindableItem
import java.text.SimpleDateFormat
import java.util.Locale

class EventFormResultWorkItem (
    val withHeader : Boolean,
    val work: WorkExperience?
) : BindableItem<ItemEventFormResultWorkBinding>() {


    override fun bind(viewBinding: ItemEventFormResultWorkBinding, position: Int) {
        viewBinding.apply {
            tvWorkFormTitle.isVisible = withHeader

            tvWorkDate.apply {
                val format = SimpleDateFormat(
                    DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS,
                    Locale.getDefault()
                )
                val startYear = work?.begin.parseAndFormatOrDefault(defaultServerDateFormatter, format, "")
                val endYear = work?.end.parseAndFormatOrDefault(defaultServerDateFormatter, format, resources.getString(R.string.profile_date_present))
                val years = context.getString(R.string.profile_dates, startYear, endYear)

                if (years.isNullOrEmpty()) {
                    alpha = 0.4f
                    text = "Не заполнено"
                } else text = years
            }

            tvWorkOrganization.apply {
                val organization = work?.organization?.trim()
                if (organization.isNullOrEmpty()) {
                    alpha = 0.4f
                    text = "Не заполнено"
                } else text = organization
            }

            tvWorkPosition.apply {
                val workPosition = work?.position?.trim()
                if (workPosition.isNullOrEmpty()) {
                    alpha = 0.4f
                    text = "Не заполнено"
                } else text = workPosition
            }
        }
    }


    override fun getLayout(): Int = R.layout.item_event_form_result_work
}