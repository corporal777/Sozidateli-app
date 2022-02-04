package com.example.holders

import android.view.View
import androidx.core.view.isVisible
import com.example.R
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.isSameMonth
import com.example.extensions.parseToDate
import com.example.util.*
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import initAsMonthYearPicker
import kotlinx.android.synthetic.main.item_profile_data_edit_work.*
import java.util.*

class ProfileDataWorkEditItem(
        id: Int?,
        start: String?,
        finish: String?,
        val organization: String?,
        val position: String?,
        birthday: String?,
        showInProfile: Boolean?,
        private val onRemoveClickListener: (ProfileDataWorkEditItem) -> Unit,
        private val isDataValid: (isValid: Boolean) -> Unit
) : Item() {

    var isDeleteVisible = true

    var mId = id
        private set
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
    var mShowInProfile = showInProfile?: false
        private set

    private val birthday = birthday?.parseToDate(defaultServerDateFormatter)

    private val now = Date()

    private var isEditable = true

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        isEnabledItems(viewHolder)
        viewHolder.apply {
            val startDate = mStart?.parseToDate(defaultServerDateFormatter)
            etStart.setText(startDate?.let { formatDateYear(it).capitalize() })
            tilStart.initAsMonthYearPicker(startDate, minDate = birthday, maxDate = now) { year, month, day ->
                tilStart.error = null
                mStart = formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month, day)
                isDateValid(viewHolder)
                validateEnableButton()
                profileDateFormat(year, month, day)
            }

            val finishDate = mFinish?.parseToDate(defaultServerDateFormatter)
            etFinish.setText(finishDate?.let { formatDateYear(it).capitalize() })
            tilFinish.initAsMonthYearPicker(finishDate, minDate = birthday, maxDate = now) { year, month, day ->
                tilFinish.error = null
                mFinish = formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month, day)
                isDateValid(viewHolder)
                validateEnableButton()
                profileDateFormat(year, month, day)
            }

            setFinishEnabled(this, !isNotFinished)
            scFinish.initSwitch(isNotFinished) {
                tilFinish.error = null
                mFinish = null
                etFinish.text = null
                setFinishEnabled(this, !it)
                validateEnableButton()
            }
            etProject.initInput(mOrganization) {
                tilProject.error = null
                mOrganization = it.toString()
                validateEnableButton()
            }
            etPosition.initInput(mPosition) {
                tilPosition.error = null
                mPosition = it.toString()
                validateEnableButton()
            }

            scWork.apply {
                isVisible = !organization.isNullOrEmpty() || !this@ProfileDataWorkEditItem.position.isNullOrEmpty()
                initSwitch(mShowInProfile) { mShowInProfile = it }
            }

            btnRemove.setOnClickListener { onRemoveClickListener(this@ProfileDataWorkEditItem) }

            if (isDeleteVisible)
                btnRemove.visibility = View.VISIBLE
            else
                btnRemove.visibility = View.GONE
        }
    }

    private fun isEnabledItems(viewHolder: GroupieViewHolder) {
        viewHolder.apply {
            etProject.isEnabled = isEditable
            etPosition.isEnabled = isEditable
            tilStart.isEnabled = isEditable
            tilFinish.isEnabled = isEditable
            scFinish.isEnabled = isEditable
            btnRemove.isEnabled = isEditable
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
                    error = resources.getString(R.string.profile_work_finish_error)
                }
                if (!isOrganizationValid()) tilProject.apply {
                    error = if ((mOrganization?.length?: 0) < 4 && (mOrganization?.length?: 0) > 0)
                        resources.getString(R.string.ten_letters_error)
                    else
                        resources.getString(R.string.enter_organization)
                }
                if (!isPositionValid()) tilPosition.apply {
                    error = if ((mPosition?.length?: 0) < 4 && (mPosition?.length?: 0) > 0)
                        resources.getString(R.string.five_letters_error)
                    else
                        resources.getString(R.string.enter_position)
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

    private fun validateEnableButton() {
        isDataValid(isDataValid())
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
        return (mOrganization?.length?: 0) >= 4
        //!mwOrganization.isNullOrBlank()
    }
    private fun isPositionValid(): Boolean {
        return (mPosition?.length?: 0) >= 4
        //!mPosition.isNullOrBlank()
    }
    fun isDataValid() = isStartValid() && isFinishValid() && isOrganizationValid() && isPositionValid()

    fun hasExp(hasExp: Boolean) {
        isEditable = !hasExp
    }

    override fun getLayout() = R.layout.item_profile_data_edit_work
}