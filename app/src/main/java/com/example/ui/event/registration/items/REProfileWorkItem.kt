package com.example.ui.event.registration.items

import android.view.View
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemRegisterEventProfileWorkBinding
import com.example.data.models.WorkExperience
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseAndFormatOrDefault
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS
import com.xwray.groupie.viewbinding.BindableItem
import java.text.SimpleDateFormat
import java.util.Locale

class REProfileWorkItem(
    val itemId : Int?,
    val work: WorkExperience,
    val index: Int,
    val onClick: () -> Unit
) : BindableItem<ItemRegisterEventProfileWorkBinding>(itemId?.toLong() ?: 0) {

    override fun bind(viewBinding: ItemRegisterEventProfileWorkBinding, position: Int) {
        viewBinding.apply {
            btnActionDate.setOnClickListener { onClick.invoke() }
            btnActionOrg.setOnClickListener { onClick.invoke() }
            btnActionPosition.setOnClickListener { onClick.invoke() }

            workTitleTextView.isVisible = index == 0
            workDateTextView.apply {
                val format = SimpleDateFormat(DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE_NUMBERS, Locale.getDefault())
                val startYear = work.begin.parseAndFormatOrDefault(defaultServerDateFormatter, format, "")
                val endYear = work.end.parseAndFormatOrDefault(defaultServerDateFormatter, format, resources.getString(R.string.profile_date_present))
                val years = context.getString(R.string.profile_dates, startYear, endYear)
                text = years
            }
            workOrgTextView.apply {
                text = work.organization?.trim()
            }
            workPositionTextView.apply {
                text = work.position?.trim()
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is REProfileWorkItem) return false
        if (index != other.index) return false
        if (work != other.work) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemRegisterEventProfileWorkBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_register_event_profile_work
}