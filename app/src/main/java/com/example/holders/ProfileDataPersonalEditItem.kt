package com.example.holders

import android.content.Context
import android.graphics.PorterDuff
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
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
import com.example.util.USER_MIDDLE_NAME_EMPTY
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import initAsDatePicker
import isValidPhoneNumber
import kotlinx.android.synthetic.main.item_profile_data_edit_notes.*
import kotlinx.android.synthetic.main.item_profile_data_edit_personal.*
import kotlinx.android.synthetic.main.item_profile_data_edit_personal.etMiddleName
import kotlinx.android.synthetic.main.item_profile_data_edit_personal.etNotes
import kotlinx.android.synthetic.main.item_profile_data_edit_personal.scNoMiddleName
import kotlinx.android.synthetic.main.item_profile_data_edit_personal.tilMiddleName
import kotlinx.android.synthetic.main.item_profile_data_edit_personal.tilName
import kotlinx.android.synthetic.main.item_profile_data_edit_personal.tilSurname
import kotlinx.android.synthetic.main.item_profile_data_user_edit.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import onTextChanged
import setOnClickListener
import java.util.*

class ProfileDataPersonalEditItem(
        id: Long,
        context: Context,
        private val name: String?,
        private val surname: String?,
        private val middleName: String?,
        private val gender: String?,
        private val birthday: String?,
        private val showBirthday: Boolean,
        private val address: UserAddress,
        private val notes: String?,
) : Item(id) {

    private val genderMale = context.getString(R.string.profile_gender_male)
    private val genderFemale = context.getString(R.string.profile_gender_female)
    private val emptyInputError = context.getString(R.string.profile_edit_empty_field_error)

    private var mName = name
    private var mSurname = surname
    private var mMiddleName = middleName

    private var mGender = gender
    private var mBirthday = birthday?.formatToDefaultDate()
    private var mShowBirthday = showBirthday
    private var mAddress = address
    private var mNotes = notes

    private var mNoMiddleNameChecked = middleName == USER_MIDDLE_NAME_EMPTY

    private val isCanChangeName = middleName.isNullOrEmpty()

    private lateinit var viewHolder: GroupieViewHolder

    override fun getLayout() = R.layout.item_profile_data_edit_personal

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        this.viewHolder = viewHolder
        viewHolder.apply {
            tilSurname.initNameInput(surname) { mSurname = it.toString() }
            tilName.initNameInput(mName) { mName = it.toString() }
            tilMiddleName.initNameInput(mMiddleName) { mMiddleName = it.toString() }

            scNoMiddleName.apply {
                isChecked = mNoMiddleNameChecked
                isEnabled = isCanChangeName
                if (isCanChangeName) {
                    setOnCheckedChangeListener { _, isChecked ->
                        mNoMiddleNameChecked = isChecked
                        etMiddleName.apply {
                            tilMiddleName.isEnabled = !isChecked
                            if (!isEnabled) tilMiddleName.error = null
                        }
                    }
                }
            }

            etBirthday?.initInput(mBirthday) { mBirthday = it.toString() }
            tilBirthday.initAsDatePicker(
                    mBirthday?.let { defaultDateFormatter.parse(it) },
                    maxDate = Calendar.getInstance().apply {
                        add(Calendar.YEAR, -14)
                    }
                            .time
            ) { year, month, day ->
                String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, day, month + 1, year)
            }

            etCity.apply {
                setTextWithoutSearch(mAddress.address)
                onDataSelectedListener = { mAddress = UserAddress.fromDaDataItem(it) }
            }

            scBirthday.initSwitch(mShowBirthday) { mShowBirthday = it }

            tvGender.apply {
                keyListener = null
                setAdapter(NoFilterArrayAdapter(context, android.R.layout.simple_list_item_1, mutableListOf(genderMale, genderFemale)))
                initInput(mGender) { mGender = it.toString() }
            }

            etNotes.apply {
                setText(mNotes)
                onTextChanged { mNotes = it?.toString() }
            }
        }
    }

    private fun EditText.initInput(text: String?, onTextChanged: (text: CharSequence?) -> Unit) {
        setText(text)
        onTextChanged(onTextChanged)
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
            setEndIconDrawable(R.drawable.ic_information)
            setEndIconTintMode(PorterDuff.Mode.MULTIPLY)
            setEndIconOnClickListener { showDisabledMainInputInfo(context) }
        }
    }

    private fun showDisabledMainInputInfo(context: Context) {
        val supportEmail = context.getString(R.string.support_email)
        val message = context.getString(R.string.profile_edit_name_disabled_message).format(supportEmail).toSpannable()
        Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES)

        AlertDialog.Builder(context)
                .setTitle(R.string.profile_edit_name_disabled_title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show()
                .apply {
                    findViewById<TextView>(android.R.id.message)?.let {
                        it.movementMethod = BetterLinkMovementMethod.getInstance()
                    }
                }
    }

    private fun CheckBox.initSwitch(checked: Boolean, onCheckedChanged: (isChecked: Boolean) -> Unit) {
        isChecked = checked
        setOnCheckedChangeListener { _, isChecked -> onCheckedChanged(isChecked) }
    }

    fun checkDataValid(): Boolean {
        var isValid = true
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

        return isValid
    }

    fun getDataToSave(): Map<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            if (name != mName) put(User.FIELD_USER_NAME, mName)
            if (surname != mSurname) put(User.FIELD_USER_LAST_NAME, mSurname)
            val middleName = if (mNoMiddleNameChecked) USER_MIDDLE_NAME_EMPTY else mMiddleName
            if (this@ProfileDataPersonalEditItem.middleName != middleName) put(User.FIELD_USER_MIDDLE_NAME, middleName)
            if (gender != mGender) put(User.FIELD_USER_GENDER, getGender())
            mBirthday?.formatToDefaultServerDate()?.let {
                if (birthday != it) put(User.FIELD_USER_BIRTHDAY, it)
            }
            if (showBirthday != mShowBirthday) put(User.FIELD_USER_BIRTHDAY_SHOW, mShowBirthday)
            if (address != mAddress) {
                put(User.FIELD_USER_ADDRESS, mAddress.address ?: "")
                put(User.FIELD_USER_ADDRESS_INDEX, mAddress.index ?: "")
                put(User.FIELD_USER_ADDRESS_COUNTRY, mAddress.country ?: "")
                put(User.FIELD_USER_ADDRESS_FEDERAL, mAddress.federal ?: "")
                put(User.FIELD_USER_ADDRESS_REGION, mAddress.region ?: "")
                put(User.FIELD_USER_ADDRESS_AREA, mAddress.area ?: "")
                put(User.FIELD_USER_ADDRESS_CITY, mAddress.city ?: "")
                put(User.FIELD_USER_ADDRESS_CITY_GPS_LAT, mAddress.lat)
                put(User.FIELD_USER_ADDRESS_CITY_GPS_LON, mAddress.lon)
                put(User.FIELD_USER_ADDRESS_DISTRICT, mAddress.district ?: "")
                put(User.FIELD_USER_ADDRESS_SETTLEMENT, mAddress.settlement ?: "")
                put(User.FIELD_USER_ADDRESS_STREET, mAddress.street ?: "")
                put(User.FIELD_USER_ADDRESS_HOUSE, mAddress.house ?: "")
                put(User.FIELD_USER_ADDRESS_FLAT, mAddress.flat ?: "")
            }
            if (notes != mNotes) put(User.FIELD_USER_NOTES, mNotes)
        }
    }

    private fun getGender(): String? {
        return when (mGender) {
            genderMale -> GENDER_MALE
            genderFemale -> GENDER_FEMALE
            else -> null
        }
    }
}