package com.example.holders

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.widget.AppCompatCheckBox
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
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.btnSiteAdd
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.btnSocialNetworkAdd
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.etAdditionalNumber
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.etWorkPhone
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.llSites
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.llSocialNetworks
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.networksError
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.scNoSocialNetworks
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.scNoWorkPhone
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.scShowWorkPhone
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.scSite
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.sitesError
import kotlinx.android.synthetic.main.item_profile_data_edit_contacts.tilWorkPhone
import kotlinx.android.synthetic.main.item_profile_data_edit_personal_new.*
import onTextChanged
import setOnClickListener

class ProfileContactsEditItem(
    private val context: Context,
    private val mobilePhone: FieldDetails?,
    private val workPhone: FieldDetails?,
    private val socialNetworks: LinksModel?,
    private val site: LinksModel?,
    private val email: FieldDetails?,
    private val showEmail: Boolean,
    private val emails: List<EmailsModel>,
    private val changeEmailClick: () -> Unit,
    private val confirmPhoneClick: (String) -> Unit
) : Item() {

    private lateinit var mEditedPhone: String

    private lateinit var viewHolder: GroupieViewHolder

    private val invalidNumberError = context.getString(R.string.invalid_phone_number_error)
    private val invalidNumberSecondError =
        context.getString(R.string.invalid_phone_number_second_error)
    private val invalidError = context.getString(R.string.fill_field)

    private var mMobilePhone = mobilePhone?.value
    private var mShowMobilePhone = mobilePhone?.isVisible ?: false//showMobilePhone
    private var mIsPhoneConfirmed = mobilePhone?.isConfirmed ?: false//isPhoneConfirmed

    private var mWorkPhone = workPhone?.value
    private var mShowWorkPhone = workPhone?.isVisible ?: false//showWorkPhone
    private var mNoWorkPhone = workPhone?.absent ?: false//user_work_phone_absent
    private var mAdditionalPhone = workPhone?.additional

    private var mSite = (site?.values?.map {
        UserDataSite(
            value = it.value ?: "",
            showInProfile = it.showInProfile ?: false
        )
    } ?: emptyList())
        .map { it.copy() }
        .let {
            if (it.isEmpty()) it.plus(UserDataSite(value = "", showInProfile = false))
            else it
        }
        .toMutableList()
    private var mNoSite = site?.absent ?: false//user_site_absent
    private var mNoNetworks = socialNetworks?.absent ?: false//user_social_links_absent

    private var mShowEmail = showEmail
    private var mSocialNetworks = (socialNetworks?.values?.map {
        UserDataSocialLink(
            value = it.value ?: "",
            showInProfile = it.showInProfile ?: false
        )
    } ?: emptyList())
        .map { it.copy() }
        .let {
            if (it.isEmpty()) it.plus(UserDataSocialLink(value = "", showInProfile = false))
            else it
        }
        .toMutableList()

    private var mEmails =
        (emails?.map { UserEmailsData(value = it.value ?: "", showInProfile = it.showInProfile) }
            ?: emptyList())
            .map { it.copy() }
            .let {
                if (it.isEmpty()) it.plus(UserEmailsData(value = "", showInProfile = false))
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
                    if (it?.isNotEmpty() == true && tilMobilePhone.error != null) tilMobilePhone.error =
                        null

                    if (mobilePhone?.isConfirmed == true) {
                        mIsPhoneConfirmed = it.toString().phoneToServer() == mobilePhone.value
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
                    if (it?.isNotEmpty() == true && tilWorkPhone.error != null) tilWorkPhone.error =
                        null
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
            etAdditionalNumber.apply {
                initInput(mAdditionalPhone) {
                    mAdditionalPhone = it.toString()
                }
            }

            llSocialNetworks.removeAllViews()
            mSocialNetworks.forEach { initSocialNetworkInput(viewHolder, it) }
            btnSocialNetworkAdd.apply {
                setOnClickListener {
                    if (!mSocialNetworks.lastOrNull()?.value.isNullOrBlank()) {
                        UserDataSocialLink(value = "", showInProfile = false).apply {
                            mSocialNetworks.add(this)
                            initSocialNetworkInput(viewHolder, this)
                        }
                    } else {
                        networksError.visibility = View.VISIBLE
                    }
                }
            }

            llEmails.removeAllViews()
            mEmails.forEach { initEmailsInput(viewHolder, it) }
            btnEmailAdd.apply {
                setOnClickListener {
                    if (mEmails.lastOrNull()?.value?.isNotBlank() == true && AuthValidateUtil.isValidEmail(
                            mEmails.lastOrNull()?.value
                                ?: ""
                        )
                    ) {
                        UserEmailsData(value = "", showInProfile = false).apply {
                            mEmails.add(this)
                            initEmailsInput(viewHolder, this)
                        }
                    } else {
                        if (mEmails[mEmails.size - 1].value.isEmpty()) {
                            emailsError.text = context.resources.getString(R.string.fill_field)
                        } else {
                            emailsError.text = context.resources.getString(R.string.incorrect_data)
                        }
                        emailsError.visibility = View.VISIBLE
                    }
                }
            }

            llSites.removeAllViews()
            mSite.forEach { initSiteInput(viewHolder, it) }
            btnSiteAdd.apply {
                setOnClickListener {
                    if (!mSite.lastOrNull()?.value.isNullOrBlank()) {
                        UserDataSite(value = "", showInProfile = false).apply {
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

            //scShowEmail.initSwitch(mShowEmail) { mShowEmail = it }

            updatePhoneConfirmationStatus(this)
        }
    }

    private fun checkPhone() {
        viewHolder.tilWorkPhone.isEnabled = !mNoWorkPhone
        viewHolder.tilAdditionalNumber.isEnabled = !mNoWorkPhone
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

    private fun initEmailsInput(viewHolder: GroupieViewHolder, em: UserEmailsData) {
        var csn = em
        val parent = LayoutInflater.from(context)
            .inflate(R.layout.item_profile_email, viewHolder.llEmails, false)
        val etSn = parent.findViewById<EditText>(R.id.etEm).apply {
            filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                source.toString().filterNot {
                    it.isWhitespace()
                }
            })
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            initInput(csn.value) { csn.value = it?.toString() ?: "" }
        }
        /*parent.findViewById<CheckBox>(R.id.scShowEmail).apply {
            initSwitch(csn.showInProfile) {
                csn.showInProfile = it
            }
        }*/
        parent.findViewById<View>(R.id.btnDeleteEmail).apply {
            setOnClickListener {
                if (mEmails.remove(csn)) {
                    if (mEmails.isEmpty()) {
                        csn = UserEmailsData(value = "", showInProfile = false)
                        mEmails.add(csn)
                        etSn.text?.clear()
                    } else {
                        viewHolder.llEmails.removeView(it.parent as View)
                    }
                    viewHolder.emailsError.visibility = View.GONE
                }
            }
        }

        viewHolder.llEmails.addView(parent)
    }

    private fun initSocialNetworkInput(viewHolder: GroupieViewHolder, sn: UserDataSocialLink) {
        var csn = sn
        val parent = LayoutInflater.from(context)
            .inflate(R.layout.item_profile_social_network, viewHolder.llSocialNetworks, false)
        val etSn = parent.findViewById<EditText>(R.id.etSn).apply {
            filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                source.toString().filterNot {
                    it.isWhitespace()
                }
            })
            inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            initInput(csn.value) { csn.value = it?.toString() ?: "" }
        }
        parent.findViewById<AppCompatCheckBox>(R.id.scNetwork).apply {
            isVisible = sn.value.isNotEmpty()
            initSwitch(csn.showInProfile) { csn.showInProfile = it }
        }
        parent.findViewById<View>(R.id.btnDelete).apply {
            setOnClickListener {
                if (mSocialNetworks.remove(csn)) {
                    if (mSocialNetworks.isEmpty()) {
                        csn = UserDataSocialLink(value = "", showInProfile = false)
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
        val parent = LayoutInflater.from(context)
            .inflate(R.layout.item_profile_social_network, viewHolder.llSites, false)
        val etSn = parent.findViewById<EditText>(R.id.etSn).apply {
            filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                source.toString().filterNot {
                    it.isWhitespace()
                }
            })
            inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            hint = context.resources.getString(R.string.profile_site)
            initInput(site.value) { site.value = it?.toString() ?: "" }
        }

        parent.findViewById<AppCompatCheckBox>(R.id.scNetwork).apply {
            isVisible = site.value.isNotEmpty()
            initSwitch(site.showInProfile) { site.showInProfile = it }
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
//
        if (mobilePhone?.value != mMobilePhone
            && !mMobilePhone.isNullOrEmpty()
            //&& !mMobilePhone.isValidPhoneNumber(context)
            && !Utils.newPhoneValidator(mMobilePhone.phoneToServer())
        ) {
            viewHolder.tilMobilePhone.apply {
                error = invalidNumberSecondError
                requestFocus()
            }
            isValid = false
        }


        if (!mNoWorkPhone && !mWorkPhone.isNullOrEmpty() && !Utils.newPhoneValidator(
                mWorkPhone.phoneToServer() ?: ""
            )
        ) {
            viewHolder.tilWorkPhone.apply {
                error = invalidNumberSecondError
                requestFocus()
            }
            isValid = false
        }

        if (!mEmails.lastOrNull()?.value.isNullOrEmpty()) {
            if (!AuthValidateUtil.isValidEmail(mEmails.lastOrNull()?.value ?: "")) {
                if (mEmails[mEmails.size - 1].value.isEmpty()) {
                    viewHolder.emailsError.text = context.resources.getString(R.string.fill_field)
                } else {
                    viewHolder.emailsError.text =
                        context.resources.getString(R.string.incorrect_data)
                }
                viewHolder.emailsError.visibility = View.VISIBLE
                isValid = false
            }
        }
        return isValid
    }

    private fun validatePhone(): Boolean {
        return if (mMobilePhone.isNullOrBlank()) {
            viewHolder.tilMobilePhone.apply {
                error = invalidNumberError
                requestFocus()
            }
            false
        } else {
            true
        }
    }

    fun isNewPhoneIsValid(): Boolean {
        return if (mMobilePhone.isNullOrBlank()) {
            viewHolder.tilMobilePhone.apply {
                error = invalidNumberError
                requestFocus()
            }
            false
        } else {
            Utils.isNewPhoneIsValid(getPersonalPhone())
        }
    }

    fun newPhoneIsConfirmed() = mIsPhoneConfirmed
    fun getPersonalPhone() = mMobilePhone.phoneToServer()
    fun getValidatedPhone() = Utils.validatePhoneBeforeSend(getPersonalPhone() ?: "")

    fun getDataToSave(): MutableMap<String, Any?> {
        return mutableMapOf<String, Any?>().apply {

            //add personal phone
            val list = mutableListOf<FieldDetails>()
            list.add(
                FieldDetails(
                    Utils.validatePhoneBeforeSend(getPersonalPhone() ?: ""),
                    type = PHONE_PERSONAL,
                    mShowMobilePhone,
                    mIsPhoneConfirmed,
                    false
                )
            )

            //add work phone
            val workPhoneUpdate = if (mNoWorkPhone) null
            else Utils.validatePhoneBeforeSend(mWorkPhone.phoneToServer() ?: "")

            list.add(
                FieldDetails(
                    value = workPhoneUpdate,
                    type = PHONE_WORK,
                    isVisible = mShowWorkPhone,
                    absent = mNoWorkPhone,
                    additional = mAdditionalPhone
                )
            )
            put(UserDetail.USER_PHONE, list)


            //add contact information
            val siteUpdate = if (mNoSite) arrayListOf()
            else mSite


            val networkUpdate = if (mNoNetworks) arrayListOf()
            else mSocialNetworks


            val contactEmails = mutableListOf<EmailsModel>()
            if ((mEmails.size != 1) && !mEmails[0].value.isEmpty()) {
                mEmails.forEach {
                    if (!it.value.isNullOrEmpty()) {
                        contactEmails.add(EmailsModel(value = it.value, showInProfile = true))
                    }
                }
            }

            put(
                UserDetail.USER_CONTACT_INFORMATION, ContactInformationModel(
                    site = LinksModel(values = siteUpdate.filter { it.value.isNotBlank() }
                        .map { ToggleStringModel(it.value, it.showInProfile) }, absent = mNoSite),
                    socialLinks = LinksModel(
                        values = networkUpdate.filter { it.value.isNotBlank() }
                            .map { ToggleStringModel(it.value, it.showInProfile) },
                        absent = mNoNetworks
                    ),
                    emails = contactEmails
                )
            )


        }
    }


    fun updatePhone(phone: List<FieldDetails>?) {
        mMobilePhone = phone?.firstOrNull { it.type == PHONE_PERSONAL }?.value
        mIsPhoneConfirmed = phone?.firstOrNull { it.type == PHONE_PERSONAL }?.isConfirmed ?: false
        mShowMobilePhone = phone?.firstOrNull { it.type == PHONE_PERSONAL }?.isVisible ?: true
        updatePhoneConfirmationStatus(viewHolder)
        viewHolder.etMobilePhone.apply {
            initInput(mMobilePhone) {

            }
            addTextChangedListener(PhoneNumberFormattingTextWatcher())
        }
    }

    fun setPhoneConfirmed(isConfirmed: Boolean) {
        mIsPhoneConfirmed = isConfirmed
        updatePhoneConfirmationStatus(viewHolder)
    }
}