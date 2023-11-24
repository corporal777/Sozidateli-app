package com.example.holders

import android.content.Context
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.FieldDetails
import com.example.data.models.ToggleStringModel
import com.example.data.models.UserAddress
import com.example.data.models.UserDetail
import com.example.databinding.ItemProfileDataEditPersonalBinding
import com.example.extensions.defaultDateFormatter
import com.example.extensions.firstLetterToUppercase
import com.example.extensions.formatToDefaultDate
import com.example.extensions.formatToDefaultServerDate
import com.example.ui.userprofile.edit.items.AboutAdditionalInfoBottomSheet
import com.example.ui.userprofile.edit.items.AdditionalInfoBottomSheet
import com.example.ui.views.suggestFieldView.region.SearchRegionBottomSheet
import com.example.ui.views.suggestFieldView.settlement.SearchSettlementBottomSheet
import com.example.util.*
import com.xwray.groupie.databinding.BindableItem
import initAsDatePicker
import java.util.*

class ProfileDataPersonalEditItem(
    id: Long,
    context: Context,
    private val gender: ToggleStringModel?,
    private val birthday: FieldDetails?,
    private val address: UserAddress,
    private val notes: ToggleStringModel?
) : BindableItem<ItemProfileDataEditPersonalBinding>(id) {

    private val genderMale = context.getString(R.string.profile_gender_male)
    private val genderFemale = context.getString(R.string.profile_gender_female)

    private var mGender = gender?.value?.firstLetterToUppercase()
    private var mGenderShow = gender?.showInProfile ?: true

    private var mBirthday = birthday?.value?.formatToDefaultDate()
    private var mShowBirthday = birthday?.isVisible ?: true

    private var mAddressRegion = address.region
    private var mAddressCity = address.city
    private var mAddressShow = address.showInProfile ?: true

    private var mNotes = notes?.value
    private var mNotesShow = notes?.showInProfile ?: true

    private lateinit var viewBinding: ItemProfileDataEditPersonalBinding

    override fun bind(viewBinding: ItemProfileDataEditPersonalBinding, position: Int) {
        this@ProfileDataPersonalEditItem.viewBinding = viewBinding
        viewBinding.apply {
            etBirthday.initInput(mBirthday) { mBirthday = it.toString() }
            tilBirthday.apply {
                val date =
                    if (!mBirthday.isNullOrBlank()) defaultDateFormatter.parse(mBirthday)
                    else null
                initAsDatePicker(
                    startDate = date,
                    maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, -14) }.time
                ) { year, month, day ->
                    String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
                }
            }
            scBirthday.initSwitch(mShowBirthday) { mShowBirthday = it }

            scGender.apply {
                isChecked = mGenderShow
                setOnCheckedChangeListener { _, isChecked ->
                    mGenderShow = isChecked
                }
            }
            tvGender.apply {
                mGender = setGender()
                initDropDownAdapter(mutableListOf(genderMale, genderFemale))
                initInput(mGender) { mGender = it.toString() }
            }

            tvRegion.apply {
                text = mAddressRegion
                setOnClickListener {
                    SearchRegionBottomSheet(context)
                        .setRegionSelectedCallback {
                            mAddressRegion = it?.name
                            text = mAddressRegion
                            if (mAddressRegion != address.region){
                                mAddressCity = null
                                tvCity.text = mAddressCity
                            }
                        }.show()
                }
            }
            tvCity.apply {
                isEnabled = !mAddressRegion.isNullOrEmpty()
                text = mAddressCity
                setOnClickListener {
                    SearchSettlementBottomSheet(context, mAddressRegion)
                        .setSettlementSelectedCallback {
                            mAddressCity = it?.name
                            text = mAddressCity
                        }.show()
                }
            }
            scCity.initSwitch(mAddressShow) { mAddressShow = it }

            scNotes.apply {
                isVisible = notes?.value?.isNullOrEmpty() == false
                initSwitch(mNotesShow) { mNotesShow = it }
            }

            setNotes(viewBinding)
            tvEditNotes.setOnClickListener {
                AdditionalInfoBottomSheet(root.context, mNotes)
                    .setSaveClickCallback {
                        if (mNotes != it) {
                            mNotes = it
                            setNotes(viewBinding)
                        }
                    }.show()
            }


        }
    }


    private fun setNotes(viewBinding: ItemProfileDataEditPersonalBinding){
        viewBinding.tilNotes.apply {
            setInformationIconVisibility(mNotes.isNullOrEmpty()){
                AboutAdditionalInfoBottomSheet(context).show()
            }
            viewBinding.etNotes.setText(mNotes)
        }
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
        if (mAddressRegion.isNullOrEmpty() && mAddressCity.isNullOrEmpty())
            isValid = true
        return isValid
    }

    fun getDataToSave(): Map<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            if (gender?.value != mGender || gender?.showInProfile != mGenderShow) put(
                UserDetail.USER_GENDER,
                ToggleStringModel(getGender(), mGenderShow)
            )
            mBirthday?.formatToDefaultServerDate()?.let {
                if (birthday?.value != it)
                    put(
                        UserDetail.USER_BIRTHDAY,
                        FieldDetails(value = it, isVisible = mShowBirthday)
                    )
            }
            if (checkAddressIsEqual()) {
                put(UserDetail.USER_ADDRESS, getNewAddress())
            }
            if (notes?.value != mNotes || notes?.showInProfile != mNotesShow)
                put(UserDetail.USER_NOTES, ToggleStringModel(mNotes, mNotesShow))
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

    private fun checkAddressIsEqual(): Boolean {
        return address.region != mAddressRegion
                || address.city != mAddressCity
                || address.showInProfile != mAddressShow
    }

    private fun getNewAddress(): UserAddress {
        return UserAddress(
            index = address.index,
            region = mAddressRegion,
            city = mAddressCity,
            fullValue = address.fullValue,
            shortValue = address.shortValue,
            showInProfile = mAddressShow
        )
    }

    override fun getLayout() = R.layout.item_profile_data_edit_personal
}