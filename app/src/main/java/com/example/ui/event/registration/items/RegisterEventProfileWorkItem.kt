package com.example.ui.event.registration.items

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.WorkExperience
import com.example.databinding.ItemRegisterEventProfileWorkBinding
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormatOrDefault
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS
import com.xwray.groupie.databinding.BindableItem
import java.text.SimpleDateFormat
import java.util.*

class RegisterEventProfileWorkItem(
    val itemId : Long,
    val isRequired: Boolean,
    val workExperienceAbsent: Boolean,
    val withTitle: Boolean,
    val work: WorkExperience?
) : BindableItem<ItemRegisterEventProfileWorkBinding>(itemId) {

    private var years = ""

    override fun bind(viewBinding: ItemRegisterEventProfileWorkBinding, position: Int) {
        viewBinding.apply {
            titleWorkExperience.isVisible = withTitle
            if (work != null && !workExperienceAbsent) {
                tvWorkDate.apply {
                    titleWorkDate.isVisible =
                        !work.begin.isNullOrEmpty() || !work.end.isNullOrEmpty()
                    isVisible = !work.begin.isNullOrEmpty() || !work.end.isNullOrEmpty()
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
                    years = context.getString(R.string.profile_dates, startYear, endYear)
                    text = years
                }
                tvOrganization.apply {
                    titleOrganization.isVisible = !work.organization.isNullOrEmpty()
                    isVisible = !work.organization.isNullOrEmpty()
                    text = work.organization?.trim()
                }

                tvPosition.apply {
                    titlePosition.isVisible = !work.position.isNullOrEmpty()
                    isVisible = !work.position.isNullOrEmpty()
                    text = work.position?.trim()
                }

            } else {
                lnWorkExperience.isVisible = false
                tvNoWorkExperience.apply {
                    isVisible = true
                    if (workExperienceAbsent){
                        setTextColor(ContextCompat.getColor(context, R.color.vk_black))
                        text = context.getString(R.string.no_experience)
                    }else {
                        if (isRequired) {
                            setTextColor(ContextCompat.getColor(context, R.color.red_new))
                        } else {
                            setTextColor(ContextCompat.getColor(context, R.color.vk_black))
                        }
                    }
                }
            }


        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is RegisterEventProfileWorkItem) return false
        if (isRequired != other.isRequired) return false
        if (workExperienceAbsent != other.workExperienceAbsent) return false
        if (withTitle != other.withTitle) return false
        if (work != other.work) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_register_event_profile_work
}