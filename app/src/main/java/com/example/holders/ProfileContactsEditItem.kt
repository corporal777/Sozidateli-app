package com.example.holders

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.UserDataSite
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
        private val site: List<String>?,
        private val email: String?,
        private val showEmail: Boolean,
        private val changeEmailClick: () -> Unit,
        private val confirmPhoneClick: (String) -> Unit
) : Item() {

    private lateinit var viewHolder: GroupieViewHolder

    private val invalidNumberError = context.getString(R.string.invalid_phone_number_error)
    private val invalidError = context.getString(R.string.fill_field)

    private var mMobilePhone = mobilePhone
    private var mShowMobilePhone = showMobilePhone
    private var mIsPhoneConfirmed = isPhoneConfirmed
    private var mWorkPhone = workPhone
    private var mShowWorkPhone = showWorkPhone
    private var mSite = (site?.map { UserDataSite(value = it) } ?: emptyList()).toMutableList()
    private var mNoSite = site.isNullOrEmpty()
    private var mNoNetworks = socialNetworks.isNullOrEmpty()
    private var mNoWorkPhone = workPhone == USER_DATA_EMPTY || workPhone == null || workPhone == ""

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

            etWorkPhone.apply {
                initInput(mWorkPhone) {
                    mWorkPhone = it.toString()
                    if (it?.isNotEmpty() == true && tilWorkPhone.error != null) tilWorkPhone.error = null
                }
                addTextChangedListener(PhoneNumberFormattingTextWatcher())
            }
            checkPhone()
            scShowWorkPhone.initSwitch(mShowWorkPhone) { mShowWorkPhone = it }
            scNoWorkPhone.initSwitch(mNoWorkPhone) {
                mNoWorkPhone = it
                if (it) {
                    tilWorkPhone.error = null
                }
                checkPhone()
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

            llSites.removeAllViews()
            mSite.forEach { initSiteInput(viewHolder, it) }
            btnSiteAdd.apply {
                setOnClickListener {
                    if (!mSite.lastOrNull()?.value.isNullOrBlank()) {
                        UserDataSite(value = "").apply {
                            mSite.add(this)
                            initSiteInput(viewHolder, this)
                        }
                    }
                }
            }
            checkSites()

            scSite.initSwitch(mNoSite) {
                mNoSite = it
                checkSites()
            }
            checkNetworks()
            scNoSocialNetworks.initSwitch(mNoNetworks) {
                mNoNetworks = it
                checkNetworks()
            }

            btnEmail.apply {
                text = email
                setOnClickListener(changeEmailClick)
            }

            scShowEmail.initSwitch(mShowEmail) { mShowEmail = it }

            updatePhoneConfirmationStatus(this)
        }
    }

    private fun checkPhone() {
        viewHolder.tilWorkPhone.isEnabled = !mNoWorkPhone
    }

    private fun checkSites() {
        viewHolder.apply {
            btnSiteAdd.isEnabled = !mNoSite
            llSites.isEnabled = !mNoSite

            for (i in 0 until llSites.childCount) {
                val child: View = llSites.getChildAt(i)
                child.isEnabled = !mNoSite
                (child as ViewGroup).forEach { view ->
                    view.isEnabled = !mNoSite
                }
            }

            if (mNoSite)
                sitesError.visibility = View.GONE
        }
    }

    private fun checkNetworks() {
        viewHolder.apply {
            btnSocialNetworkAdd.isEnabled = !mNoNetworks
            llSocialNetworks.isEnabled = !mNoNetworks

            for (i in 0 until llSocialNetworks.childCount) {
                val child: View = llSocialNetworks.getChildAt(i)
                child.isEnabled = !mNoNetworks
                (child as ViewGroup).forEach { view ->
                    view.isEnabled = !mNoNetworks
                }
            }

            if (mNoNetworks)
                networksError.visibility = View.GONE
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

    private fun initSiteInput(viewHolder: GroupieViewHolder, site: UserDataSite) {
        val parent = LayoutInflater.from(context).inflate(R.layout.item_profile_social_network, viewHolder.llSites, false)
        val etSn = parent.findViewById<EditText>(R.id.etSn).apply {
            hint = context.resources.getString(R.string.profile_site)
            initInput(site.value) { site.value = it?.toString() ?: "" }
        }

        parent.findViewById<View>(R.id.btnDelete).apply {
            setOnClickListener {
                if (mSite.remove(site)) {
                    if (mSite.isEmpty()) {
                        site.value = ""
                        mSite.add(site)
                        etSn.text?.clear()
                    } else {
                        viewHolder.llSites.removeView(it.parent as View)
                    }
                }
            }
        }

        viewHolder.llSites.addView(parent)
    }

    private fun CheckBox.initSwitch(checked: Boolean, onCheckedChanged: (isChecked: Boolean) -> Unit) {
        isChecked = checked
        setOnCheckedChangeListener { _, isChecked -> onCheckedChanged(isChecked) }
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

        /*if (!mNoSite && mSite.isNullOrEmpty()) {
            viewHolder.tilSite.apply {
                error = invalidError
                requestFocus()
            }
            isValid = false
        }*/
        if (!mNoSite && mSite.size == 1) {
            if (mSite[0].value.isEmpty()) {
                viewHolder.sitesError.apply {
                    visibility = View.VISIBLE
                }
                isValid = false
            }
        }

        if (!mNoWorkPhone && mWorkPhone.isNullOrEmpty()) {
            viewHolder.tilWorkPhone.apply {
                error = invalidError
                requestFocus()
            }
            isValid = false
        }

        if (!mNoNetworks && mSocialNetworks.size == 1) {
            if (mSocialNetworks[0].value.isEmpty()) {
                viewHolder.networksError.apply {
                    visibility = View.VISIBLE
                }
                isValid = false
            }
        }

        return isValid
    }

    fun getDataToSave(): Map<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            if (showEmail != mShowEmail) put(User.FIELD_USER_EMAIL_SHOW, mShowEmail)

            val workPhoneUpdate = if (mNoWorkPhone) ""
            else mWorkPhone
            if (workPhone != workPhoneUpdate) put(User.FIELD_USER_PHONE_WORK, workPhoneUpdate)
            if (showWorkPhone != mShowWorkPhone) put(User.FIELD_USER_PHONE_WORK_SHOW, mShowWorkPhone)
            if (mobilePhone != mMobilePhone) {
                put(User.FIELD_USER_STATUS_PHONE, mMobilePhone)
                put(User.FIELD_USER_PHONE_MOBILE, mMobilePhone)
            }
            if (showMobilePhone != mShowMobilePhone) put(User.FIELD_USER_PHONE_MOBILE_SHOW, mShowMobilePhone)

            /*if (socialNetworks?.toHashSet() != mSocialNetworks.toHashSet()) {
                put(User.FIELD_SOCIAL_LINKS, mSocialNetworks.filter { it.value.isNotBlank() })
            }*/
            /*val siteUpdate = if (mNoSite) ""
            else mSite
            if (site != siteUpdate) put(User.FIELD_USER_SITE, siteUpdate)*/
            val siteUpdate = if (mNoSite) arrayListOf()
            else mSite
            if (site?.toHashSet() != siteUpdate.toHashSet()) {
                put(User.FIELD_USER_SITE, siteUpdate.filter { it.value.isNotBlank() }.map { it.value })
            }

            val networkUpdate = if (mNoNetworks) arrayListOf()
            else mSocialNetworks
            if (socialNetworks?.toHashSet() != networkUpdate.toHashSet()) {
                put(User.FIELD_SOCIAL_LINKS, networkUpdate.filter { it.value.isNotBlank() })
            }
        }
    }
}