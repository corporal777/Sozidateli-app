package com.example.ui.editwork.items

import android.view.View
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.WorkExperience
import com.example.data.models.WorkExperienceServerModel
import com.example.databinding.ItemProfileDataEditWorkBinding
import com.example.databinding.ItemProfileDataWorkExperienceBinding
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.isSameMonth
import com.example.extensions.parseToDate
import com.example.holders.ProfileDataWorkEditItem
import com.example.util.*
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import initAsMonthYearPicker
import kotlinx.android.synthetic.main.item_profile_data_edit_work.*
import java.util.*

class UserWorkItem(
    id: Int?,
    val start: String?,
    val finish: String?,
    val organization: String?,
    val position: String?,
    val birthday: String?,
    val showInProfile: Boolean?,
    private val onRemoveClickListener: (UserWorkItem) -> Unit,
) : BindableItem<ItemProfileDataEditWorkBinding>(id?.toLong() ?: 0) {

    var onDataValidCallback: (isValid: Boolean) -> Unit = {}

    var isDeleteVisible = true

    private var mId = id
    private var mStart = start
    private var mFinish = finish
    private var mOrganization = organization
    private var mPosition = position
    private var isNotFinished = mFinish == null
    private var mShowInProfile = showInProfile ?: false
    private val mBirthday = birthday?.parseToDate(defaultServerDateFormatter)
    private val now = Date()

    var isFieldsEnabled = false

    private lateinit var mBinding: ItemProfileDataEditWorkBinding
    override fun bind(viewBinding: ItemProfileDataEditWorkBinding, position: Int) {
        mBinding = viewBinding
        validateEnableButton()
        isEnabledItems()
        hideErrors(viewBinding)

        viewBinding.apply {
            val startDate = mStart?.parseToDate(defaultServerDateFormatter)
            etStart.setText(startDate?.let { formatDateYear(it).capitalize() })
            tilStart.initAsMonthYearPicker(
                startDate,
                minDate = mBirthday,
                maxDate = now
            ) { year, month, day ->
                tilStart.showError(null)
                mStart = formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month, day)
                isDateValid(this)
                validateEnableButton()
                profileDateFormat(year, month, day)
            }

            val finishDate = mFinish?.parseToDate(defaultServerDateFormatter)
            etFinish.setText(finishDate?.let { formatDateYear(it).capitalize() })
            tilFinish.initAsMonthYearPicker(
                finishDate,
                minDate = mBirthday,
                maxDate = now
            ) { year, month, day ->
                tilFinish.showError(null)
                mFinish = formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month, day)
                isDateValid(this)
                validateEnableButton()
                profileDateFormat(year, month, day)
            }

            setFinishEnabled(!isNotFinished, this)
            scFinish.initSwitch(isNotFinished) {
                tilFinish.showError(null)
                mFinish = null
                etFinish.text = null
                setFinishEnabled(!it, this)
                validateEnableButton()
            }
            etProject.initInput(mOrganization) {
                tilProject.showError(null)
                mOrganization = it.toString()
                validateEnableButton()
            }
            etPosition.initInput(mPosition) {
                tilPosition.showError(null)
                mPosition = it.toString()
                validateEnableButton()
            }

            scWork.apply {
                isVisible =
                    !organization.isNullOrEmpty() || !this@UserWorkItem.position.isNullOrEmpty()
                initSwitch(mShowInProfile) { mShowInProfile = it }
            }

            btnRemove.setOnClickListener { onRemoveClickListener(this@UserWorkItem) }
            setButtonRemove()
        }
    }

    fun showErrors() {
        if (this::mBinding.isInitialized) {
            mBinding.apply {
                if (!isStartValid()) tilStart.apply {
                    showError(context.getString(R.string.required_field))
                }
                if (!isFinishValid()) tilFinish.apply {
                    showError(context.getString(R.string.profile_work_finish_error))
                }
                if (!isOrganizationValid()) tilProject.apply {
                    if ((mOrganization?.length ?: 0) < 4 && (mOrganization?.length ?: 0) > 0)
                        showError(context.getString(R.string.ten_letters_error))
                    else showError(context.getString(R.string.enter_organization))
                }
                if (!isPositionValid()) tilPosition.apply {
                    if ((mPosition?.length ?: 0) < 4 && (mPosition?.length ?: 0) > 0)
                        showError(context.getString(R.string.five_letters_error))
                    else showError(context.getString(R.string.enter_position))
                }
            }
        }
    }

    private fun hideErrors(viewBinding: ItemProfileDataEditWorkBinding) {
        viewBinding.apply {
            tilStart.showError(null)
            tilFinish.showError(null)
            tilProject.showError(null)
            tilPosition.showError(null)
        }
    }

    fun setButtonRemove() {
        if (this::mBinding.isInitialized) {
            if (isDeleteVisible) mBinding.btnRemove.visibility = View.VISIBLE
            else mBinding.btnRemove.visibility = View.GONE
        }
    }

    fun isEnabledItems() {
        if (this::mBinding.isInitialized) {
            mBinding.apply {
                etProject.isEnabled = isFieldsEnabled
                etPosition.isEnabled = isFieldsEnabled
                tilStart.isEnabled = isFieldsEnabled
                tilFinish.isEnabled = isFieldsEnabled
                scFinish.isEnabled = isFieldsEnabled
                btnRemove.isEnabled = isFieldsEnabled
            }
            hideErrors(mBinding)
        }
    }

    private fun isDateValid(viewBinding: ItemProfileDataEditWorkBinding) {
        if (!mStart.isNullOrBlank() && !mFinish.isNullOrBlank()) {
            if (validateEndDate(mStart ?: "", mFinish ?: "")) {
                viewBinding.tilFinish.error =
                    viewBinding.tilFinish.context.getString(R.string.user_education_end_date_error)
            } else {
                viewBinding.tilFinish.error = null
            }
        }
    }

    private fun setFinishEnabled(enabled: Boolean, viewBinding: ItemProfileDataEditWorkBinding) {
        isNotFinished = !enabled
        viewBinding.apply {
            etFinish.isEnabled = enabled
            tilFinish.isEnabled = enabled
        }
    }

    private fun validateEnableButton() = onDataValidCallback.invoke(isDataValid())

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
                else startDate.timeInMillis < finishDate.timeInMillis || finishDate.isSameMonth(
                    startDate
                )
            }
        }
    }

    private fun isOrganizationValid(): Boolean = (mOrganization?.length ?: 0) >= 4
    private fun isPositionValid(): Boolean = (mPosition?.length ?: 0) >= 4
    fun isDataValid() =
        isStartValid() && isFinishValid() && isOrganizationValid() && isPositionValid()

    fun isDataNotEmpty(): Boolean {
        return !mOrganization.isNullOrEmpty() || !mPosition.isNullOrEmpty() || !mStart.isNullOrBlank() || !mFinish.isNullOrBlank()
    }


    fun getDataToSave(): WorkExperience {
        return WorkExperience(
            id = mId, begin = mStart, end = mFinish,
            organization = mOrganization, position = mPosition, description = "",
            showInProfile = mShowInProfile
        )

    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is UserWorkItem) return false
        if (start != other.start) return false
        if (finish != other.finish) return false
        if (organization != other.organization) return false
        if (position != other.position) return false
        if (birthday != other.birthday) return false
        if (showInProfile != other.showInProfile) return false
        return true
    }

    override fun getLayout() = R.layout.item_profile_data_edit_work
}