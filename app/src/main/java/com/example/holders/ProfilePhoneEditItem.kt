package com.example.holders

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.user.User
import com.example.util.initInput
import com.example.util.initSwitch
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import isValidPhoneNumber
import kotlinx.android.synthetic.main.item_profile_data_edit_phone.*
import onTextChanged

class ProfilePhoneEditItem(
        private val context: Context,
        private val mobilePhone: String?,
        private val showMobilePhone: Boolean,
        private val isPhoneConfirmed: Boolean,
        private val confirmPhoneClick: (String) -> Unit
) : Item() {

    private lateinit var viewHolder: GroupieViewHolder

    private val invalidNumberError = context.getString(R.string.invalid_phone_number_error)

    private var mMobilePhone = mobilePhone
    private var mShowMobilePhone = showMobilePhone
    private var mIsPhoneConfirmed = isPhoneConfirmed

    override fun getLayout() = R.layout.item_profile_data_edit_phone

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        this.viewHolder = viewHolder
        viewHolder.apply {
            tilMobilePhone.apply { error = null }
            etMobilePhone.apply {
                initInput(mMobilePhone) {
                    mMobilePhone = it.toString()
                    if (it?.isNotEmpty() == true && tilMobilePhone.error != null) tilMobilePhone.error = null

                    if (isPhoneConfirmed) {
                        mIsPhoneConfirmed = mMobilePhone == mobilePhone
                        updatePhoneConfirmationStatus(viewHolder)
                    }
                }
                addTextChangedListener(PhoneNumberFormattingTextWatcher())
            }

            scMobilePhone.initSwitch(mShowMobilePhone) { mShowMobilePhone = it }

            btnPhoneConfirm.apply {
                setOnClickListener {
                    val phone = etMobilePhone.text.toString()
                    if (phone.isValidPhoneNumber(context)) {
                        confirmPhoneClick(phone)
                    } else {
                        tilMobilePhone.apply {
                            error = invalidNumberError
                            requestFocus()
                        }
                    }
                }
            }

            updatePhoneConfirmationStatus(this)
        }
    }

    private fun updatePhoneConfirmationStatus(viewHolder: GroupieViewHolder) {
        viewHolder.apply {
            btnPhoneConfirm.isVisible = !mIsPhoneConfirmed
            tvPhoneConfirmed.isVisible = mIsPhoneConfirmed
        }
    }

    fun checkDataValid(): Boolean {
        var isValid = true

        if (mobilePhone != mMobilePhone
                && !mMobilePhone.isNullOrEmpty()
                && !mMobilePhone.isValidPhoneNumber(context)
        ) {
            viewHolder.tilMobilePhone.apply {
                error = invalidNumberError
                requestFocus()
            }
            isValid = false
        }

        return isValid
    }

    fun getDataToSave(): Map<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            if (mobilePhone != mMobilePhone) {
                put(User.FIELD_USER_STATUS_PHONE, mMobilePhone)
                put(User.FIELD_USER_PHONE_MOBILE, mMobilePhone)
            }
            if (showMobilePhone != mShowMobilePhone) put(User.FIELD_USER_PHONE_MOBILE_SHOW, mShowMobilePhone)
        }
    }
}