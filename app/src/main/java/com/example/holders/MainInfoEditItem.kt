package com.example.holders

import android.content.Context
import com.example.R
import com.example.data.models.*
import com.example.databinding.ItemEditMainInfoBinding
import com.example.extensions.defaultDateFormatter
import com.example.extensions.formatToDefaultDate
import com.example.extensions.formatToDefaultServerDate
import com.example.ui.views.suggestFieldView.region.SearchRegionBottomSheet
import com.example.ui.views.suggestFieldView.settlement.SearchSettlementBottomSheet
import com.example.util.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem
import initAsDatePicker
import java.util.*


class MainInfoEditItem(
    id: Long,
    private val gender: ToggleStringModel?,
    private val birthday: String?,
    private val address: UserAddress?,
    private val phone: List<FieldDetails>?,
    private val showBirthday: Boolean?,
    private val image: String?,
    private val isEnableNext: (isEnable: Boolean) -> Unit,
    private val onEditPhoneClick: (String?) -> Unit,
    private val onImageClick: (canRemove: Boolean) -> Unit
) : BindableItem<ItemEditMainInfoBinding>(id) {

    private val genderMale = "Мужской"
    private val genderFemale = "Женский"
    private var mGender = gender?.value?.firstLetterToUppercase()
    private var mGenderShow = gender?.showInProfile ?: true

    private var mImage = image

    private var mMobilePhone = phone?.firstOrNull { it.type == PHONE_PERSONAL }?.value
    private var mMobilePhoneIsVisible = phone?.firstOrNull { it.type == PHONE_PERSONAL }?.isVisible

    private var mBirthday = birthday?.formatToDefaultDate()
    private var mShowBirthday = showBirthday ?: false

    private var mAddressRegion = address?.region
    private var mAddressCity = address?.city
    private var mAddressShow = address?.showInProfile ?: true


    private lateinit var mBinding: ItemEditMainInfoBinding

    override fun bind(viewBinding: ItemEditMainInfoBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            setAvatar()
            tilBirthday.apply {
                initAsDatePicker(
                    startDate = if (!mBirthday.isNullOrEmpty()) defaultDateFormatter.parse(mBirthday) else null,
                    maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, -14) }.time
                ) { year, month, day ->
                    checkDataValid()
                    String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
                }
                etBirthday.initInput(mBirthday) {
                    mBirthday = it.toString()
                    checkDataValid()
                }
            }

            tvGender.apply {
                mGender = setGender(context)
                initDropDownAdapter(mutableListOf(genderMale, genderFemale))
                initInput(mGender) {
                    mGender = it.toString()
                    checkDataValid()
                }
            }
            scGender.apply {
                isChecked = mGenderShow
                setOnCheckedChangeListener { _, isChecked ->
                    mGenderShow = isChecked
                }
            }

            tvRegion.apply {
                text = mAddressRegion
                setOnClickListener {
                    SearchRegionBottomSheet(context)
                        .setRegionSelectedCallback {
                            mAddressRegion = it?.name
                            text = mAddressRegion
                            tvCity.isEnabled = !mAddressRegion.isNullOrEmpty()
                            if (mAddressRegion != address?.region) {
                                mAddressCity = null
                                tvCity.text = mAddressCity
                            }
                            checkDataValid()
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

            etMobilePhone.apply {
                initInput(mMobilePhone) {
                    mMobilePhone = it.toString()
                    checkDataValid()
                }
                tvEditPhone.setOnClickListener {
                    onEditPhoneClick.invoke(mMobilePhone)
                }
            }

            btnEdit.setOnClickListener {
                onImageClick(!mImage.isNullOrEmpty())
            }
        }
        checkDataValid()
    }




    fun checkDataValid(): Boolean {
        var isValid = true
        if (mGender.isNullOrEmpty()) isValid = false
        if (mBirthday.isNullOrEmpty()) isValid = false
        if (mAddressRegion.isNullOrEmpty()) isValid = false
        if (mMobilePhone.isNullOrEmpty() || !isPhoneValid()) isValid = false
        if (mImage.isNullOrEmpty()) isValid = false

        isEnableNext(isValid)
        return isValid
    }

    private fun getPersonalPhone() = mMobilePhone?.phoneToServer() ?: ""
    private fun isPhoneValid(): Boolean = Utils.isNewPhoneIsValid(getPersonalPhone())
    private fun getValidatedPhone() = Utils.validatePhoneBeforeSend(getPersonalPhone())

    fun getDataToSave(): MutableMap<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            if (gender?.value != mGender)
                put(UserDetail.USER_GENDER, ToggleStringModel(getGender(), mGenderShow))

            mBirthday?.formatToDefaultServerDate()?.let {
                put(UserDetail.USER_BIRTHDAY, FieldDetails(value = it, isVisible = mShowBirthday))
            }

            if (checkAddressIsEqual()) put(UserDetail.USER_ADDRESS, getNewAddress())

            put(
                UserDetail.USER_PHONE, arrayListOf(
                    FieldDetails(
                        value = getValidatedPhone(),
                        type = PHONE_PERSONAL,
                        isVisible = mMobilePhoneIsVisible
                    )
                )
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

    private fun setGender(context: Context): String {
        return when (mGender) {
            GENDER_MALE, context.getString(R.string.profile_gender_male) -> genderMale
            GENDER_FEMALE, context.getString(R.string.profile_gender_female) -> genderFemale
            else -> ""
        }
    }

    private fun setAvatar() {
        if (::mBinding.isInitialized) {
            mBinding.apply {
                btnEdit.text =
                    if (!mImage.isNullOrEmpty()) root.context.getString(R.string.edit_title)
                    else root.context.getString(R.string.profile_add_photo)

                ivAvatar.apply {
                    clipToOutline = true
                    transitionName = mImage
                    Picasso.get()
                        .load(mImage)
                        .placeholder(R.drawable.avatar_placeholder_rectangle)
                        .error(R.drawable.avatar_placeholder_rectangle)
                        .into(this)
                }
            }
        }
    }

    fun setImage(image: ImageModel?) {
        mImage = image?.uri
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
            shortValue = address?.shortValue,
            showInProfile = mAddressShow
        )
    }

    override fun getLayout(): Int = R.layout.item_edit_main_info
}