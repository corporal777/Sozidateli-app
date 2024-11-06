package com.example.holders

import com.example.extensions.additionalNumber
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import com.example.app.R
import com.example.data.models.LinksModel
import com.example.data.models.OrganizationNew
import com.example.app.databinding.ItemProfileDataPersonalBinding
import com.example.data.models.EmailsModel
import com.example.data.models.FieldDetails
import com.example.data.models.ToggleStringModel
import com.example.extensions.formatToDefaultDate
import com.example.extensions.parsePhone
import com.example.util.ClickableSpan
import com.xwray.groupie.databinding.BindableItem
import com.example.extensions.removeUrlUnderline
import com.example.extensions.setTextDataOrHide
import com.example.ui.views.CustomSpannableString


class ProfileDataPersonalItem(
    private val organizations: List<OrganizationNew>?,
    private val email: FieldDetails?,
    private val publicEmail: List<EmailsModel>?,

    private val mobilePhone: FieldDetails?,
    private val workPhone: FieldDetails?,

    private val mobilePhoneConfirmed: Boolean,

    private val gender: ToggleStringModel?,

    private val birthday: FieldDetails?,

    private val city: String?,

    private val socialNetworks: LinksModel?,
    private val sites: LinksModel?,
    private val userPhoneWorkAdditional: String?,
    private val onOrganizationClick: (OrganizationNew) -> Unit
) : BindableItem<ItemProfileDataPersonalBinding>() {

    override fun bind(viewBinding: ItemProfileDataPersonalBinding, position: Int) {
        viewBinding.apply {
            groupOrganization.apply {
                tvOrganization.movementMethod = LinkMovementMethod.getInstance()
                val organizationStringBuilder = SpannableStringBuilder().apply {
                    organizations?.forEachIndexed { index, org ->
                        if (index > 0) append("\n")
                        append(CustomSpannableString(org.getOrganizationName()).apply {
                            setClickSpan(tvOrganization) { onOrganizationClick(org) }
                        })
                    }
                }
                setTextDataOrHide(tvOrganization, organizationStringBuilder, true)
            }


            groupEmail.apply {
                val public = publicEmail?.filter { x -> x.showInProfile }
                    ?.joinToString("\n") { it.value ?: "" }
                setTextDataOrHide(tvEmail, public, true)
            }

            tvAdditionalNumber.additionalNumber(userPhoneWorkAdditional)
            groupPhoneWork.apply {
                setTextDataOrHide(
                    tvPhoneWork,
                    workPhone?.value?.parsePhone(context),
                    workPhone?.isVisible
                )
            }
            groupPhoneMobile.apply {
                setTextDataOrHide(
                    tvPhoneMobile,
                    mobilePhone?.value?.parsePhone(context),
                    mobilePhone?.isVisible
                )
            }

            groupGender.setTextDataOrHide(tvGender, gender?.value, gender?.showInProfile)

            groupBirthday.setTextDataOrHide(
                tvBirthday,
                birthday?.value?.formatToDefaultDate(),
                birthday?.isVisible
            )

            groupCity.setTextDataOrHide(tvCity, city, true)

            tvSocialNetworks.apply {
                val scNetworks = socialNetworks?.values?.filter { it.showInProfile == true }
                    ?.joinToString("\n") { it.value ?: "" }

                text = if (socialNetworks?.absent == true) context.getString(R.string.user_profile_no_social_networks) else scNetworks
                isVisible = !scNetworks.isNullOrEmpty()
                tvSocialNetworksTitle.isVisible = !scNetworks.isNullOrEmpty()
                removeUrlUnderline()
            }
            tvSite.apply {
                val site = sites?.values?.filter { it.showInProfile == true }
                    ?.joinToString("\n") { it.value ?: "" }

                text = if (sites?.absent == true) context.getString(R.string.user_profile_no_site) else site
                isVisible = !site.isNullOrEmpty()
                tvSiteTitle.isVisible = !site.isNullOrEmpty()
                removeUrlUnderline()
            }

            tvPhoneConfirmed.isVisible = mobilePhoneConfirmed && tvPhoneMobile.isVisible
        }
    }


    override fun getLayout() = R.layout.item_profile_data_personal
}