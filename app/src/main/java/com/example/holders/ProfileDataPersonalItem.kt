package com.example.holders

import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Organization
import com.example.extensions.parsePhone
import com.example.util.ClickableSpan
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_personal.*
import setTextDataOrHide

class ProfileDataPersonalItem(
        private val organizations: List<Organization>?,
        private val email: String?,
        private val workPhone: String?,
        private val mobilePhone: String?,
        private val mobilePhoneConfirmed: Boolean,
        private val gender: String?,
        private val birthday: String?,
        private val city: String?,
        private val socialNetworks: List<String>?,
        private val onOrganizationClick: (Organization) -> Unit
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvOrganization.movementMethod = LinkMovementMethod.getInstance()

            val organizationStringBuilder = SpannableStringBuilder()
            groupOrganization.setTextDataOrHide(tvOrganization, organizations?.joinTo(organizationStringBuilder, "\n") {
                it.name.toSpannable().apply {
                    set(0, this.length, ClickableSpan {
                        onOrganizationClick(it)
                    })
                }
            })

            groupEmail.setTextDataOrHide(tvEmail, email)
            groupPhoneWork.apply { setTextDataOrHide(tvPhoneWork, workPhone?.parsePhone(context)) }
            groupPhoneMobile.apply { setTextDataOrHide(tvPhoneMobile, mobilePhone?.parsePhone(context)) }
            groupGender.setTextDataOrHide(tvGender, gender)
            groupBirthday.setTextDataOrHide(tvBirthday, birthday)
            groupCity.setTextDataOrHide(tvCity, city)
            groupSocialNetworks.setTextDataOrHide(tvSocialNetworks, socialNetworks?.joinToString("\n"))

            tvPhoneConfirmed.isVisible = mobilePhoneConfirmed
        }
    }

    override fun getLayout() = R.layout.item_profile_data_personal
}