package com.example.holders

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemProfileDataEditPersonalBinding
import com.example.data.models.FieldDetails
import com.example.data.models.ToggleStringModel
import com.example.data.models.UserAddress
import com.example.data.models.UserDetail
import com.example.extensions.formatToDefaultDate
import com.example.extensions.formatToDefaultServerDate
import com.example.extensions.initAsDatePicker
import com.example.extensions.onTextChanged
import com.example.extensions.showSearchRegionDialog
import com.example.extensions.showSearchSettlementDialog
import com.example.ui.views.dialogs.AboutAdditionalInfoBottomSheet
import com.example.ui.views.dialogs.AdditionalInfoBottomSheet
import com.example.ui.views.suggestFieldView.region.SearchRegionBottomSheet
import com.example.ui.views.suggestFieldView.settlement.SearchSettlementBottomSheet
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.GENDER_FEMALE
import com.example.util.GENDER_MALE
import com.example.util.initDropDownAdapter
import com.example.util.initInput
import com.example.util.initSwitch
import com.xwray.groupie.viewbinding.BindableItem
import java.util.Calendar

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

    private var mGender = setGender()
    private var mGenderShow = gender?.showInProfile ?: true

    private var mBirthday = birthday?.value?.formatToDefaultDate()
    private var mShowBirthday = birthday?.isVisible ?: true

    private var mAddressRegion = address.region
    private var mAddressCity = address.city
    private var mAddressShow = address.showInProfile ?: true

    private var mNotes = notes?.value
    private var mNotesShow = notes?.showInProfile ?: true


    override fun bind(viewBinding: ItemProfileDataEditPersonalBinding, position: Int) {
        viewBinding.apply {
            etBirthday.apply {
                val maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, -16) }.time
                tilBirthday.initAsDatePicker(null, null, maxDate) { y, m, d ->
                    String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, d, m + 1, y)
                }
                initInput(mBirthday) { mBirthday = it.toString() }
            }
            scNoBirthday.initSwitch(mShowBirthday) { mShowBirthday = it }

            etGender.apply {
                initDropDownAdapter(mutableListOf("Мужской", "Женский"))
                initInput(mGender){ mGender = it.toString() }
            }
            scNoGender.initSwitch(mGenderShow) { mGenderShow = it }

            tvRegion.apply {
                text = mAddressRegion
                setOnClickListener {
                    showSearchRegionDialog {
                        mAddressRegion = it?.name
                        text = mAddressRegion
                        if (mAddressRegion != address.region) {
                            mAddressCity = null
                            tvCity.text = mAddressCity
                            tvCity.isEnabled = !mAddressRegion.isNullOrEmpty()
                        }
                    }
                }
            }
            tvCity.apply {
                isEnabled = !mAddressRegion.isNullOrEmpty()
                text = mAddressCity
                setOnClickListener {
                    showSearchSettlementDialog(mAddressRegion) {
                        mAddressCity = it?.name
                        text = mAddressCity
                    }
                }
            }
            scNoAddress.initSwitch(mAddressShow) { mAddressShow = it }

            tvEditNotes.apply {
                scNotes.initSwitch(mNotesShow) { mNotesShow = it }
                setOnClickListener {
                    AdditionalInfoBottomSheet(root.context, mNotes)
                        .setSaveClickCallback {
                            if (mNotes != it) {
                                mNotes = it
                                setNotes(viewBinding)
                            }
                        }.show()
                }
                setNotes(viewBinding)
            }
        }
    }


    private fun setNotes(viewBinding: ItemProfileDataEditPersonalBinding) {
        viewBinding.scNotes.isVisible = !mNotes.isNullOrEmpty()
        viewBinding.tilNotes.apply {
            isEndIconVisible = mNotes.isNullOrEmpty()
            setEndIconOnClickListener { AboutAdditionalInfoBottomSheet(context).show() }
        }
        viewBinding.etNotes.setText(mNotes)
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
            if (gender?.value != mGender || gender.showInProfile != mGenderShow)
                put(UserDetail.USER_GENDER, ToggleStringModel(getGender(), mGenderShow))
            mBirthday?.formatToDefaultServerDate()?.let {
                if (birthday?.value != it)
                    put(UserDetail.USER_BIRTHDAY, FieldDetails(it, isVisible = mShowBirthday))
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

    override fun initializeViewBinding(view: View) = ItemProfileDataEditPersonalBinding.bind(view)
    override fun getLayout() = R.layout.item_profile_data_edit_personal
}