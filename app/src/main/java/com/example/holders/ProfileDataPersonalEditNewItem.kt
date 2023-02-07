package com.example.holders

import android.content.Context
import android.util.Log
import androidx.core.view.isVisible
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.data.models.FieldDetails
import com.example.data.models.ToggleStringModel
import com.example.data.models.UserAddress
import com.example.data.models.UserDetail
import com.example.databinding.ItemProfileDataEditPersonalNewBinding
import com.example.extensions.defaultDateFormatter
import com.example.extensions.formatToDefaultDate
import com.example.extensions.formatToDefaultServerDate
import com.example.util.*
import com.xwray.groupie.databinding.BindableItem
import initAsDatePicker
import onTextChanged
import java.util.*

class ProfileDataPersonalEditNewItem(
    id: Long,
    context: Context,
    private val gender: ToggleStringModel?,
    private val birthday: String?,
    private val showBirthday: Boolean,
    private val address: UserAddress,
    private val notes: ToggleStringModel?,
    private val addInfoClick: () -> Unit
) : BindableItem<ItemProfileDataEditPersonalNewBinding>(id) {

    private val genderMale = context.getString(R.string.profile_gender_male)
    private val genderFemale = context.getString(R.string.profile_gender_female)
    private val emptyInputError = context.getString(R.string.profile_edit_empty_field_error)

    private var mGender = gender?.value?.firstLetterToUppercase()
    private var mGenderShow = gender?.showInProfile ?: true
    private var mBirthday = birthday?.formatToDefaultDate()
    private var mShowBirthday = showBirthday
    private var mAddress = address
    private var mAddressShow = address.showInProfile ?: true
    private var mNotes = notes?.value
    private var mNotesShow = notes?.showInProfile ?: true

    //private var mNoMiddleNameChecked = noMiddleName

    //private val isCanChangeName = !canEditName

    private lateinit var viewBinding: ItemProfileDataEditPersonalNewBinding

    override fun bind(viewBinding: ItemProfileDataEditPersonalNewBinding, position: Int) {
        this@ProfileDataPersonalEditNewItem.viewBinding = viewBinding
        viewBinding.apply {
            etBirthday.initInput(mBirthday) { mBirthday = it.toString() }
            val date = if (!mBirthday.isNullOrBlank()) defaultDateFormatter.parse(mBirthday)
            else null
            tilBirthday.initAsDatePicker(
                //mBirthday?.let { defaultDateFormatter.parse(it) },
                startDate = date,
                maxDate = Calendar.getInstance().apply {
                    add(Calendar.YEAR, -14)
                }.time
            ) { year, month, day ->
                String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
            }


            etCity.apply {
                setTextWithoutSearch(mAddress.address)
                onDataSelectedListener = { mAddress = UserAddress.fromDaDataItem(it) }
            }
            scCity.initSwitch(mAddressShow) { mAddressShow = it }

            scBirthday.initSwitch(mShowBirthday) { mShowBirthday = it }
            scGender.apply {
                isChecked = mGenderShow
                setOnCheckedChangeListener { _, isChecked ->
                    mGenderShow = isChecked
                }
            }
            scNotes.isVisible = notes?.value?.isNullOrEmpty() == false
            scNotes.initSwitch(mNotesShow) { mNotesShow = it }

            tvGender.apply {
                keyListener = null
                setAdapter(
                    NoFilterArrayAdapter(
                        context,
                        android.R.layout.simple_list_item_1,
                        mutableListOf(genderMale, genderFemale)
                    )
                )
                mGender = setGender()
                initInput(mGender) { mGender = it.toString() }
            }

            etNotes.apply {
                setText(mNotes)
                onTextChanged { mNotes = it?.toString() }
            }
            btnAddInfo.setOnClickListener {
                addInfoClick()
            }

        }
    }


    fun checkDataValid(): Boolean {
        var isValid = true
        /*if (::viewHolder.isInitialized) {
            viewHolder.apply {
                if (mSurname.isNullOrEmpty()) {
                    tilSurname.error = emptyInputError
                    isValid = false
                }
                if (mName.isNullOrEmpty()) {
                    tilName.error = emptyInputError
                    isValid = false
                }
                if (!mNoMiddleNameChecked && mMiddleName.isNullOrEmpty()) {
                    tilMiddleName.error = emptyInputError
                    isValid = false
                }
            }
        }*/

        return isValid
    }

    fun checkMaxFieldsValid(): Boolean {
        var isValid = false
        if (mNotes.isNullOrEmpty()) isValid = true
        return isValid
    }

    fun checkBaseFieldsValid(): Boolean {
        var isValid = false
        if (mGender.isNullOrEmpty()) isValid = true
        if (mBirthday.isNullOrEmpty()) isValid = true
        if (mAddress.address.isNullOrEmpty() && mAddress.region.isNullOrEmpty() && mAddress.city.isNullOrEmpty()) isValid =
            true
        return isValid
    }

    fun getDataToSave(): Map<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            if (gender?.value != mGender || gender?.showInProfile != mGenderShow) put(
                UserDetail.USER_GENDER,
                ToggleStringModel(getGender(), mGenderShow)
            )
            mBirthday?.formatToDefaultServerDate()?.let {
                if (birthday != it) put(
                    UserDetail.USER_BIRTHDAY,
                    FieldDetails(value = it, isVisible = mShowBirthday)
                )
            }
            if (address != mAddress) {
                mAddress.showInProfile = mAddressShow
                put(UserDetail.USER_ADDRESS, mAddress)
            }
            if (notes?.value != mNotes || notes?.showInProfile != mNotesShow) put(
                UserDetail.USER_NOTES,
                ToggleStringModel(mNotes, mNotesShow)
            )
        }
    }

    private fun getGender(): String? {
        return when (mGender) {
            genderMale -> GENDER_MALE
            genderFemale -> GENDER_FEMALE
            else -> null
        }
    }

    private fun setGender(): String {
        return when (gender?.value) {
            GENDER_MALE -> genderMale
            GENDER_FEMALE -> genderFemale
            else -> ""
        }
    }

    override fun getLayout() = R.layout.item_profile_data_edit_personal_new
}