package com.example.holders

import android.widget.CheckBox
import android.widget.EditText
import com.example.R
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.isSameMonth
import com.example.extensions.parseToDate
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE
import com.example.util.DATE_FORMAT_SERVER_TIMESTAMP
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import initAsMonthYearPicker
import kotlinx.android.synthetic.main.item_profile_data_edit_work.*
import onTextChanged
import java.text.SimpleDateFormat
import java.util.*

class ProfileDataWorkEditItem(
        start: String?,
        finish: String?,
        organization: String?,
        position: String?,
        private val onRemoveClickListener: (ProfileDataWorkEditItem) -> Unit
) : Item() {

    var mStart = start
        private set
    var mFinish = finish
        private set
    var mOrganization = organization
        private set
    var mPosition = position
        private set
    var isNotFinished = mFinish == null
        private set

    private val now = Date()

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            val startDate = mStart?.parseToDate(defaultServerDateFormatter)
            etStart.setText(startDate?.let { formatDate(it).capitalize() })
            tilStart.initAsMonthYearPicker(startDate, maxDate = now) { year, month, day ->
                tilStart.error = null
                mStart = formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month, day)
                formatDate(DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE, year, month, day).capitalize()
            }

            val finishDate = mFinish?.parseToDate(defaultServerDateFormatter)
            etFinish.setText(finishDate?.let { formatDate(it).capitalize() })
            tilFinish.initAsMonthYearPicker(finishDate, maxDate = now) { year, month, day ->
                tilFinish.error = null
                mFinish = formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month, day)
                formatDate(DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE, year, month, day).capitalize()
            }

            setFinishEnabled(this, !isNotFinished)
            scFinish.initSwitch(isNotFinished) {
                tilFinish.error = null
                setFinishEnabled(this, !it)
            }
            etProject.initInput(mOrganization) {
                tilProject.error = null
                mOrganization = it.toString()
            }
            etPosition.initInput(mPosition) {
                tilPosition.error = null
                mPosition = it.toString()
            }

            btnRemove.setOnClickListener { onRemoveClickListener(this@ProfileDataWorkEditItem) }
        }
    }

    override fun bind(holder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) super.bind(holder, position, payloads)
        else {
            holder.apply {
                if (!isStartValid()) tilStart.apply {
                    error = resources.getString(R.string.profile_work_start_error)
                }
                if (!isFinishValid()) tilFinish.apply {
                    error = resources.getString(R.string.profile_work_finish_error)
                }
                if (!isOrganizationValid()) tilProject.apply {
                    error = resources.getString(R.string.profile_edit_empty_field_error)
                }
                if (!isPositionValid()) tilPosition.apply {
                    error = resources.getString(R.string.profile_edit_empty_field_error)
                }
            }
        }
    }

    private fun EditText.initInput(text: String?, onTextChanged: (text: CharSequence?) -> Unit) {
        setText(text)
        onTextChanged(onTextChanged)
    }

    private fun CheckBox.initSwitch(checked: Boolean, onCheckedChanged: (isChecked: Boolean) -> Unit) {
        isChecked = checked
        setOnCheckedChangeListener { _, isChecked -> onCheckedChanged(isChecked) }
    }

    private fun formatDate(date: Date): String {
        return formatDate(DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE, date.time)
    }

    private fun formatDate(format: String, year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }

        return formatDate(format, calendar.timeInMillis)
    }

    private fun formatDate(format: String, date: Long): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(date)
    }

    private fun setFinishEnabled(viewHolder: GroupieViewHolder, enabled: Boolean) {
        isNotFinished = !enabled
        viewHolder.apply {
            etFinish.isEnabled = enabled
            tilFinish.isEnabled = enabled
        }
    }

    private fun isStartValid() = mStart != null
    private fun isFinishValid(): Boolean {
        return if (isNotFinished) {
            true
        } else {
            val start = mStart
            val finish = mFinish
            if (start == null || finish == null) false
            else {
                val finishDate = finish.parseToDate(defaultServerDateFormatter)?.calendar()
                val startDate = start.parseToDate(defaultServerDateFormatter)?.calendar()
                if (finishDate == null || startDate == null) false
                else finishDate.isSameMonth(startDate)
            }
        }
    }

    private fun isOrganizationValid() = !mOrganization.isNullOrBlank()
    private fun isPositionValid() = !mPosition.isNullOrBlank()
    fun isDataValid() = isStartValid() && isFinishValid() && isOrganizationValid() && isPositionValid()

    override fun getLayout() = R.layout.item_profile_data_edit_work
}