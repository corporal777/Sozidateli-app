package com.example.holders

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.UserDataSocialLink
import com.example.data.models.user.User
import com.example.util.USER_DATA_EMPTY
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import isValidPhoneNumber
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.*
import onTextChanged
import setOnClickListener

class ProfileContactsEditItem(
        private val context: Context,
        private val mobilePhone: String?,
        private val showMobilePhone: Boolean,
        private val isPhoneConfirmed: Boolean,
        private val workPhone: String?,
        private val showWorkPhone: Boolean,
        private val socialNetworks: List<UserDataSocialLink>?,
        private val site: String?,
        private val email: String?,
        private val showEmail: Boolean,
        private val changeEmailClick: () -> Unit,
        private val confirmPhoneClick: (String) -> Unit
) : Item() {

    private lateinit var viewHolder: GroupieViewHolder

    private val invalidNumberError = context.getString(R.string.invalid_phone_number_error)

    private var mMobilePhone = mobilePhone
    private var mShowMobilePhone = showMobilePhone
    private var mIsPhoneConfirmed = isPhoneConfirmed
    private var mWorkPhone = workPhone
    private var mShowWorkPhone = showWorkPhone
    private var mSite = site
    private var mNoSite = site == USER_DATA_EMPTY
    private var mNoWorkPhone = workPhone == USER_DATA_EMPTY

    private var mShowEmail = showEmail
    private var mSocialNetworks = (socialNetworks ?: emptyList())
            .map { it.copy() }
            .let {
                if (it.isEmpty()) it.plus(UserDataSocialLink(value = ""))
                else it
            }
            .toMutableList()

    override fun getLayout() = R.layout.item_profile_data_edit_contacts

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

            tilWorkPhone.apply {
                error = null
                isEnabled = !mNoWorkPhone
            }
            etWorkPhone.apply {
                initInput(mWorkPhone) {
                    mWorkPhone = it.toString()
                    if (it?.isNotEmpty() == true && tilWorkPhone.error != null) tilWorkPhone.error = null
                }
                addTextChangedListener(PhoneNumberFormattingTextWatcher())
            }

            scShowWorkPhone.initSwitch(mShowWorkPhone) { mShowWorkPhone = it }
            scNoWorkPhone.initSwitch(mNoWorkPhone) {
                mNoWorkPhone = it
                if (it) {
                    tilWorkPhone.error = null
                }
                tilWorkPhone.isEnabled = !it
            }

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

            tilSite.isEnabled = !mNoSite
            etSite.initInput(mSite) { mSite = it.toString() }
            scSite.initSwitch(mNoSite) {
                mNoSite = it
                tilSite.isEnabled = !it
            }

            btnEmail.apply {
                text = email
                setOnClickListener(changeEmailClick)
            }

            scShowEmail.initSwitch(mShowEmail) { mShowEmail = it }

            updatePhoneConfirmationStatus(this)
        }
    }

    private fun updatePhoneConfirmationStatus(viewHolder: GroupieViewHolder) {
        viewHolder.apply {
            btnPhoneConfirm.isVisible = !mIsPhoneConfirmed
            tvPhoneConfirmed.isVisible = mIsPhoneConfirmed
        }
    }

    private fun EditText.initInput(text: String?, onTextChanged: (text: CharSequence?) -> Unit) {
        setText(text)
        onTextChanged(onTextChanged)
    }

    private fun initSocialNetworkInput(viewHolder: GroupieViewHolder, sn: UserDataSocialLink) {
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

    private fun CheckBox.initSwitch(checked: Boolean, onCheckedChanged: (isChecked: Boolean) -> Unit) {
        isChecked = checked
        setOnCheckedChangeListener { _, isChecked -> onCheckedChanged(isChecked) }
    }

    fun checkDataValid(): Boolean {
        var isValid = true
        if (workPhone != mWorkPhone
                && !mWorkPhone.isNullOrEmpty()
                && !mWorkPhone.isValidPhoneNumber(context)
        ) {
            viewHolder.tilWorkPhone.apply {
                error = invalidNumberError
                requestFocus()
            }
            isValid = false
        }

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
            if (showEmail != mShowEmail) put(User.FIELD_USER_EMAIL_SHOW, mShowEmail)

            val workPhoneUpdate = if (mNoWorkPhone) USER_DATA_EMPTY
            else mWorkPhone
            if (workPhone != workPhoneUpdate) put(User.FIELD_USER_PHONE_WORK, workPhoneUpdate)
            if (showWorkPhone != mShowWorkPhone) put(User.FIELD_USER_PHONE_WORK_SHOW, mShowWorkPhone)
            if (mobilePhone != mMobilePhone) {
                put(User.FIELD_USER_STATUS_PHONE, mMobilePhone)
                put(User.FIELD_USER_PHONE_MOBILE, mMobilePhone)
            }
            if (showMobilePhone != mShowMobilePhone) put(User.FIELD_USER_PHONE_MOBILE_SHOW, mShowMobilePhone)

            if (socialNetworks?.toHashSet() != mSocialNetworks.toHashSet()) {
                put(User.FIELD_SOCIAL_LINKS, mSocialNetworks.filter { it.value.isNotBlank() })
            }
            val siteUpdate = if (mNoSite) USER_DATA_EMPTY
            else mSite
            if (site != siteUpdate) put(User.FIELD_USER_SITE, siteUpdate)
        }
    }
}