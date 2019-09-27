package com.example.holders

import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import com.example.R
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.parseToDate
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE
import com.example.util.DATE_FORMAT_SERVER_TIMESTAMP
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import initAsDatePicker
import kotlinx.android.synthetic.main.item_profile_data_edit_education.*
import onTextChanged
import java.text.SimpleDateFormat
import java.util.*

class ProfileDataEducationEditItem(
        start: String?,
        finish: String?,
        organization: String?,
        speciality: String?,
        private val onRemoveClickListener: (ProfileDataEducationEditItem) -> Unit
) : Item() {

    var mStart = start
        private set
    var mFinish = finish
        private set
    var mInstitution = organization
        private set
    var mSpeciality = speciality
        private set
    private var isNotFinished = mFinish == null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            val startDate = mStart?.parseToDate(defaultServerDateFormatter)
            etStart.setText(startDate?.let { formatDate(it) })
            tilStart.initAsDatePicker(startDate) { year, month, _ ->
                tilStart.error = null
                mStart = formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month)
                formatDate(DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE, year, month)
            }

            val finishDate = mFinish?.parseToDate(defaultServerDateFormatter)
            etFinish.setText(finishDate?.let { formatDate(it) })
            tilFinish.initAsDatePicker(finishDate) { year, month, _ ->
                tilFinish.error = null
                mFinish = formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month)
                formatDate(DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE, year, month)
            }

            setFinishEnabled(this, !isNotFinished)
            scFinish.initSwitch(isNotFinished) {
                tilFinish.error = null
                setFinishEnabled(this, !it)
            }
            etInstitution.initInput(mInstitution) {
                tilInstitution.error = null
                mInstitution = it.toString()
            }
            etSpeciality.initInput(mSpeciality) {
                tilSpeciality.error = null
                mSpeciality = it.toString()
            }

            btnRemove.setOnClickListener { onRemoveClickListener(this@ProfileDataEducationEditItem) }
        }
    }

    override fun bind(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) super.bind(holder, position, payloads)
        else {
            holder.apply {
                if (!isStartValid()) tilStart.apply {
                    error = resources.getString(R.string.profile_education_start_error)
                }
                if (!isFinishValid()) tilFinish.apply {
                    error = resources.getString(R.string.profile_education_finish_error)
                }
                if (!isOrganizationValid()) tilInstitution.apply {
                    error = resources.getString(R.string.profile_edit_empty_field_error)
                }
                if (!isSpecialityValid()) tilSpeciality.apply {
                    error = resources.getString(R.string.profile_edit_empty_field_error)
                }
            }
        }
    }

    private fun EditText.initInput(text: String?, onTextChanged: (text: CharSequence?) -> Unit) {
        setText(text)
        onTextChanged(onTextChanged)
    }

    private fun SwitchCompat.initSwitch(checked: Boolean, onCheckedChanged: (isChecked: Boolean) -> Unit) {
        isChecked = checked
        setOnCheckedChangeListener { _, isChecked -> onCheckedChanged(isChecked) }
    }

    private fun formatDate(date: Date): String {
        return Calendar.getInstance().apply { time = date }.let {
            formatDate(DATE_FORMAT_FULL_MONTH_FULL_YEAR_NO_DATE, it.get(Calendar.YEAR), it.get(Calendar.MONTH))
        }
    }

    private fun formatDate(format: String, year: Int, month: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
        }

        return SimpleDateFormat(format, Locale.getDefault()).format(calendar.time)
    }

    private fun setFinishEnabled(viewHolder: ViewHolder, enabled: Boolean) {
        isNotFinished = !enabled
        viewHolder.apply {
            etFinish.isEnabled = enabled
            tilFinish.isEnabled = enabled
        }
    }

    private fun isStartValid() = mStart != null
    private fun isFinishValid(): Boolean {
        val finish = mFinish
        return if (finish == null) {
            isNotFinished
        } else {
            val start = mStart
            if (start == null) true
            else finish > start
        }
    }

    private fun isOrganizationValid() = !mInstitution.isNullOrBlank()
    private fun isSpecialityValid() = !mSpeciality.isNullOrBlank()
    fun isDataValid() = isStartValid() && isFinishValid() && isOrganizationValid() && isSpecialityValid()

    override fun getLayout() = R.layout.item_profile_data_edit_education
}