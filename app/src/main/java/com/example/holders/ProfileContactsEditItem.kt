package com.example.holders

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.util.*
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import isValidPhoneNumber
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.*
import onTextChanged
import setOnClickListener

class ProfileContactsEditItem(
        private val context: Context,
        private val mobilePhone: FieldDetails?,
        private val workPhone: FieldDetails?,
        private val socialNetworks: FieldListDetails?,
        private val site: FieldListDetails?,
        private val email: FieldDetails?,
        private val showEmail: Boolean,
        private val changeEmailClick: () -> Unit,
        private val confirmPhoneClick: (String) -> Unit
) : Item() {

    private lateinit var viewHolder: GroupieViewHolder

    private val invalidNumberError = context.getString(R.string.invalid_phone_number_error)
    private val invalidNumberSecondError = context.getString(R.string.invalid_phone_number_second_error)
    private val invalidError = context.getString(R.string.fill_field)

    private var mMobilePhone = mobilePhone?.value
    private var mShowMobilePhone = mobilePhone?.isVisible?: false//showMobilePhone
    private var mIsPhoneConfirmed = mobilePhone?.isConfirmed?: false//isPhoneConfirmed
    private var mWorkPhone = workPhone?.value
    private var mShowWorkPhone = workPhone?.isVisible?: false//showWorkPhone
    private var mSite = (site?.value?.map { UserDataSite(value = it) } ?: emptyList())
            .map { it.copy() }
            .let {
                if (it.isEmpty()) it.plus(UserDataSite(value = ""))
                else it
            }
            .toMutableList()
    private var mNoSite = site?.absent?: false//user_site_absent
    private var mNoNetworks = socialNetworks?.absent?: false//user_social_links_absent
    private var mNoWorkPhone = workPhone?.absent?: false//user_work_phone_absent

    private var mShowEmail = showEmail
    private var mSocialNetworks = (socialNetworks?.value?.map { UserDataSocialLink(value = it) } ?: emptyList())
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

                    if (mobilePhone?.isConfirmed == true) {
                        mIsPhoneConfirmed = mMobilePhone == mobilePhone.value
                        updatePhoneConfirmationStatus(viewHolder)
                    }
                }
                addTextChangedListener(PhoneNumberFormattingTextWatcher())
            }
            validatePhone()

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
                etWorkPhone.setText("")
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
                    } else {
                        networksError.visibility = View.VISIBLE
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
                    } else {
                        sitesError.visibility = View.VISIBLE
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
                text = email?.value
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

    private fun initSocialNetworkInput(viewHolder: GroupieViewHolder, sn: UserDataSocialLink) {
        var csn = sn
        val parent = LayoutInflater.from(context).inflate(R.layout.item_profile_social_network, viewHolder.llSocialNetworks, false)
        val etSn = parent.findViewById<EditText>(R.id.etSn).apply {
            filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                source.toString().filterNot {
                    it.isWhitespace()
                }
            })
            inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            initInput(csn.value) { csn.value = it?.toString()?: "" }
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
                    viewHolder.networksError.visibility = View.GONE
                }
            }
        }

        viewHolder.llSocialNetworks.addView(parent)
    }

    private fun initSiteInput(viewHolder: GroupieViewHolder, site: UserDataSite) {
        val parent = LayoutInflater.from(context).inflate(R.layout.item_profile_social_network, viewHolder.llSites, false)
        val etSn = parent.findViewById<EditText>(R.id.etSn).apply {
            filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                source.toString().filterNot {
                    it.isWhitespace()
                }
            })
            inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            hint = context.resources.getString(R.string.profile_site)
            initInput(site.value) { site.value = it?.toString()?: ""}
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
                    viewHolder.sitesError.visibility = View.GONE
                }
            }
        }

        viewHolder.llSites.addView(parent)
    }

    fun checkBaseFieldsValid(): Boolean {
        var isValid = false
        if (mobilePhone?.value != mMobilePhone
                && !mMobilePhone.isNullOrEmpty()
                && !mMobilePhone.isValidPhoneNumber(context)
        ) {
            viewHolder.tilMobilePhone.apply {
                error = invalidNumberSecondError
            }
            isValid = true
        }
        return isValid
    }

    fun checkMaxFieldsValid(): Boolean {
        var isValid = false
        if (!mNoNetworks && mSocialNetworks[0].value.isNullOrEmpty()) isValid = true
        if (!mNoSite && mSite[0].value.isNullOrEmpty()) isValid = true
        return isValid
    }

    fun checkDataValid(): Boolean {
        var isValid = true

        isValid = validatePhone()

        if (mobilePhone?.value != mMobilePhone
                && !mMobilePhone.isNullOrEmpty()
                && !mMobilePhone.isValidPhoneNumber(context)
        ) {
            viewHolder.tilMobilePhone.apply {
                error = invalidNumberSecondError
                requestFocus()
            }
            isValid = false
        }

        if (!mNoWorkPhone && mWorkPhone.isNullOrEmpty()) {
            viewHolder.tilWorkPhone.apply {
                error = invalidError
                requestFocus()
            }
            isValid = false
        }

        return isValid
    }

    private fun validatePhone(): Boolean {
        if (mMobilePhone.isNullOrBlank()) {
            viewHolder.tilMobilePhone.apply {
                error = invalidNumberError
                requestFocus()
            }
            return false
        }
        return true
    }

    fun getDataToSave(): MutableMap<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            if (showEmail != mShowEmail) put(UserDetail.USER_EMAIL, FieldDetails(value = email?.value, isVisible = mShowEmail, isConfirmed = email?.isConfirmed))

            val workPhoneUpdate = if (mNoWorkPhone) null
            else mWorkPhone.phoneToServer()
            put(UserDetail.USER_PHONE, arrayListOf(
                    FieldDetails(value = mMobilePhone.phoneToServer(),
                            type = PHONE_PERSONAL, isConfirmed = mIsPhoneConfirmed, isVisible = mShowMobilePhone, absent = false),
                    FieldDetails(value = workPhoneUpdate, type = PHONE_WORK, isVisible = mShowWorkPhone, absent = mNoWorkPhone)
            ))

            val siteUpdate = if (mNoSite) arrayListOf()
            else mSite
            if (mNoSite != site?.absent || site.value?.toHashSet() != siteUpdate.toHashSet()) {
                put(UserDetail.USER_SITE, FieldListDetails(value = siteUpdate.filter { it.value.isNotBlank() }.map { it.value }, absent = mNoSite))
            }

            val networkUpdate = if (mNoNetworks) arrayListOf()
            else mSocialNetworks
            if (mNoNetworks != socialNetworks?.absent || socialNetworks.value?.toHashSet() != networkUpdate.toHashSet()) {
                put(UserDetail.USER_SOCIAL_LINKS, FieldListDetails(value = networkUpdate.filter { it.value.isNotBlank() }.map { it.value }, absent = mNoNetworks))
            }
        }
    }
}