package com.example.ui.event.registration.items

import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventActivityModel
import com.example.databinding.ItemRegisterEventProfileMainBinding
import com.xwray.groupie.databinding.BindableItem
import kotlinx.android.synthetic.main.item_lecture.*

class RegisterEventProfileMainItem(
    birthDay: ProfileFieldString,
    gender: ProfileFieldString,
    address: ProfileFieldString,
    additional: ProfileFieldString,
    email: ProfileFieldString,
    personalPhone: ProfileFieldString,
    workPhone: ProfileFieldString,
    socialLinks: ProfileFieldString,
    sites: ProfileFieldString,
    publicEmails: ProfileFieldString,
    files: ProfileFieldString
) : BindableItem<ItemRegisterEventProfileMainBinding>() {

    private var userBirthDay = birthDay
    private var userGender = gender
    private var userAddress = address
    private var userAdditional = additional
    private var userEmail = email
    private var userPersonalPhone = personalPhone
    private var userWorkPhone = workPhone
    private var userSocialLinks = socialLinks
    private var userSites = sites
    private var userPublicEmails = publicEmails
    private var userFiles = files

    private lateinit var mBinding : ItemRegisterEventProfileMainBinding

    override fun bind(viewBinding: ItemRegisterEventProfileMainBinding, position: Int) {
        viewBinding.apply {
            mBinding = this
            initDataFields(this)
        }
    }

    private fun initDataFields(viewBinding: ItemRegisterEventProfileMainBinding) {
        viewBinding.apply {
            lnBirthday.apply {
                isVisible = userBirthDay.isChosen
                tvBirthday.setField(userBirthDay)
            }
            lnGender.apply {
                isVisible = userGender.isChosen
                tvGender.setField(userGender)
            }
            lnAddress.apply {
                isVisible = userAddress.isChosen
                tvAddress.setField(userAddress)
            }
            lnAdditional.apply {
                isVisible = userAdditional.isChosen
                tvAdditional.setField(userAdditional)
            }
            lnFiles.apply {
                isVisible = userFiles.isChosen
                tvFiles.setField(userFiles)
            }
            lnEmail.apply {
                isVisible = userEmail.isChosen
                tvEmail.setField(userEmail)
            }
            lnPublicEmail.apply {
                isVisible = userPublicEmails.isChosen
                tvPublicEmail.setField(userPublicEmails)
            }
            lnPersonalPhone.apply {
                isVisible = userPersonalPhone.isChosen
                tvPersonalPhone.setField(userPersonalPhone)
            }
            lnWorkPhone.apply {
                isVisible = userWorkPhone.isChosen
                if (userWorkPhone.isAbsent) {
                    tvWorkPhone.setTextColor(ContextCompat.getColor(context, R.color.profile_edit_button_text_enabled))
                    tvWorkPhone.text = context.getString(R.string.user_profile_no_work_phone)
                } else {
                    tvWorkPhone.setField(userWorkPhone)
                }
            }
            lnSocialLinks.apply {
                isVisible = userSocialLinks.isChosen
                if (userSocialLinks.isAbsent) {
                    tvSocialLinks.setTextColor(ContextCompat.getColor(context, R.color.profile_edit_button_text_enabled))
                    tvSocialLinks.text = context.getString(R.string.user_profile_no_social_networks)
                } else {
                    tvSocialLinks.setField(userSocialLinks)
                }
            }
            lnSites.apply {
                isVisible = userSites.isChosen
                if (userSites.isAbsent) {
                    tvSite.setTextColor(ContextCompat.getColor(context, R.color.profile_edit_button_text_enabled))
                    tvSite.text = context.getString(R.string.user_profile_no_site)
                } else {
                    tvSite.setField(userSites)
                }
            }
        }
    }

    private fun TextView.setField(field: ProfileFieldString) {
        if (field.value.isNullOrEmpty()) {
            text = context.getString(R.string.user_profile_additional_hint)
            if (field.isRequired) {
                setTextColor(ContextCompat.getColor(context, R.color.red_new))
            }
        } else {
            setTextColor(ContextCompat.getColor(context, R.color.profile_edit_button_text_enabled))
            text = field.value
        }
    }


    fun updateFields(payload : ProfileFieldsFormResult){
        if (payload.user_birthday != userBirthDay) userBirthDay = payload.user_birthday
        if (payload.user_gender != userGender) userGender = payload.user_gender
        if (payload.address != userAddress) userAddress = payload.address
        if (payload.user_email != userEmail) userEmail = payload.user_email
        if (payload.user_notes != userAdditional) userAdditional = payload.user_notes
        if (payload.user_phone != userPersonalPhone) userPersonalPhone = payload.user_phone
        if (payload.user_work_phone != userWorkPhone) userWorkPhone =
            payload.user_work_phone
        if (payload.user_links != userSocialLinks) userSocialLinks = payload.user_links
        if (payload.user_sites != userSites) userSites = payload.user_sites
        if (payload.user_public_email != userPublicEmails) userPublicEmails =
            payload.user_public_email
        if (payload.user_files != userFiles) userFiles = payload.user_files
        if (this::mBinding.isInitialized){
            initDataFields(mBinding)
        }
    }

//    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
//        if (other !is RegisterEventProfileMainItem) return false
//        if (userBirthDay != other.userBirthDay) return false
//        if (userGender != other.userGender) return false
//        if (userAddress != other.userAddress) return false
//        if (userEmail != other.userEmail) return false
//        if (userAdditional != other.userAdditional) return false
//        if (userPersonalPhone != other.userPersonalPhone) return false
//        if (userWorkPhone != other.userWorkPhone) return false
//        if (userSocialLinks != other.userSocialLinks) return false
//        if (userSites != other.userSites) return false
//        //if (userPublicEmails != other.userPublicEmails) return false
//        if (userFiles != other.userFiles) return false
//        return true
//    }

    override fun getLayout(): Int = R.layout.item_register_event_profile_main
}