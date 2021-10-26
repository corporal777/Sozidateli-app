package com.example.holders

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import androidx.core.view.isVisible
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.data.models.*
import com.example.extensions.defaultDateFormatter
import com.example.extensions.formatToDefaultDate
import com.example.extensions.formatToDefaultServerDate
import com.example.util.*
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import initAsDatePicker
import isValidPhoneNumber
import kotlinx.android.synthetic.main.item_edit_main_info.*
import kotlinx.android.synthetic.main.item_edit_main_info.btnPhoneConfirm
import kotlinx.android.synthetic.main.item_edit_main_info.scCity
import kotlinx.android.synthetic.main.item_edit_main_info.scGender
import kotlinx.android.synthetic.main.item_edit_main_info.tilMobilePhone
import kotlinx.android.synthetic.main.item_edit_main_info.tvPhoneConfirmed
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.*
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.etMobilePhone
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.*
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.etBirthday
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.etCity
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.tilBirthday
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.tvGender
import onTextChanged
import java.util.*
import setOnClickListener

class MainInfoEditItem(
        id: Long,
        context: Context,
        private val name: String?,
        private val surname: String?,
        private val middleName: String?,
        private val noMiddleName: Boolean,
        private val gender: ToggleStringModel?,
        private val birthday: String?,
        private val address: UserAddress,
        private val phone: List<FieldDetails>?,
        private val showBirthday: Boolean,
        private val canEditName: Boolean,
        private val email: FieldDetails?,
        private val image: ImageModel,
        private val isEnableNext: (isEnable: Boolean) -> Unit,
        private val confirmPhoneClick: (String) -> Unit,
        private val onImageClick: (canRemove: Boolean) -> Unit
) : Item(id) {

    private val genderMale = context.getString(R.string.profile_gender_male)
    private val genderFemale = context.getString(R.string.profile_gender_female)
    private val emptyInputError = context.getString(R.string.profile_edit_empty_field_error)

    private var mImage = image
    private var mName = name
    private var mSurname = surname
    private var mMiddleName = middleName
    private var mMobilePhone = phone?.firstOrNull { it.type == PHONE_PERSONAL }?.value

    private var mGender = gender?.value?.firstLetterToUppercase()
    private var mGenderShow = gender?.showInProfile?: true
    var mBirthday = birthday?.formatToDefaultDate()
    private var mAddress = address
    private var mAddressShow = address.showInProfile?: true
    private var mShowBirthday = showBirthday
    private var mIsPhoneConfirmed = phone?.firstOrNull { it.type == PHONE_PERSONAL }?.isConfirmed?: false

    private var mNoMiddleNameChecked = noMiddleName/*middleName == USER_DATA_EMPTY*/

    private val isCanChangeName = !canEditName//middleName.isNullOrEmpty()

    private lateinit var viewHolder: GroupieViewHolder

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        this.viewHolder = viewHolder
        viewHolder.apply {
            tilSurname.initNameInput(surname) {
                mSurname = it.toString()
                checkDataValid()
            }
            tilName.initNameInput(mName) {
                mName = it.toString()
                checkDataValid()
            }
            tilMiddleName.initNameInput(mMiddleName) {
                mMiddleName = it.toString()
                checkDataValid()
            }
            /*tilEmail.initEmailInput(email?.value) {
                checkDataValid()
            }*/

            /*etEmail.apply {
                setText(email?.value)
                checkDataValid()
            }*/

            scNoMiddleName.apply {
                isChecked = mNoMiddleNameChecked
                isEnabled = isCanChangeName
                if (isCanChangeName) {
                    setOnCheckedChangeListener { _, isChecked ->
                        mNoMiddleNameChecked = isChecked
                        etMiddleName.apply {
                            if (isChecked) etMiddleName.setText("")
                            tilMiddleName.isEnabled = !isChecked
                            if (!isEnabled) tilMiddleName.error = null
                        }
                        checkDataValid()
                    }
                }
            }

            scGender.apply {
                isChecked = mGenderShow
                setOnCheckedChangeListener { _, isChecked ->
                    mGenderShow = isChecked
                }
            }

            tilMobilePhone.apply { error = null }
            etMobilePhone.apply {
                initInput(mMobilePhone) {
                    mMobilePhone = it.toString()
                    if (it?.isNotEmpty() == true && tilMobilePhone.error != null) tilMobilePhone.error = null
                    checkDataValid()

                    if (mIsPhoneConfirmed) {
                        mIsPhoneConfirmed = mMobilePhone == phone?.firstOrNull { ph -> ph.type == PHONE_PERSONAL }?.value
                        updatePhoneConfirmationStatus(viewHolder)
                    }
                }
                addTextChangedListener(PhoneNumberFormattingTextWatcher())
            }

            etBirthday?.initInput(mBirthday) {
                mBirthday = it.toString()
                checkDataValid()
            }
            tilBirthday.initAsDatePicker(
                    mBirthday?.let { defaultDateFormatter.parse(it) },
                    maxDate = Calendar.getInstance().apply {
                        add(Calendar.YEAR, -14)
                    }
                            .time
            ) { year, month, day ->
                checkDataValid()
                String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
            }

            etCity.apply {
                val city = if (mAddress.city != null) {
                    mAddress.city
                } else if (mAddress.district != null) {
                    mAddress.district
                } else {
                    mAddress.address
                }
                setTextWithoutSearch(city)
                onDataSelectedListener = {
                    mAddress = UserAddress.fromDaDataItem(it)
                    checkDataValid()
                }
            }
            scCity.initSwitch(mAddressShow) { mAddressShow = it }
            tvGender.apply {
                keyListener = null
                setAdapter(NoFilterArrayAdapter(context, android.R.layout.simple_list_item_1, mutableListOf(genderMale, genderFemale)))
                mGender = setGender(context)
                initInput(mGender) {
                    mGender = it.toString()
                    checkDataValid()
                }
            }

            btnPhoneConfirm.apply {
                setOnClickListener {
                    val phone = etMobilePhone.text.toString()
                    if (/*phone.isValidPhoneNumber(context)*/Utils.newPhoneValidator(context, phone.replace(" ", "").replace("-", ""))) {
                        confirmPhoneClick(phone)
                    } else {
                        tilMobilePhone.apply {
                            //error = invalidNumberError
                            requestFocus()
                        }
                    }
                }
            }
            setAvatar()
            btnEdit.setOnClickListener {
                onImageClick(mImage.uri != null)
            }
            updatePhoneConfirmationStatus(this)
        }
        checkDataValid()
    }

    private fun updatePhoneConfirmationStatus(viewHolder: GroupieViewHolder) {
        viewHolder.apply {
            btnPhoneConfirm.isVisible = !mIsPhoneConfirmed
            tvPhoneConfirmed.isVisible = mIsPhoneConfirmed
        }
    }

    private fun TextInputLayout.initNameInput(text: String?, onTextChanged: (text: CharSequence?) -> Unit) {
        editText?.setText(text)
        error = null
        isEnabled = true
        if (isCanChangeName) {
            editText?.isEnabled = true
            editText?.onTextChanged {
                if (it?.isNotEmpty() == true) error = null
                onTextChanged(it)
            }
            setEndIconDrawable(0)
        } else {
            editText?.isEnabled = false
        }
    }

    private fun TextInputLayout.initEmailInput(text: String?, onTextChanged: (text: CharSequence?) -> Unit) {
        editText?.setText(text)
        error = null
        isEnabled = true
        editText?.isEnabled = true
        editText?.onTextChanged {
            if (it?.isNotEmpty() == true) error = null
            onTextChanged(it)
        }
        setEndIconDrawable(0)
    }

    private fun setAvatar() {
        this.viewHolder.ivAvatar.apply {
            val avatarUrl = mImage.uri?.takeIf { it.isNotBlank() }
            clipToOutline = true
            transitionName = avatarUrl
            Picasso.get()
                    .load(avatarUrl)
                    .placeholder(R.drawable.avatar_placeholder_rectangle)
                    .error(R.drawable.avatar_placeholder_rectangle)
                    .into(this)
        }
    }

    fun checkDataValid(): Boolean {
        var isValid = true
        if (::viewHolder.isInitialized) {
            viewHolder.apply {
                if (mSurname.isNullOrEmpty()) {
                    isValid = false
                }
                if (mName.isNullOrEmpty()) {
                    isValid = false
                }
                if (!mNoMiddleNameChecked && mMiddleName.isNullOrEmpty()) {
                    isValid = false
                }
                if (mGender.isNullOrEmpty()) {
                    isValid = false
                }
                if (mBirthday.isNullOrEmpty()) {
                    isValid = false
                }
                if (mAddress.address.isNullOrEmpty() && mAddress.region.isNullOrEmpty() && mAddress.city.isNullOrEmpty()) {
                    isValid = false
                }
                if (mMobilePhone.isNullOrEmpty() || !mIsPhoneConfirmed) {
                    isValid = false
                }
                if (mImage.uri.isNullOrEmpty()) isValid = false
                /*if (!AuthValidateUtil.isValidEmail(etEmail.text.toString())) {
                    isValid = false
                }*/
            }
        }
        isEnableNext(isValid)
        return isValid
    }

    /*fun showConfirmEmail(): Boolean = email?.value != viewHolder.etEmail.text.toString()

    fun getEmail(): String = viewHolder.etEmail.text.toString()*/

    fun getDataToSave(): Map<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            if (isCanChangeName) {
                if (name != mName) put(UserDetail.USER_NAME, mName)
                if (surname != mSurname) put(UserDetail.USER_LAST_NAME, mSurname)
                val middleName = if (mNoMiddleNameChecked) USER_DATA_EMPTY else mMiddleName
                if (this@MainInfoEditItem.middleName != middleName) put(UserDetail.USER_MIDDLE_NAME, FieldDetails(value = middleName, absent = mNoMiddleNameChecked))
            }
            if (gender?.value != mGender) put(UserDetail.USER_GENDER, ToggleStringModel(getGender(), mGenderShow))
            mBirthday?.formatToDefaultServerDate()?.let {
                if (birthday != it) put(UserDetail.USER_BIRTHDAY, FieldDetails(value = it, isVisible = mShowBirthday))
            }
            //if (email?.value != viewHolder.etEmail.text.toString()) put(UserDetail.USER_EMAIL, FieldDetails(value = viewHolder.etEmail.text.toString(), isConfirmed = false))
            if (address != mAddress) {
                mAddress.showInProfile = mAddressShow
                put(UserDetail.USER_ADDRESS, mAddress)
            }
            if (phone?.firstOrNull { it.type == PHONE_PERSONAL }?.value != mMobilePhone) {
                val personal = phone?.firstOrNull { it.type == PHONE_PERSONAL }
                val work = phone?.firstOrNull { it.type == PHONE_WORK }
                put(UserDetail.USER_PHONE, arrayListOf(
                        FieldDetails(value = Utils.validatePhoneBeforeSend(mMobilePhone.phoneToServer()?: ""),
                                type = PHONE_PERSONAL, isConfirmed = personal?.isConfirmed, isVisible = personal?.isVisible, absent = false),
                        FieldDetails(value = work?.value, type = PHONE_WORK, isVisible = work?.isVisible, absent = work?.absent)
                ))
            }
        }
    }

    private fun getGender(): String? {
        return when (mGender) {
            genderMale -> GENDER_MALE
            genderFemale-> GENDER_FEMALE
            else -> null
        }
    }

    private fun setGender(context: Context): String {
        return when (mGender) {
            GENDER_MALE, context.getString(R.string.profile_gender_male) -> genderMale
            GENDER_FEMALE, context.getString(R.string.profile_gender_female)  -> genderFemale
            else -> ""
        }
    }

    fun setPhoneNumberValid(isValid: Boolean) {
        mIsPhoneConfirmed = isValid
        notifyChanged()
    }

    fun setImage(image: ImageModel) {
        mImage = image
        checkDataValid()
        setAvatar()
    }

    override fun getLayout(): Int = R.layout.item_edit_main_info
}