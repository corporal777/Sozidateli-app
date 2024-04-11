package com.example.holders

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.*
import com.example.databinding.ItemProfileDataEditContactsBinding
import com.example.databinding.ItemProfileEmailBinding
import com.example.databinding.ItemProfileSocialNetworkBinding
import com.example.extensions.phoneToServer
import com.example.util.*
import com.example.util.Utils.isPhoneNumberValid
import com.example.util.Utils.validatePhoneBeforeSend
import com.xwray.groupie.databinding.BindableItem
import com.example.extensions.setOnClickListener

class ProfileContactsEditItem(
    private val context: Context,
    phone: FieldDetails?,
    workPhone: FieldDetails?,
    private val email: FieldDetails?,
    socialNetworks: LinksModel?,
    site: LinksModel?,
    emails: List<EmailsModel>?,
    private val changeEmailClick: () -> Unit,
    private val confirmPhoneClick: (String) -> Unit
) : BindableItem<ItemProfileDataEditContactsBinding>() {

    private lateinit var mBinding: ItemProfileDataEditContactsBinding

    private val invalidNumberError = "Введите номер телефона"
    private val invalidNumberSecondError = "Введите корректный номер"

    private var mobilePhone = phone
    private var mMobilePhone = mobilePhone?.value
    private var mShowMobilePhone = mobilePhone?.isVisible ?: false//showMobilePhone
    private var mIsPhoneConfirmed = mobilePhone?.isConfirmed ?: false//isPhoneConfirmed

    private var mWorkPhone = workPhone?.value
    private var mShowWorkPhone = workPhone?.isVisible ?: false//showWorkPhone
    private var mNoWorkPhone = workPhone?.absent ?: false//user_work_phone_absent
    private var mAdditionalPhone = workPhone?.additional


    private var mSite = arrayListOf<UserDataSite>().apply {
        if (site == null || site.values.isNullOrEmpty())
            add(UserDataSite(value = "", showInProfile = false))
        else addAll(site.values.map {
            UserDataSite(value = it.value ?: "", showInProfile = it.showInProfile ?: false)
        })
    }

    private var mNoSite = site?.absent ?: false//user_site_absent
    private var mNoNetworks = socialNetworks?.absent ?: false//user_social_links_absent

    private var mSocialNetworks = arrayListOf<UserDataSocialLink>().apply {
        if (socialNetworks == null || socialNetworks.values.isNullOrEmpty())
            add(UserDataSocialLink(value = "", showInProfile = false))
        else addAll(socialNetworks.values.map {
            UserDataSocialLink(value = it.value ?: "", showInProfile = it.showInProfile ?: false)
        })
    }

    private val publicEmails = arrayListOf<UserEmailsData>().apply {
        if (emails.isNullOrEmpty()) add(UserEmailsData("", true))
        else addAll(emails.map {
            UserEmailsData(value = it.value ?: "", showInProfile = it.showInProfile)
        })
    }

    override fun bind(viewBinding: ItemProfileDataEditContactsBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            tilMobilePhone.apply {
                error = null
                scMobilePhone.initSwitch(mShowMobilePhone) { mShowMobilePhone = it }
            }
            etMobilePhone.apply {
                filters = getPhoneFilter()
                initInput(mMobilePhone) {
                    mMobilePhone = it.toString()
                    if (!it.isNullOrEmpty() && tilMobilePhone.error != null)
                        tilMobilePhone.error = null

                    if (mobilePhone?.isConfirmed == true) {
                        mIsPhoneConfirmed = mMobilePhone.phoneToServer() == mobilePhone?.value
                        updatePhoneConfirmationStatus(viewBinding)
                    }
                }
                //addTextChangedListener(PhoneNumberFormattingTextWatcher())
            }
            btnPhoneConfirm.setOnClickListener {
                if (checkPhoneIsValid()) confirmPhoneClick(getValidatedPhone())
            }
            etWorkPhone.apply {
                filters = getPhoneFilter()
                initInput(mWorkPhone) {
                    mWorkPhone = it.toString()
                    if (it?.isNotEmpty() == true && tilWorkPhone.error != null)
                        tilWorkPhone.error = null
                }
                addTextChangedListener(PhoneNumberFormattingTextWatcher())
            }
            checkPhone()
            scShowWorkPhone.initSwitch(mShowWorkPhone) { mShowWorkPhone = it }
            scNoWorkPhone.initSwitch(mNoWorkPhone) {
                mNoWorkPhone = it
                if (it) tilWorkPhone.showError(null)
                etWorkPhone.setText("")
                checkPhone()
            }
            etAdditionalNumber.apply {
                filters = getPhoneFilter()
                initInput(mAdditionalPhone) {
                    mAdditionalPhone = it.toString()
                }
            }

            llSocialNetworks.removeAllViews()
            mSocialNetworks.forEach { initSocialNetworkInput(viewBinding, it) }
            btnSocialNetworkAdd.apply {
                setOnClickListener {
                    if (!mSocialNetworks.lastOrNull()?.value.isNullOrBlank()) {
                        UserDataSocialLink(value = "", showInProfile = false).apply {
                            mSocialNetworks.add(this)
                            initSocialNetworkInput(viewBinding, this)
                        }
                    } else networksError.visibility = View.VISIBLE
                }
            }

            llEmails.removeAllViews()
            publicEmails.forEach { initEmailsInput(viewBinding, it) }
            btnEmailAdd.apply {
                setOnClickListener {
                    if (publicEmails.lastOrNull()?.value?.isNotBlank() == true
                        && AuthValidateUtil.isValidEmail(publicEmails.lastOrNull()?.value ?: "")
                    ) {
                        UserEmailsData(value = "", showInProfile = false).apply {
                            publicEmails.add(this)
                            initEmailsInput(viewBinding, this)
                        }
                    } else {
                        if (publicEmails[publicEmails.size - 1].value.isEmpty())
                            emailsError.text = context.resources.getString(R.string.fill_field)
                        else emailsError.text = context.resources.getString(R.string.incorrect_data)
                        emailsError.visibility = View.VISIBLE
                    }
                }
            }

            llSites.removeAllViews()
            mSite.forEach { initSiteInput(viewBinding, it) }
            btnSiteAdd.apply {
                setOnClickListener {
                    if (!mSite.lastOrNull()?.value.isNullOrBlank()) {
                        UserDataSite(value = "", showInProfile = false).apply {
                            mSite.add(this)
                            initSiteInput(viewBinding, this)
                        }
                    } else sitesError.visibility = View.VISIBLE
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
            updatePhoneConfirmationStatus(this)
        }
    }


    private fun checkPhone() {
        mBinding.tilWorkPhone.isEnabled = !mNoWorkPhone
        mBinding.tilAdditionalNumber.isEnabled = !mNoWorkPhone
    }

    private fun checkSites() {
        mBinding.apply {
            btnSiteAdd.isEnabled = !mNoSite
            llSites.isEnabled = !mNoSite

            for (i in 0 until llSites.childCount) {
                val child: View = llSites.getChildAt(i)
                child.isEnabled = !mNoSite
                (child as ViewGroup).forEach { view ->
                    view.isEnabled = !mNoSite
                }
            }
            if (mNoSite) sitesError.visibility = View.GONE
        }
    }

    private fun checkNetworks() {
        mBinding.apply {
            btnSocialNetworkAdd.isEnabled = !mNoNetworks
            llSocialNetworks.isEnabled = !mNoNetworks

            for (i in 0 until llSocialNetworks.childCount) {
                val child: View = llSocialNetworks.getChildAt(i)
                child.isEnabled = !mNoNetworks
                (child as ViewGroup).forEach { view ->
                    view.isEnabled = !mNoNetworks
                }
            }
            if (mNoNetworks) networksError.visibility = View.GONE
        }
    }


    private fun initEmailsInput(b: ItemProfileDataEditContactsBinding, email: UserEmailsData) {
        val binding =
            ItemProfileEmailBinding.inflate(LayoutInflater.from(context), b.llEmails, false)
        binding.etEm.apply {
            filters = getSiteFilter()
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            initInput(email.value) {
                email.value = it?.toString() ?: ""
                b.emailsError.visibility = View.GONE
            }
        }
        binding.btnDeleteEmail.setOnClickListener {
            binding.etEm.text?.clear()
            if (publicEmails.size < 2) email.value = ""
            else {
                if (publicEmails.remove(email)) {
                    b.llEmails.removeView(it.parent as View)
                    b.emailsError.visibility = View.GONE
                }
            }
        }
        b.llEmails.addView(binding.root)
    }

    private fun initSocialNetworkInput(b: ItemProfileDataEditContactsBinding, socialNetwork: UserDataSocialLink) {
        val binding = ItemProfileSocialNetworkBinding.inflate(
            LayoutInflater.from(context),
            b.llSocialNetworks,
            false
        )
        binding.etSn.apply {
            filters = getSiteFilter()
            inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            initInput(socialNetwork.value) { socialNetwork.value = it?.toString() ?: "" }
        }
        binding.scNetwork.apply {
            isVisible = socialNetwork.value.isNotEmpty()
            initSwitch(socialNetwork.showInProfile) { socialNetwork.showInProfile = it }
        }
        binding.btnDelete.setOnClickListener {
            binding.etSn.text?.clear()
            if (mSocialNetworks.size < 2) socialNetwork.value = ""
            else {
                if (mSocialNetworks.remove(socialNetwork)) {
                    b.llSocialNetworks.removeView(binding.root)
                    b.networksError.visibility = View.GONE
                }
            }
        }

        b.llSocialNetworks.addView(binding.root)
    }

    private fun initSiteInput(b: ItemProfileDataEditContactsBinding, site: UserDataSite) {
        val binding = ItemProfileSocialNetworkBinding.inflate(
            LayoutInflater.from(context),
            b.llSocialNetworks,
            false
        )

        binding.etSn.apply {
            filters = getSiteFilter()
            inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            hint = context.resources.getString(R.string.profile_site)
            initInput(site.value) { site.value = it?.toString() ?: "" }
        }

        binding.scNetwork.apply {
            isVisible = site.value.isNotEmpty()
            initSwitch(site.showInProfile) { site.showInProfile = it }
        }

        binding.btnDelete.setOnClickListener {
            binding.etSn.text?.clear()
            if (mSite.size < 2) site.value = ""
            else {
                if (mSite.remove(site)) {
                    b.llSites.removeView(binding.root)
                    b.sitesError.visibility = View.GONE
                }
            }
        }
        b.llSites.addView(binding.root)
    }

    fun checkBaseFieldsValid(): Boolean {
        var isValid = false
        if (mobilePhone?.value != mMobilePhone && !isPhoneNumberValid(mMobilePhone)) {
            mBinding.tilMobilePhone.showError(invalidNumberSecondError)
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

        if (!mobilePhone?.value.isNullOrBlank() && mMobilePhone.isNullOrBlank()) {
            mBinding.tilMobilePhone.apply {
                showError(invalidNumberError)
                requestFocus()
            }
            isValid = false
        }

        if (mobilePhone?.value != mMobilePhone && !isPhoneNumberValid(mMobilePhone.phoneToServer())) {
            mBinding.tilMobilePhone.apply {
                showError(invalidNumberSecondError)
                requestFocus()
            }
            isValid = false
        }

        if (!mNoWorkPhone && !isPhoneNumberValid(mWorkPhone.phoneToServer() ?: "")) {
            mBinding.tilWorkPhone.apply {
                showError(invalidNumberSecondError)
                requestFocus()
            }
            isValid = false
        }

        if (!publicEmails.lastOrNull()?.value.isNullOrEmpty()) {
            if (!AuthValidateUtil.isValidEmail(publicEmails.lastOrNull()?.value ?: "")) {
                if (publicEmails[publicEmails.size - 1].value.isEmpty()) {
                    mBinding.emailsError.text = context.resources.getString(R.string.fill_field)
                } else mBinding.emailsError.text = context.resources.getString(R.string.incorrect_data)
                mBinding.emailsError.visibility = View.VISIBLE
                isValid = false
            }
        }
        return isValid
    }


    private fun checkPhoneIsValid(): Boolean {
        var isValid = true
        if (mMobilePhone.isNullOrBlank()){
            isValid = false
            mBinding.tilMobilePhone.showError(invalidNumberError)
        } else {
            if (!isPhoneNumberValid(mMobilePhone.phoneToServer())){
                isValid = false
                mBinding.tilMobilePhone.showError(invalidNumberSecondError)
            }
        }
        return isValid
    }

    fun newPhoneIsConfirmed() = mIsPhoneConfirmed
    fun getValidatedPhone() = validatePhoneBeforeSend(mMobilePhone.phoneToServer() ?: "")

    fun getDataToSave(): MutableMap<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            val list = mutableListOf<FieldDetails>().apply {
                //add personal phone
                add(
                    FieldDetails(
                        getValidatedPhone(),
                        type = PHONE_PERSONAL,
                        mShowMobilePhone,
                        mIsPhoneConfirmed,
                        false
                    )
                )
                //add work phone
                add(
                    FieldDetails(
                        value = if (mNoWorkPhone || mWorkPhone.isNullOrEmpty()) null
                        else validatePhoneBeforeSend(mWorkPhone.phoneToServer()!!),
                        type = PHONE_WORK,
                        isVisible = mShowWorkPhone,
                        absent = mNoWorkPhone,
                        additional = mAdditionalPhone
                    )
                )
            }
            put(UserDetail.USER_PHONE, list)


            //add contact information
            val siteUpdate = if (mNoSite) arrayListOf() else mSite
            val networkUpdate = if (mNoNetworks) arrayListOf() else mSocialNetworks
            val contactEmails = mutableListOf<EmailsModel>().apply {
                publicEmails.forEach {
                    if (!it.value.isNullOrEmpty()) add(EmailsModel(it.value,true))
                }
            }
            put(
                UserDetail.USER_CONTACT_INFORMATION, ContactInformationModel(
                    site = LinksModel(values = siteUpdate.filter { it.value.isNotBlank() }.map { ToggleStringModel(it.value, it.showInProfile) }, absent = mNoSite),
                    socialLinks = LinksModel(values = networkUpdate.filter { it.value.isNotBlank() }.map { ToggleStringModel(it.value, it.showInProfile) }, absent = mNoNetworks),
                    emails = contactEmails
                )
            )
        }
    }


    private fun updatePhoneConfirmationStatus(binding: ItemProfileDataEditContactsBinding) {
        binding.apply {
            btnPhoneConfirm.isVisible = !mIsPhoneConfirmed
            tvPhoneConfirmed.isVisible = mIsPhoneConfirmed
        }
    }

    fun setPhoneConfirmed(phone: FieldDetails?) {
        if (!this::mBinding.isInitialized) return
        mobilePhone = phone
        mIsPhoneConfirmed = phone?.isConfirmed ?: false
        updatePhoneConfirmationStatus(mBinding)
    }


    override fun getLayout() = R.layout.item_profile_data_edit_contacts
}