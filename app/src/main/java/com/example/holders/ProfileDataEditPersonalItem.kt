package com.example.holders

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.PorterDuff
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.data.models.UserAddress
import com.example.data.models.UserDataSocialLink
import com.example.data.models.user.User
import com.example.extensions.defaultDateFormatter
import com.example.extensions.formatToDefaultDate
import com.example.extensions.formatToDefaultServerDate
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.GENDER_FEMALE
import com.example.util.GENDER_MALE
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import isValidPhoneNumber
import kotlinx.android.synthetic.main.item_profile_data_edit_personal.*
import onTextChanged
import java.util.*

class ProfileDataEditPersonalItem(
        private val context: Context,
        private val email: String?,
        private val showEmail: Boolean,
        private val workPhone: String?,
        private val showWorkPhone: Boolean,
        private val mobilePhone: String?,
        private val showMobilePhone: Boolean,
        private val gender: String?,
        private val birthday: String?,
        private val showBirthday: Boolean,
        private val address: UserAddress,
        private val socialNetworks: List<UserDataSocialLink>?,
        private val saveClickListener: (data: Map<String, Any?>) -> Unit,
        private val cancelClickListener: () -> Unit,
        private val changeEmailClick: () -> Unit,
        private val changePasswordClick: () -> Unit
) : Item() {

    private val genderMale = context.getString(R.string.profile_gender_male)
    private val genderFemale = context.getString(R.string.profile_gender_female)
    private val invalidNumberError = context.getString(R.string.invalid_phone_number_error)

    private var mShowEmail = showEmail
    private var mWorkPhone = workPhone
    private var mShowWorkPhone = showWorkPhone
    private var mMobilePhone = mobilePhone
    private var mShowMobilePhone = showMobilePhone
    private var mGender = gender
    private var mBirthday = birthday?.formatToDefaultDate()
    private var mShowBirthday = showBirthday
    private var mAddress = address
    private var mSocialNetworks = (socialNetworks ?: emptyList())
            .map { it.copy() }
            .let {
                if (it.isEmpty()) it.plus(UserDataSocialLink(value = ""))
                else it
            }
            .toMutableList()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            etEmail.setText(email)
            tilWorkPhone.apply { error = null }
            etWorkPhone.apply {
                initInput(mWorkPhone) {
                    mWorkPhone = it.toString()
                    if (it?.isNotEmpty() == true) tilWorkPhone.error = null
                }
                addTextChangedListener(PhoneNumberFormattingTextWatcher())
            }
            tilMobilePhone.apply { error = null }
            etMobilePhone.apply {
                initInput(mMobilePhone) {
                    mMobilePhone = it.toString()
                    if (it?.isNotEmpty() == true) tilMobilePhone.error = null
                }
                addTextChangedListener(PhoneNumberFormattingTextWatcher())
            }
            etBirthday?.initInput(mBirthday) { mBirthday = it.toString() }
            tilBirthday.apply {
                setEndIconDrawable(R.drawable.ic_calendar)
                setEndIconTintMode(PorterDuff.Mode.MULTIPLY)
                setEndIconOnClickListener {
                    val calendar = Calendar.getInstance().apply {
                        mBirthday?.let { time = defaultDateFormatter.parse(it) }
                    }
                    DatePickerDialog(this@ProfileDataEditPersonalItem.context, R.style.AlertDialogTheme, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        etBirthday.setText(String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, dayOfMonth, month + 1, year))
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                            .show()
                }
            }
            etCity.apply {
                setTextWithoutSearch(mAddress.address)
                onDataSelectedListener = { mAddress = UserAddress.fromDaDataItem(it) }
            }

            scShowEmail.initSwitch(mShowEmail) { mShowEmail = it }
            scWorkPhone.initSwitch(mShowWorkPhone) { mShowWorkPhone = it }
            scMobilePhone.initSwitch(mShowMobilePhone) { mShowMobilePhone = it }
            scBirthday.initSwitch(mShowBirthday) { mShowBirthday = it }

            tvGender.apply {
                keyListener = null
                setAdapter(NoFilterArrayAdapter(context, android.R.layout.simple_list_item_1, mutableListOf(genderMale, genderFemale)))
                initInput(mGender) { mGender = it.toString() }
            }

            btnSave.setOnClickListener { if (checkDataValid(viewHolder)) saveClickListener(getDataToSave()) }
            btnRevoke.setOnClickListener { cancelClickListener() }

            llSocialNetworks.removeAllViews()
            mSocialNetworks.forEach { initSocialNetworkInput(viewHolder, it) }
            btnSocialNetworkAdd.apply {
                setOnClickListener {
                    if (!mSocialNetworks.lastOrNull()?.value.isNullOrBlank()) {
                        UserDataSocialLink(value = "").apply {
                            mSocialNetworks.add(this)
                            initSocialNetworkInput(viewHolder, this)
                        }
                    }
                }
            }

            btnChangeEmail.apply {
                setOnClickListener { changeEmailClick() }
            }

            btnChangePassword.apply {
                setOnClickListener { changePasswordClick() }
            }
        }
    }

    private fun EditText.initInput(text: String?, onTextChanged: (text: CharSequence?) -> Unit) {
        setText(text)
        onTextChanged(onTextChanged)
    }

    private fun SwitchCompat.initSwitch(checked: Boolean, onCheckedChanged: (isChecked: Boolean) -> Unit) {
        isChecked = checked
        setOnCheckedChangeListener { _, isChecked -> onCheckedChanged(isChecked) }
    }

    private fun initSocialNetworkInput(viewHolder: ViewHolder, sn: UserDataSocialLink) {
        var csn = sn
        val parent = LayoutInflater.from(context).inflate(R.layout.item_profile_social_network, viewHolder.llSocialNetworks, false)
        val etSn = parent.findViewById<EditText>(R.id.etSn).apply {
            initInput(csn.value) { csn.value = it?.toString() ?: "" }
        }

        parent.findViewById<View>(R.id.btnDelete).apply {
            setOnClickListener {
                if (mSocialNetworks.remove(csn)) {
                    if (mSocialNetworks.isEmpty()) {
                        csn = UserDataSocialLink(value = "")
                        mSocialNetworks.add(csn)
                        etSn.text?.clear()
                    } else {
                        viewHolder.llSocialNetworks.removeView(it.parent as View)
                    }
                }
            }
        }

        viewHolder.llSocialNetworks.addView(parent)
    }

    private fun checkDataValid(viewHolder: ViewHolder): Boolean {
        var isValid = true
        if (workPhone != mWorkPhone || mobilePhone != mMobilePhone) {
            if (workPhone != mWorkPhone && !mWorkPhone.isNullOrEmpty()) {
                if (!mWorkPhone.isValidPhoneNumber(context)) {
                    viewHolder.tilWorkPhone.error = invalidNumberError
                    isValid = false
                }
            }
            if (mobilePhone != mMobilePhone && !mMobilePhone.isNullOrEmpty()) {
                if (!mMobilePhone.isValidPhoneNumber(context)) {
                    viewHolder.tilMobilePhone.error = invalidNumberError
                    isValid = false
                }
            }
        }

        return isValid
    }

    private fun getDataToSave(): Map<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            if (showEmail != mShowEmail) put(User.FIELD_USER_EMAIL_SHOW, mShowEmail)
            if (workPhone != mWorkPhone) put(User.FIELD_USER_PHONE_WORK, mWorkPhone)
            if (showWorkPhone != mShowWorkPhone) put(User.FIELD_USER_PHONE_WORK_SHOW, mShowWorkPhone)
            if (mobilePhone != mMobilePhone) put(User.FIELD_USER_PHONE_MOBILE, mMobilePhone)
            if (showMobilePhone != mShowMobilePhone) put(User.FIELD_USER_PHONE_MOBILE_SHOW, mShowMobilePhone)
            if (gender != mGender) put(User.FIELD_USER_GENDER, getGender())
            mBirthday?.formatToDefaultServerDate()?.let {
                if (birthday != it) put(User.FIELD_USER_BIRTHDAY, it)
            }
            if (showBirthday != mShowBirthday) put(User.FIELD_USER_BIRTHDAY_SHOW, mShowBirthday)
            if (address != mAddress) {
                put(User.FIELD_USER_ADDRESS, mAddress.address ?: "")
                put(User.FIELD_USER_ADDRESS_INDEX, mAddress.index ?: "")
                put(User.FIELD_USER_ADDRESS_COUNTRY, mAddress.country ?: "")
                put(User.FIELD_USER_ADDRESS_REGION, mAddress.region ?: "")
                put(User.FIELD_USER_ADDRESS_AREA, mAddress.area ?: "")
                put(User.FIELD_USER_ADDRESS_CITY, mAddress.city ?: "")
                put(User.FIELD_USER_ADDRESS_DISTRICT, mAddress.district ?: "")
                put(User.FIELD_USER_ADDRESS_SETTLEMENT, mAddress.settlement ?: "")
                put(User.FIELD_USER_ADDRESS_STREET, mAddress.street ?: "")
                put(User.FIELD_USER_ADDRESS_HOUSE, mAddress.house ?: "")
                put(User.FIELD_USER_ADDRESS_FLAT, mAddress.flat ?: "")
            }

            if (socialNetworks?.toHashSet() != mSocialNetworks.toHashSet()) {
                put(User.FIELD_SOCIAL_LINKS, mSocialNetworks.filter { it.value.isNotBlank() })
            }
        }
    }

    private fun getGender(): String? {
        return when (mGender) {
            genderMale -> GENDER_MALE
            genderFemale -> GENDER_FEMALE
            else -> null
        }
    }

    override fun getLayout() = R.layout.item_profile_data_edit_personal
}