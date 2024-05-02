package com.example.ui.event.registration.items

import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.ProfileFieldFiles
import com.example.data.models.ProfileFieldString
import com.example.databinding.ItemRegisterEventProfileMainBinding
import com.example.ui.views.CustomSpannableString
import com.example.util.getColor
import com.example.util.showCustomTabsBrowser
import com.example.util.showFileBrowser
import com.xwray.groupie.databinding.BindableItem

class RegisterEventProfileMainItem(
    val itemId : Long,
    val name : ProfileFieldString,
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
    val files: ProfileFieldFiles
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
            lnName.apply {
                isVisible = name.isChosen
                tvName.setField(name)
            }
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
                tvFiles.apply {
                    val field = files.value.joinToString("\n") { it.name ?: "" }
                    if (field.isNullOrEmpty()) {
                        text = context.getString(R.string.user_profile_additional_hint)
                        if (files.isRequired) setTextColor(getColor(R.color.red_new))
                    } else {
                        setTextColor(getColor(R.color.profile_edit_button_text_enabled))
                        text = SpannableStringBuilder().apply {
                            files.value.forEachIndexed { index, file ->
                                if (index > 0) append("\n")
                                append(CustomSpannableString(file.name).apply {
                                    setClickSpan(tvFiles){
                                        if (file.isFilePDF())
                                            showFileBrowser(context, file.uri.toString())
                                        else showCustomTabsBrowser(context, file.uri.toString())
                                    }
                                })
                            }

                        }
                        highlightColor = getColor(R.color.event_tabs_text_unchecked)
                        movementMethod = LinkMovementMethod.getInstance()
                    }
                }

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
                    tvWorkPhone.setTextColor(getColor(R.color.profile_edit_button_text_enabled))
                    tvWorkPhone.text = context.getString(R.string.user_profile_no_work_phone)
                } else {
                    tvWorkPhone.setField(workPhone)
                }
            }
            lnSocialLinks.apply {
                isVisible = socialLinks.isChosen
                if (socialLinks.isAbsent) {
                    tvSocialLinks.setTextColor(getColor(R.color.profile_edit_button_text_enabled))
                    tvSocialLinks.text = context.getString(R.string.user_profile_no_social_networks)
                }else if (socialLinks.value.isNullOrEmpty()){
                    if (socialLinks.isRequired) tvSocialLinks.setTextColor(getColor(R.color.red_new))
                    tvSocialLinks.text = context.getString(R.string.user_profile_additional_hint)
                } else {
                    tvSocialLinks.apply {
                        text = SpannableStringBuilder().apply {
                            socialLinks.value!!.split("\n").forEachIndexed { index, s ->
                                if (index > 0) append("\n")
                                append(CustomSpannableString(s).apply {
                                    setClickSpan(tvSocialLinks){
                                        showCustomTabsBrowser(context, s)
                                    }
                                })
                            }
                        }
                        highlightColor = getColor(R.color.event_tabs_text_unchecked)
                        movementMethod = LinkMovementMethod.getInstance()
                    }
                }
            }
            lnSites.apply {
                isVisible = sites.isChosen
                if (sites.isAbsent) {
                    tvSite.setTextColor(getColor(R.color.profile_edit_button_text_enabled))
                    tvSite.text = context.getString(R.string.user_profile_no_site)
                } else if (sites.value.isNullOrEmpty()){
                    tvSite.text = context.getString(R.string.user_profile_additional_hint)
                    if (sites.isRequired) tvSite.setTextColor(getColor(R.color.red_new))
                } else {
                    tvSite.apply {
                        text = SpannableStringBuilder().apply {
                            sites.value!!.split("\n").forEachIndexed { index, s ->
                                if (index > 0) append("\n")
                                append(CustomSpannableString(s).apply {
                                    setClickSpan(tvSite){
                                        showCustomTabsBrowser(context, s)
                                    }
                                })
                            }
                        }
                        highlightColor = getColor(R.color.event_tabs_text_unchecked)
                        movementMethod = LinkMovementMethod.getInstance()
                    }
                }
            }
        }
    }

    private fun TextView.setField(field: ProfileFieldString) {
        if (field.value.isNullOrEmpty()) {
            text = context.getString(R.string.user_profile_additional_hint)
            if (field.isRequired) setTextColor(getColor(R.color.red_new))
        } else {
            setTextColor(getColor(R.color.profile_edit_button_text_enabled))
            text = field.value
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is RegisterEventProfileMainItem) return false
        if (name != other.name) return false
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