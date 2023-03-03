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
    val itemId : Long,
    val birthDay: ProfileFieldString,
    val gender: ProfileFieldString,
    val address: ProfileFieldString,
    val additional: ProfileFieldString,
    val email: ProfileFieldString,
    val personalPhone: ProfileFieldString,
    val workPhone: ProfileFieldString,
    val socialLinks: ProfileFieldString,
    val sites: ProfileFieldString,
    val publicEmails: ProfileFieldString,
    val files: ProfileFieldString
) : BindableItem<ItemRegisterEventProfileMainBinding>(itemId) {


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
                isVisible = birthDay.isChosen
                tvBirthday.setField(birthDay)
            }
            lnGender.apply {
                isVisible = gender.isChosen
                tvGender.setField(gender)
            }
            lnAddress.apply {
                isVisible = address.isChosen
                tvAddress.setField(address)
            }
            lnAdditional.apply {
                isVisible = additional.isChosen
                tvAdditional.setField(additional)
            }
            lnFiles.apply {
                isVisible = files.isChosen
                tvFiles.setField(files)
            }
            lnEmail.apply {
                isVisible = email.isChosen
                tvEmail.setField(email)
            }
            lnPublicEmail.apply {
                isVisible = publicEmails.isChosen
                tvPublicEmail.setField(publicEmails)
            }
            lnPersonalPhone.apply {
                isVisible = personalPhone.isChosen
                tvPersonalPhone.setField(personalPhone)
            }
            lnWorkPhone.apply {
                isVisible = workPhone.isChosen
                if (workPhone.isAbsent) {
                    tvWorkPhone.setTextColor(ContextCompat.getColor(context, R.color.profile_edit_button_text_enabled))
                    tvWorkPhone.text = context.getString(R.string.user_profile_no_work_phone)
                } else {
                    tvWorkPhone.setField(workPhone)
                }
            }
            lnSocialLinks.apply {
                isVisible = socialLinks.isChosen
                if (socialLinks.isAbsent) {
                    tvSocialLinks.setTextColor(ContextCompat.getColor(context, R.color.profile_edit_button_text_enabled))
                    tvSocialLinks.text = context.getString(R.string.user_profile_no_social_networks)
                } else {
                    tvSocialLinks.setField(socialLinks)
                }
            }
            lnSites.apply {
                isVisible = sites.isChosen
                if (sites.isAbsent) {
                    tvSite.setTextColor(ContextCompat.getColor(context, R.color.profile_edit_button_text_enabled))
                    tvSite.text = context.getString(R.string.user_profile_no_site)
                } else {
                    tvSite.setField(sites)
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

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is RegisterEventProfileMainItem) return false
        if (birthDay != other.birthDay) return false
        if (gender != other.gender) return false
        if (address != other.address) return false
        if (additional != other.additional) return false
        if (email != other.email) return false
        if (workPhone != other.workPhone) return false
        if (socialLinks != other.socialLinks) return false
        if (sites != other.sites) return false
        if (publicEmails != other.publicEmails) return false
        if (personalPhone != other.personalPhone) return false
        if (files != other.files) return false
        return true
    }



    override fun getLayout(): Int = R.layout.item_register_event_profile_main
}