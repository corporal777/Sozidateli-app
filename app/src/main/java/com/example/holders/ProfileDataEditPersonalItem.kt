package com.example.holders

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.PorterDuff
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.data.models.UserAddress
import com.example.data.models.user.User
import com.example.extensions.defaultDateFormatter
import com.example.extensions.formatToDefaultDate
import com.example.extensions.formatToDefaultServerDate
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.GENDER_FEMALE
import com.example.util.GENDER_MALE
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
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
        private val address: UserAddress,
        private val socialNetworks: List<String>?,
        private val saveClickListener: (data: Map<String, Any?>) -> Unit,
        private val cancelClickListener: () -> Unit
) : Item() {

    private val genderMale = context.getString(R.string.profile_gender_male)
    private val genderFemale = context.getString(R.string.profile_gender_female)

    private var mShowEmail = showEmail
    private var mWorkPhone = workPhone
    private var mShowWorkPhone = showWorkPhone
    private var mMobilePhone = mobilePhone
    private var mShowMobilePhone = showMobilePhone
    private var mGender = gender
    private var mBirthday = birthday?.formatToDefaultDate()
    private var mAddress = address

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            etEmail.setText(email)
            etWorkPhone.initInput(mWorkPhone) { mWorkPhone = it.toString() }
            etMobilePhone.initInput(mMobilePhone) { mMobilePhone = it.toString() }
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
//                onDataSelectedListener = { mAddress = it.data }
            }

            scShowEmail.initSwitch(mShowEmail) { mShowEmail = it }
            scWorkPhone.initSwitch(mShowWorkPhone) { mShowWorkPhone = it }
            scMobilePhone.initSwitch(mShowMobilePhone) { mShowMobilePhone = it }

            tvGender.apply {
                keyListener = null
                setAdapter(NoFilterArrayAdapter(context, android.R.layout.simple_list_item_1, mutableListOf(genderMale, genderFemale)))
                initInput(mGender) { mGender = it.toString() }
            }

            btnSave.setOnClickListener { saveClickListener(getDataToSave()) }
            btnRevoke.setOnClickListener { cancelClickListener() }
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
            if (address != mAddress) {
                put(User.FIELD_USER_CITY, mAddress.city)
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