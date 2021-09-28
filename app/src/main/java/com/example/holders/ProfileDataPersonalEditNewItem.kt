package com.example.holders

import android.content.Context
import android.graphics.PorterDuff
import android.text.util.Linkify
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.toSpannable
import androidx.fragment.app.FragmentManager
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.data.models.FieldDetails
import com.example.data.models.UserAddress
import com.example.data.models.UserDetail
import com.example.data.models.user.User
import com.example.extensions.defaultDateFormatter
import com.example.extensions.formatToDefaultDate
import com.example.extensions.formatToDefaultServerDate
import com.example.extensions.longToDate
import com.example.util.*
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import initAsDatePicker
import kotlinx.android.synthetic.main.item_edit_main_info.*
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.*
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.etBirthday
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.etCity
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.etMiddleName
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.scNoMiddleName
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.tilBirthday
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.tilMiddleName
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.tilName
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.tilSurname
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.tvGender
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import onTextChanged
import java.util.*

class ProfileDataPersonalEditNewItem(
        id: Long,
        context: Context,
        private val name: String?,
        private val surname: String?,
        private val middleName: String?,
        private val noMiddleName: Boolean,
        private val gender: String?,
        private val birthday: String?,
        private val showBirthday: Boolean,
        private val address: UserAddress,
        private val notes: String?,
        private val canEditName: Boolean,
        private val state: String,
        private val fragmentManager: FragmentManager,
        private val addInfoClick:() -> Unit
) : Item(id) {

    private val genderMale = context.getString(R.string.profile_gender_male)
    private val genderFemale = context.getString(R.string.profile_gender_female)
    private val emptyInputError = context.getString(R.string.profile_edit_empty_field_error)

    private var mName = name
    private var mSurname = surname
    private var mMiddleName = middleName

    private var mGender = gender
    var mBirthday = birthday?.formatToDefaultDate()
    private var mShowBirthday = showBirthday
    private var mAddress = address
    private var mNotes = notes

    private var mNoMiddleNameChecked = noMiddleName//middleName == USER_DATA_EMPTY

    private val isCanChangeName = !canEditName//middleName.isNullOrEmpty()

    private lateinit var viewHolder: GroupieViewHolder

    override fun getLayout() = R.layout.item_profile_data_edit_personal_new

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
                            if (isChecked) etMiddleName.setText("")
                            tilMiddleName.isEnabled = !isChecked
                            if (!isEnabled) tilMiddleName.error = null
                        }
                    }
                }
            }
            tvState.setText(state)
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

            /*etBirthday.setOnClickListener {
                fragmentManager.showDatePicker(mBirthday?: "",
                        onDateSelected = { date ->
                            etBirthday.setText(longToDate(date))
                        })
            }*/

            etCity.apply {
                setTextWithoutSearch(mAddress.address)
                onDataSelectedListener = { mAddress = UserAddress.fromDaDataItem(it) }
            }

            scBirthday.initSwitch(mShowBirthday) { mShowBirthday = it }

            tvGender.apply {
                keyListener = null
                setAdapter(NoFilterArrayAdapter(context, android.R.layout.simple_list_item_1, mutableListOf(genderMale, genderFemale)))
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

    fun checkDataValid(): Boolean {
        var isValid = true
        if (::viewHolder.isInitialized) {
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
        }

        return isValid
    }

    fun checkMaxFieldsValid(): Boolean {
        var isValid = false
        if (mNotes.isNullOrEmpty()) isValid = true
        return isValid
    }

    fun checkBaseFieldsValid(): Boolean {
        var isValid = false
        if (mSurname.isNullOrEmpty()) isValid = true
        if (mName.isNullOrEmpty()) isValid = true
        if (!mNoMiddleNameChecked && mMiddleName.isNullOrEmpty()) isValid = true
        if (mGender.isNullOrEmpty()) isValid = true
        if (mBirthday.isNullOrEmpty()) isValid = true
        if (mAddress.address.isNullOrEmpty() && mAddress.region.isNullOrEmpty() && mAddress.city.isNullOrEmpty()) isValid = true
        return isValid
    }

    fun getDataToSave(): Map<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            if (isCanChangeName) {
                if (name != mName) put(UserDetail.USER_NAME, mName)
                if (surname != mSurname) put(UserDetail.USER_LAST_NAME, mSurname)
                val middleName = if (mNoMiddleNameChecked) USER_DATA_EMPTY else mMiddleName
                if (this@ProfileDataPersonalEditNewItem.middleName != middleName) put(UserDetail.USER_MIDDLE_NAME, FieldDetails(value = middleName, absent = mNoMiddleNameChecked))
            }
            if (gender != mGender) put(UserDetail.USER_GENDER, getGender())
            mBirthday?.formatToDefaultServerDate()?.let {
                if (birthday != it) put(UserDetail.USER_BIRTHDAY, FieldDetails(value = it, isVisible = mShowBirthday))
            }
            if (address != mAddress) put(UserDetail.USER_ADDRESS, mAddress)
            if (notes != mNotes) put(UserDetail.USER_NOTES, mNotes)

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
        return when (gender) {
            GENDER_MALE -> genderMale
            GENDER_FEMALE -> genderFemale
            else -> ""
        }
    }
}