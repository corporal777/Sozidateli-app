package com.example.holders

import com.example.R
import com.example.data.models.*
import com.example.databinding.ItemEditMainInfoBinding
import com.example.extensions.*
import com.example.ui.views.suggestFieldView.region.SearchRegionBottomSheet
import com.example.ui.views.suggestFieldView.settlement.SearchSettlementBottomSheet
import com.example.util.*
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem


class MainInfoEditItem(
    id: Long,
    private val gender: ToggleStringModel?,
    private val birthday: FieldDetails?,
    private val address: NewUserAddress?,
    private val phone: FieldDetails?,
    private val image: String?,
    private val imageIsDefault: Boolean?,
) : BindableItem<ItemEditMainInfoBinding>(id) {

    var onEnableNext: (isEnable: Boolean) -> Unit = {}
    var onEditPhoneClick: (String?) -> Unit = {}
    var onImageClick: (canRemove: Boolean) -> Unit = {}

    private val genderMale = "Мужской"
    private val genderFemale = "Женский"
    private var mGender = setGender()
    private var mGenderShow = gender?.showInProfile ?: true

    private var mImage = image
    private var mImageIsDefault = imageIsDefault ?: true

    private var mMobilePhone = phone?.value
    private var mMobilePhoneIsVisible = phone?.isVisible

    private var mBirthday = birthday?.value?.formatToDefaultDate()
    private var mShowBirthday = birthday?.isVisible ?: false

    private var mAddressRegion = address?.region
    private var mAddressCity = address?.city
    private var mAddressShow = address?.showInProfile ?: true


    private lateinit var mBinding: ItemEditMainInfoBinding

    override fun bind(viewBinding: ItemEditMainInfoBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            setAvatar()
            btnEdit.setOnClickListener {
                onImageClick(!mImage.isNullOrEmpty())
            }
            etBirthday.apply {
                initAsDateTimePicker(mBirthday) {
                    mBirthday = it.toString()
                    checkDataValid()
                }
                initSwitch(mShowBirthday) { mShowBirthday = it }
            }

            tvGender.apply {
                initAsDropDown(mGender, listOf(genderMale, genderFemale)) {
                    mGender = it.toString()
                    checkDataValid()
                }
                initSwitch(mGenderShow) { mGenderShow = it }
            }

            tvRegion.apply {
                initAsCustomMode(mAddressRegion) {
                    SearchRegionBottomSheet(context)
                        .setRegionSelectedCallback {
                            mAddressRegion = it?.name
                            setText(mAddressRegion)
                            if (mAddressRegion != address?.region) {
                                mAddressCity = null
                                tvCity.setText(mAddressCity)
                                tvCity.isEnabled = !mAddressRegion.isNullOrEmpty()
                            }
                            checkDataValid()
                        }.show()
                }
            }
            tvCity.apply {
                isEnabled = !mAddressRegion.isNullOrEmpty()
                initAsCustomMode(mAddressCity) {
                    SearchSettlementBottomSheet(context, mAddressRegion)
                        .setSettlementSelectedCallback {
                            mAddressCity = it?.name
                            setText(mAddressCity)
                        }.show()
                }
                initSwitch(mAddressShow) { mAddressShow = it }
            }

            etMobilePhone.apply {
                initInput(mMobilePhone) {
                    mMobilePhone = it.toString()
                    checkDataValid()
                }
                tvEditPhone.setOnClickListener {
                    onEditPhoneClick.invoke(mMobilePhone)
                }
            }
        }
        checkDataValid()
    }


    fun checkDataValid() {
        var isValid = true
        if (mGender.isNullOrEmpty()) isValid = false
        if (mBirthday.isNullOrEmpty()) isValid = false
        if (mAddressRegion.isNullOrEmpty()) isValid = false
        if (mMobilePhone.isNullOrEmpty() || !isPhoneValid()) isValid = false
        if (mImage.isNullOrEmpty() || mImageIsDefault) isValid = false

        onEnableNext(isValid)
    }

    private fun isPhoneValid(): Boolean =
        Utils.isPhoneNumberValid(mMobilePhone?.phoneToServer() ?: "")

    fun getDataToSave(): MutableMap<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            if (gender?.value != mGender)
                put(UserDetail.USER_GENDER, ToggleStringModel(getGender(), mGenderShow))

            mBirthday?.formatToDefaultServerDate()?.let {
                put(UserDetail.USER_BIRTHDAY, FieldDetails(value = it, isVisible = mShowBirthday))
            }

            if (checkAddressIsEqual()) put(UserDetail.USER_ADDRESS, getNewAddress())

            put(
                UserDetail.USER_PHONE, FieldDetails(
                    value = Utils.validatePhoneBeforeSend(mMobilePhone?.phoneToServer() ?: ""),
                    type = PHONE_PERSONAL,
                    isVisible = mMobilePhoneIsVisible
                ).toList()
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

    private fun setAvatar() {
        if (::mBinding.isInitialized.not()) return
        mBinding.apply {
            btnEdit.text = if (mImage.isNullOrEmpty() || mImageIsDefault)
                root.context.getString(R.string.profile_add_photo)
            else root.context.getString(R.string.edit_title)
            ivAvatar.setImage(mImage, mImageIsDefault)
        }
    }

    fun setImage(image: ImageModel?, isDefault: Boolean?) {
        mImage = image?.uri
        mImageIsDefault = isDefault ?: false
        checkDataValid()
        setAvatar()
    }

    fun setPhone(phone: FieldDetails?) {
        if (::mBinding.isInitialized) {
            mMobilePhone = phone?.value
            mMobilePhoneIsVisible = phone?.isVisible
            mBinding.etMobilePhone.setText(mMobilePhone)
        }
    }

    private fun checkAddressIsEqual(): Boolean {
        return address?.region != mAddressRegion
                || address?.city != mAddressCity
                || address?.showInProfile != mAddressShow
    }

    private fun getNewAddress(): UserAddress {
        return UserAddress(
            index = address?.index,
            region = mAddressRegion,
            city = mAddressCity,
            fullValue = address?.fullValue,
            shortValue = address?.shortAddres,
            showInProfile = mAddressShow
        )
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is MainInfoEditItem) return false
        if (gender != other.gender) return false
        if (birthday != other.birthday) return false
        if (address != other.address) return false
        if (phone != other.phone) return false
        if (image != other.image) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_edit_main_info
}