package com.example.holders

import android.view.View
import com.example.R
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.isSameMonth
import com.example.extensions.parseToDate
import com.example.util.*
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import initAsMonthYearPicker
import kotlinx.android.synthetic.main.item_profile_data_edit_education.*
import kotlinx.android.synthetic.main.item_profile_data_edit_education.btnRemove
import kotlinx.android.synthetic.main.item_profile_data_edit_education.etFinish
import kotlinx.android.synthetic.main.item_profile_data_edit_education.etStart
import kotlinx.android.synthetic.main.item_profile_data_edit_education.scFinish
import kotlinx.android.synthetic.main.item_profile_data_edit_education.tilFinish
import kotlinx.android.synthetic.main.item_profile_data_edit_education.tilStart
import kotlinx.android.synthetic.main.item_profile_data_edit_work.*
import java.util.*

class ProfileDataEducationEditItem(
        start: String?,
        finish: String?,
        organization: String?,
        speciality: String?,
        birthday: String?,
        private val onRemoveClickListener: (ProfileDataEducationEditItem) -> Unit
) : Item() {

    var isDeleteVisible = true

    var mStart = start
        private set
    var mFinish = finish
        private set
    var mInstitution = organization
        private set
    var mSpeciality = speciality
        private set
    var isNotFinished = mFinish == null
        private set

    private val birthday = birthday?.parseToDate(defaultServerDateFormatter)
    private val now = Date()

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            val startDate = mStart?.parseToDate(defaultServerDateFormatter)
            etStart.setText(startDate?.let { formatDateYear(it).capitalize() })
            tilStart.initAsMonthYearPicker(startDate, minDate = birthday, maxDate = now) { year, month, day ->
                tilStart.error = null
                mStart = formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month, day)
                isDateValid(viewHolder)
                profileDateFormat(year, month, day)
            }

            val finishDate = mFinish?.parseToDate(defaultServerDateFormatter)
            etFinish.setText(finishDate?.let { formatDateYear(it).capitalize() })
            tilFinish.initAsMonthYearPicker(finishDate, minDate = birthday, maxDate = now) { year, month, day ->
                tilFinish.error = null
                mFinish = formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month, day)
                isDateValid(viewHolder)
                profileDateFormat(year, month, day)
            }

            setFinishEnabled(this, !isNotFinished)
            scFinish.initSwitch(isNotFinished) {
                tilFinish.error = null
                mFinish = null
                etFinish.text = null
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

            if (isDeleteVisible)
                btnRemove.visibility = View.VISIBLE
            else
                btnRemove.visibility = View.GONE
        }
    }

    private fun isDateValid(viewHolder: GroupieViewHolder) {
        if (!mStart.isNullOrBlank() && !mFinish.isNullOrBlank()) {
            if (validateEndDate(mStart?: "", mFinish?: "")) {
                viewHolder.tilFinish.error = viewHolder.tilFinish.context.getString(R.string.user_education_end_date_error)
            } else {
                viewHolder.tilFinish.error = null
            }
        }
    }

    override fun bind(holder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) super.bind(holder, position, payloads)
        else {
            holder.apply {
                if (!isStartValid()) tilStart.apply {
                    error = resources.getString(R.string.required_field)
                }
                if (!isFinishValid()) tilFinish.apply {
                    error = resources.getString(R.string.profile_education_finish_error)
                }
                if (!isOrganizationValid()) tilInstitution.apply {
                    error = if ((mInstitution?.length?: 0) < 9 && (mInstitution?.length?: 0) > 0)
                        resources.getString(R.string.ten_letters_error)
                    else
                        resources.getString(R.string.profile_educate_institution_empty_error)
                }
                if (!isSpecialityValid()) tilSpeciality.apply {
                    error = if ((mSpeciality?.length?: 0) < 4 && (mSpeciality?.length?: 0) > 0)
                        resources.getString(R.string.five_letters_error)
                    else
                        resources.getString(R.string.enter_specialty)
                }
            }
        }
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
                else startDate.timeInMillis < finishDate.timeInMillis || finishDate.isSameMonth(startDate)
            }
        }
    }

    private fun isOrganizationValid(): Boolean {
        return (mInstitution?.length?: 0) >= 9
        //!mInstitution.isNullOrBlank()
    }
    private fun isSpecialityValid(): Boolean {
        return (mSpeciality?.length?: 0) >= 4
        //!mSpeciality.isNullOrBlank()
    }
    fun isDataValid() = isStartValid() && isFinishValid() && isOrganizationValid() && isSpecialityValid()

    override fun getLayout() = R.layout.item_profile_data_edit_education
}