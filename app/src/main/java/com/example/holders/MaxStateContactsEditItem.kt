package com.example.holders

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemMaxStateMainInfoBinding
import com.example.app.databinding.ItemProfileSocialNetworkBinding
import com.example.data.models.ContactInformationModel
import com.example.data.models.EmailsModel
import com.example.data.models.FieldDetails
import com.example.data.models.LinksModel
import com.example.data.models.ToggleStringModel
import com.example.data.models.UserDataSite
import com.example.data.models.UserDataSocialLink
import com.example.data.models.UserDetail
import com.example.extensions.phoneToServer
import com.example.common.PHONE_PERSONAL
import com.example.common.PHONE_WORK
import com.example.util.Utils
import com.example.util.Utils.validatePhoneBeforeSend
import com.example.util.initInput
import com.example.util.initSwitch
import com.xwray.groupie.viewbinding.BindableItem


class MaxStateContactsEditItem(
    id: Long,
    private val context: Context,
    private val mobilePhone: FieldDetails?,
    private val workPhone: FieldDetails?,
    private val socialNetworks: LinksModel?,
    private val site: LinksModel?,
    private val notes: ToggleStringModel?,
    private val emails: List<EmailsModel>?,
    private val addInfoClick:() -> Unit,
    private val enableNextButton:(enable: Boolean) -> Unit
) : BindableItem<ItemMaxStateMainInfoBinding>(id) {

    private val invalidNumberSecondError =
        context.getString(R.string.invalid_phone_number_second_error)

    private var mNotes = notes?.value
    private var mNotesShow = notes?.showInProfile?: true
    private val isNoteVisible = notes?.value.isNullOrEmpty()
    private var mWorkPhone = workPhone?.value
    private var mShowWorkPhone = workPhone?.isVisible?: false//showWorkPhone
    private val isWorkPhoneVisible = workPhone?.value.isNullOrEmpty()
    private var mAdditionalPhone = workPhone?.additional
    private var mSite = (site?.values?.map { UserDataSite(value = it.value?: "", showInProfile = it.showInProfile?: false) } ?: emptyList())
        .map { it.copy() }
        .let {
            if (it.isEmpty()) it.plus(UserDataSite(value = "", showInProfile = false))
            else it
        }.toMutableList()
    private val isSitesVisible = site?.values?.isEmpty() == true
    private var mNoSite = site?.absent?: false//user_site_absent
    private var mNoNetworks = socialNetworks?.absent?: false//user_social_links_absent
    private var mNoWorkPhone = workPhone?.absent?: false//user_work_phone_absent

    private var mSocialNetworks = (socialNetworks?.values?.map { UserDataSocialLink(value = it.value?: "", showInProfile = it.showInProfile?: false) } ?: emptyList())
        .map { it.copy() }
        .let {
            if (it.isEmpty()) it.plus(UserDataSocialLink(value = "", showInProfile = false))
            else it
        }.toMutableList()
    private val isNetworkVisible = socialNetworks?.values?.isEmpty() == true


    private lateinit var mBinding: ItemMaxStateMainInfoBinding
    override fun bind(viewBinding: ItemMaxStateMainInfoBinding, position: Int) {
        this.mBinding = viewBinding
        viewBinding.apply {
            layWorkPhone.isVisible = isWorkPhoneVisible
            if (isWorkPhoneVisible) {
                etWorkPhone.apply {
                    initInput(mWorkPhone) {
                        mWorkPhone = it.toString()
                        if (it?.isNotEmpty() == true && tilWorkPhone.error != null) tilWorkPhone.error = null
                        checkDataValid()
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
                    checkDataValid()
                }
                etAdditionalNumber.apply {
                    initInput(mAdditionalPhone) {
                        mAdditionalPhone = it.toString()
                    }
                }
            }


            layNetwork.isVisible = isNetworkVisible
            if (isNetworkVisible) {
                llSocialNetworks.removeAllViews()
                mSocialNetworks.forEach { initSocialNetworkInput(viewBinding, it) }
                btnSocialNetworkAdd.apply {
                    setOnClickListener {
                        if (!mSocialNetworks.lastOrNull()?.value.isNullOrBlank()) {
                            UserDataSocialLink(value = "", showInProfile = false).apply {
                                mSocialNetworks.add(this)
                                initSocialNetworkInput(viewBinding, this)
                            }
                        } else {
                            networksError.visibility = View.VISIBLE
                        }
                    }
                }
                checkNetworks()
                scNoSocialNetworks.initSwitch(mNoNetworks) {
                    mNoNetworks = it
                    checkNetworks()
                    checkDataValid()
                }
            }

            laySites.isVisible = isSitesVisible
            if (isSitesVisible) {
                llSites.removeAllViews()
                mSite.forEach { initSiteInput(viewBinding, it) }
                btnSiteAdd.apply {
                    setOnClickListener {
                        if (!mSite.lastOrNull()?.value.isNullOrBlank()) {
                            UserDataSite(value = "", showInProfile = false).apply {
                                mSite.add(this)
                                initSiteInput(viewBinding, this)
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
                    checkDataValid()
                }
            }

            layNotes.isVisible = isNoteVisible
            if (isNoteVisible) {
                etNotes.apply {
                    setText(mNotes)
                    onTextChanged {
                        mNotes = it?.toString()
                        checkDataValid()
                    }
                }
                scNotes.isVisible = notes?.value?.isNullOrEmpty() == false
                scNotes.initSwitch(mNotesShow) { mNotesShow = it }
                btnAddInfo.setOnClickListener {
                    addInfoClick()
                }
            }
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

    private fun initSocialNetworkInput(viewBinging: ItemMaxStateMainInfoBinding, sn: UserDataSocialLink) {
        var csn = sn
        val parent = ItemProfileSocialNetworkBinding.inflate(LayoutInflater.from(context), viewBinging.llSites, false)
        val etSn = parent.etSn.apply {
            filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                source.toString().filterNot {
                    it.isWhitespace()
                }
            })
            inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            initInput(csn.value) {
                csn.value = it?.toString()?: ""
                checkDataValid()
            }
        }

        parent.scNetwork.apply {
            isVisible = sn.value.isNotEmpty()
            initSwitch(sn.showInProfile) { sn.showInProfile = it }
        }

        parent.btnDelete.apply {
            setOnClickListener {
                if (mSocialNetworks.remove(csn)) {
                    if (mSocialNetworks.isEmpty()) {
                        csn = UserDataSocialLink(value = "", showInProfile = false)
                        mSocialNetworks.add(csn)
                        etSn.text?.clear()
                    } else {
                        viewBinging.llSocialNetworks.removeView(it.parent as View)
                    }
                    viewBinging.networksError.visibility = View.GONE
                }
            }
        }

        viewBinging.llSocialNetworks.addView(parent.root)
    }

    private fun initSiteInput(viewBinging: ItemMaxStateMainInfoBinding, site: UserDataSite) {
        val parent = ItemProfileSocialNetworkBinding.inflate(LayoutInflater.from(context), viewBinging.llSites, false)
        val etSn = parent.etSn.apply {
            filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                source.toString().filterNot {
                    it.isWhitespace()
                }
            })
            inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            hint = context.resources.getString(R.string.profile_site)
            initInput(site.value) {
                site.value = it?.toString()?: ""
                checkDataValid()
            }
        }
        parent.scNetwork.apply {
            isVisible = site.value.isNotEmpty()
            initSwitch(site.showInProfile) { site.showInProfile = it }
        }

        parent.btnDelete.apply {
            setOnClickListener {
                if (mSite.remove(site)) {
                    if (mSite.isEmpty()) {
                        site.value = ""
                        mSite.add(site)
                        etSn.text?.clear()
                    } else {
                        viewBinging.llSites.removeView(it.parent as View)
                    }
                    viewBinging.sitesError.visibility = View.GONE
                }
            }
        }
        viewBinging.llSites.addView(parent.root)
    }

    fun workPhoneIsValid(): Boolean{
        var isValid = true
        if (!mNoWorkPhone && !mWorkPhone.isNullOrEmpty() && !Utils.isPhoneNumberValid(
                mWorkPhone.phoneToServer() ?: ""
            )
        ) {
            mBinding.tilWorkPhone.apply {
                error = invalidNumberSecondError
                requestFocus()
            }
            isValid = false
        }
        return isValid
    }

    fun notValidWorkPhoneError(){
        mBinding.tilWorkPhone.apply {
            error = invalidNumberSecondError
            requestFocus()
        }
    }

    fun checkDataValid(): Boolean {
        var isValid = true
        if (!mNoWorkPhone && mWorkPhone.isNullOrEmpty()) isValid = false
        if (!mNoNetworks && mSocialNetworks[0].value.isNullOrEmpty()) isValid = false
        //if (!mNoSite && mSite[0].value.isNullOrEmpty()) isValid = false
        if (mNotes.isNullOrEmpty()) isValid = false
        enableNextButton(isValid)
        return isValid
    }

    fun getDataToSave(): MutableMap<String, Any?> {
        return mutableMapOf<String, Any?>().apply {

            val workPhoneUpdate = if (mNoWorkPhone) null
            else validatePhoneBeforeSend(mWorkPhone.phoneToServer()?: "")
            put(UserDetail.USER_PHONE, arrayListOf(
                FieldDetails(value = mobilePhone?.value,
                    type = PHONE_PERSONAL, isConfirmed = mobilePhone?.isConfirmed, isVisible = mobilePhone?.isVisible, absent = false),
                FieldDetails(value = workPhoneUpdate, type = PHONE_WORK, isVisible = mShowWorkPhone, absent = mNoWorkPhone, additional = mAdditionalPhone)
            ))

            val siteUpdate = if (mNoSite) arrayListOf() else mSite
            val networkUpdate = if (mNoNetworks) arrayListOf() else mSocialNetworks

            put(UserDetail.USER_CONTACT_INFORMATION, ContactInformationModel(site = LinksModel(values = siteUpdate.filter { it.value.isNotBlank() }.map { ToggleStringModel(it.value, it.showInProfile) }, absent = mNoSite),
                socialLinks = LinksModel(values = networkUpdate.filter { it.value.isNotBlank() }.map { ToggleStringModel(it.value, it.showInProfile) }, absent = mNoNetworks), emails = emails))


            if (notes?.value != mNotes || notes?.showInProfile != mNotesShow) put(UserDetail.USER_NOTES, ToggleStringModel(mNotes, mNotesShow))
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is MaxStateContactsEditItem) return false
        if (mobilePhone != other.mobilePhone) return false
        if (workPhone != other.workPhone) return false
        if (socialNetworks != other.socialNetworks) return false
        if (site != other.site) return false
        if (notes != other.notes) return false
        if (emails != other.emails) return false
        return true

    }

    override fun initializeViewBinding(view: View) = ItemMaxStateMainInfoBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_max_state_main_info
}