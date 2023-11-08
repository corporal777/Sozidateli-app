package com.example.ui.editeducation.items

import android.view.View
import com.example.R
import com.example.data.models.EducationModel
import com.example.data.models.FieldDetails
import com.example.databinding.ItemProfileDataEditEducationBinding
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.isSameMonth
import com.example.extensions.parseToDate
import com.example.util.*
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import initAsMonthYearPicker
import java.util.*

class UserEducationItem(
    private val id: Int?,
    selectedLevel: String?,
    private val start: String?,
    private val finish: String?,
    private val organization: String?,
    private val speciality: String?,
    private val birthday: FieldDetails?,
    private val showInProfile: Boolean?,
    private val onRemoveClickListener: (UserEducationItem) -> Unit,
    private val isDataValid: (isValid: Boolean) -> Unit
) : BindableItem<ItemProfileDataEditEducationBinding>() {

    var isDeleteVisible = true

    private var mSelectedLevel = selectedLevel ?: ""
    private var mId = id
    private var mStart = start
    private var mFinish = finish
    private var mInstitution = organization
    private var mSpeciality = speciality

    private var mShowInProfile = showInProfile ?: false

    private val mBirthday = birthday?.value?.parseToDate(defaultServerDateFormatter)
    private val now = Date()

    private var isNotFinished = mFinish == null

    private lateinit var mBinding: ItemProfileDataEditEducationBinding
    override fun bind(viewBinding: ItemProfileDataEditEducationBinding, position: Int) {
        mBinding = viewBinding
        hideErrors()
        validateEnableButton()
        viewBinding.apply {
            tilStart.apply {
                val startDate = mStart?.parseToDate(defaultServerDateFormatter)
                etStart.setText(startDate?.let { formatDateYear(it).capitalize() })
                initAsMonthYearPicker(
                    startDate,
                    minDate = mBirthday,
                    maxDate = now
                ) { year, month, day ->
                    showError(null)
                    mStart = formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month, day)
                    isDateValid(viewBinding)
                    validateEnableButton()
                    profileDateFormat(year, month, day)
                }
            }

            tilFinish.apply {
                val finishDate = mFinish?.parseToDate(defaultServerDateFormatter)
                etFinish.setText(finishDate?.let { formatDateYear(it).capitalize() })
                initAsMonthYearPicker(
                    finishDate,
                    minDate = mBirthday,
                    maxDate = now
                ) { year, month, day ->
                    showError(null)
                    mFinish = formatDate(DATE_FORMAT_SERVER_TIMESTAMP, year, month, day)
                    isDateValid(viewBinding)
                    validateEnableButton()
                    profileDateFormat(year, month, day)
                }
            }
            scFinish.apply {
                setFinishEnabled(viewBinding, !isNotFinished)
                initSwitch(isNotFinished) {
                    tilFinish.showError(null)
                    mFinish = null
                    etFinish.text = null
                    isNotFinished = it
                    setFinishEnabled(viewBinding, !isNotFinished)
                    validateEnableButton()
                }
            }


            etInstitution.initInput(mInstitution) {
                tilInstitution.showError(null)
                mInstitution = it.toString()
                validateEnableButton()
            }
            etSpeciality.initInput(mSpeciality) {
                tilSpeciality.showError(null)
                mSpeciality = it.toString()
                validateEnableButton()
            }
            scEducation.initSwitch(mShowInProfile) { mShowInProfile = it }
            btnRemove.setOnClickListener { onRemoveClickListener(this@UserEducationItem) }
            setButtonDelete()
        }
    }

    private fun hideErrors() {
        mBinding.apply {
            tilStart.showError(null)
            tilFinish.showError(null)
            tilInstitution.showError(null)
            tilSpeciality.showError(null)
        }
    }

    fun showErrors() {
        hideErrors()
        mBinding.apply {
            if (!isStartValid()) tilStart.apply {
                showError(context.getString(R.string.required_field))
            }
            if (!isFinishValid()) tilFinish.apply {
                showError(context.getString(R.string.profile_education_finish_error))
            }
            if (!isOrganizationValid()) tilInstitution.apply {
                if ((mInstitution?.length ?: 0) < 4 && (mInstitution?.length ?: 0) > 0)
                    showError(context.getString(R.string.ten_letters_error))
                else showError(context.getString(R.string.profile_educate_institution_empty_error))
            }
            if (!isSpecialityValid()) tilSpeciality.apply {
                if ((mSpeciality?.length ?: 0) < 4 && (mSpeciality?.length ?: 0) > 0)
                    showError(context.getString(R.string.five_letters_error))
                else showError(context.getString(R.string.enter_specialty))
            }
        }
    }


    fun setButtonDelete() {
        if (this::mBinding.isInitialized) {
            mBinding.apply {
                if (isDeleteVisible) btnRemove.visibility = View.VISIBLE
                else btnRemove.visibility = View.GONE
            }
        }
    }

    fun setSelectedLevel(level: String?) {
        mSelectedLevel = level ?: ""
        if (this::mBinding.isInitialized) {
            mBinding.tilSpeciality.apply {
                if (error != null && mSelectedLevel.contains("классов")) showError(null)
            }
        }
    }

    private fun isDateValid(viewBinding: ItemProfileDataEditEducationBinding) {
        if (!mStart.isNullOrBlank() && !mFinish.isNullOrBlank()) {
            viewBinding.tilFinish.apply {
                if (validateEndDate(mStart ?: "", mFinish ?: "")) {
                    showError(context.getString(R.string.user_education_end_date_error))
                } else showError(null)
            }

        }
    }

    private fun validateEnableButton() = isDataValid(isDataValid())
    private fun setFinishEnabled(
        viewBinding: ItemProfileDataEditEducationBinding,
        enabled: Boolean
    ) {
        //isNotFinished = !enabled
        viewBinding.apply {
            etFinish.isEnabled = enabled
            tilFinish.isEnabled = enabled
        }
    }

    private fun isStartValid() = !mStart.isNullOrBlank()
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

    private fun isOrganizationValid() = (mInstitution?.length ?: 0) >= 4
    private fun isSpecialityValid(): Boolean {
        return if (mSelectedLevel.contains("классов")) true
        else (mSpeciality?.length ?: 0) >= 4
    }

    fun isDataValid() =
        isStartValid() && isFinishValid() && isOrganizationValid() && isSpecialityValid()


    fun getDataToSave(): EducationModel {
        return EducationModel(
            id = mId,
            begin = mStart,
            end = mFinish,
            organization = mInstitution,
            speciality = mSpeciality ?: "",
            showInProfile = mShowInProfile
        )
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is UserEducationItem) return false
        //if (id != other.id) return false
        if (start != other.start) return false
        if (finish != other.finish) return false
        if (organization != other.organization) return false
        if (speciality != other.speciality) return false
        if (birthday != other.birthday) return false
        if (showInProfile != other.showInProfile) return false
        return true
    }

    override fun getLayout() = R.layout.item_profile_data_edit_education
}