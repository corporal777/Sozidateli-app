package com.example.holders

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Organization
import com.example.util.ClickableSpan
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_personal.*
import setOnClickListener
import setTextDataOrHide

class ProfileDataPersonalItem(
        private val organizations: List<Organization>?,
        private val email: String?,
        private val workPhone: String?,
        private val mobilePhone: String?,
        private val gender: String?,
        private val birthday: String?,
        private val city: String?,
        private val socialNetworks: List<String>?,
        private val onOrganizationClick: (Organization) -> Unit
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvOrganization.movementMethod = LinkMovementMethod.getInstance()

            groupOrganization.setTextDataOrHide(tvOrganization, organizations?.joinTo(SpannableStringBuilder(), "\n") {
                SpannableString(it.name).apply {
                    setSpan(ClickableSpan { onOrganizationClick(it) }, 0, it.name.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
            })

            groupEmail.setTextDataOrHide(tvEmail, email)
            groupPhoneWork.setTextDataOrHide(tvPhoneWork, workPhone)
            groupPhoneMobile.setTextDataOrHide(tvPhoneMobile, mobilePhone)
            groupGender.setTextDataOrHide(tvGender, gender)
            groupBirthday.setTextDataOrHide(tvBirthday, birthday)
            groupCity.setTextDataOrHide(tvCity, city)
            groupSocialNetworks.setTextDataOrHide(tvSocialNetworks, socialNetworks?.joinToString("\n"))
        }
    }

    override fun getLayout() = R.layout.item_profile_data_personal
}