package com.example.holders

import android.graphics.Paint
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.example.R
import com.example.data.models.Organization
import com.example.util.ClickableSpan
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_personal_data.*
import removeUrlUnderline


class ProfilePersonalDataItem(
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

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvOrganization.movementMethod = LinkMovementMethod.getInstance()

            groupOrganization.setData(tvOrganization, organizations?.joinTo(SpannableStringBuilder(), "\n") {
                SpannableString(it.name).apply {
                    setSpan(ClickableSpan { onOrganizationClick(it) }, 0, it.name.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                }
            })

            groupEmail.setData(tvEmail, email)
            groupPhoneWork.setData(tvPhoneWork, workPhone)
            groupPhoneMobile.setData(tvPhoneMobile, mobilePhone)
            groupGender.setData(tvGender, gender)
            groupBirthday.setData(tvBirthday, birthday)
            groupCity.setData(tvCity, city)
            groupSocialNetworks.setData(tvSocialNetworks, socialNetworks?.joinToString("\n"))
        }
    }

    private fun Group.setData(textField: TextView, dataText: CharSequence?) {
        if (dataText.isNullOrBlank()) {
            visibility = View.GONE
            textField.text = null
        } else {
            textField.apply {
                text = dataText
                removeUrlUnderline()
            }
            visibility = View.VISIBLE
        }
    }

    override fun getLayout() = R.layout.item_profile_personal_data
}