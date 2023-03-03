package com.example.ui.event.registration.items

import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EducationModel
import com.example.databinding.ItemRegisterEventProfileEducationBinding
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormatOrDefault
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS
import com.xwray.groupie.databinding.BindableItem
import java.text.SimpleDateFormat
import java.util.*

class RegisterEventEducationItem(
    val itemId : Long,
    val educationOrganization : String?,
    val educationSpeciality : String?,
    val educationBegin : String?,
    val educationEnd: String?
) : BindableItem<ItemRegisterEventProfileEducationBinding>(itemId) {


    override fun bind(viewBinding: ItemRegisterEventProfileEducationBinding, position: Int) {
        viewBinding.apply {
            tvOrganization.apply {
                titleOrganization.isVisible = !educationOrganization.isNullOrEmpty()
                isVisible = !educationOrganization.isNullOrEmpty()
                text = educationOrganization
            }
            tvSpeciality.apply {
                titleSpeciality.isVisible = !educationSpeciality.isNullOrEmpty()
                isVisible = !educationSpeciality.isNullOrEmpty()
                text = educationSpeciality
            }
            tvEducationDate.apply {
                titleEducationDate.isVisible = !educationBegin.isNullOrEmpty() || !educationEnd.isNullOrEmpty()
                isVisible = !educationBegin.isNullOrEmpty() || !educationEnd.isNullOrEmpty()
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
//            if (!educationIsNul){
//
//            }else {
//                lnEducation.isVisible = false
//            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is RegisterEventEducationItem) return false
        if (educationOrganization != other.educationOrganization) return false
        if (educationSpeciality != other.educationSpeciality) return false
        if (educationBegin != other.educationBegin) return false
        if (educationEnd != other.educationEnd) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_register_event_profile_education
}