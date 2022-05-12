package com.example.holders

import additionalNumber
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Organization
import com.example.data.models.OrganizationModel
import com.example.data.models.OrganizationNew
import com.example.data.models.ToggleStringModel
import com.example.extensions.parsePhone
import com.example.util.ClickableSpan
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_personal.*
import removeUrlUnderline
import setTextDataOrHide

class ProfileDataPersonalItem(
        private val organizations: List</*Organization*/OrganizationNew>?,
        private val email: String?,
        private val workPhone: String?,
        private val mobilePhone: String?,
        private val mobilePhoneConfirmed: Boolean,
        private val gender: String?,
        private val birthday: String?,
        private val city: String?,
        private val socialNetworks: List<ToggleStringModel>?,
        private val user_phone_work_additional: String?,
        private val onOrganizationClick: (/*Organization*/OrganizationNew) -> Unit
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvOrganization.movementMethod = LinkMovementMethod.getInstance()

            val organizationStringBuilder = SpannableStringBuilder()
            groupOrganization.setTextDataOrHide(tvOrganization, organizations?.joinTo(organizationStringBuilder, "\n") {
                (it.legalInformation?.name?.short?: "").toSpannable().apply {
                    set(0, this.length, ClickableSpan {
                        onOrganizationClick(it)
                    })
                }
            })

            groupEmail.setTextDataOrHide(tvEmail, email)
            tvAdditionalNumber.additionalNumber(user_phone_work_additional)
            groupPhoneWork.apply { setTextDataOrHide(tvPhoneWork, workPhone?.parsePhone(context)) }
            groupPhoneMobile.apply { setTextDataOrHide(tvPhoneMobile, mobilePhone?.parsePhone(context)) }
            groupGender.setTextDataOrHide(tvGender, gender)
            groupBirthday.setTextDataOrHide(tvBirthday, birthday)
            groupCity.setTextDataOrHide(tvCity, city)
            //groupSocialNetworks.setTextDataOrHide(tvSocialNetworks, socialNetworks?.joinToString("\n"))
            tvSocialNetworks.apply {
                text = socialNetworks?.joinToString("\n")
                removeUrlUnderline()
            }

            tvPhoneConfirmed.isVisible = mobilePhoneConfirmed
        }
    }

    override fun getLayout() = R.layout.item_profile_data_personal
}