package com.example.holders

import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemProfileDataPersonalBinding
import com.example.data.models.EmailsModel
import com.example.data.models.FieldDetails
import com.example.data.models.LinksModel
import com.example.data.models.OrganizationNew
import com.example.data.models.ToggleStringModel
import com.example.extensions.additionalNumber
import com.example.extensions.formatToDefaultDate
import com.example.extensions.parsePhone
import com.example.extensions.removeUrlUnderline
import com.example.extensions.setTextDataOrHide
import com.example.ui.views.CustomSpannableString
import com.xwray.groupie.viewbinding.BindableItem


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
    private val onOrganizationClick: (OrganizationNew) -> Unit
) : BindableItem<ItemProfileDataPersonalBinding>() {

    private val workPhoneAdditional =
        if (!workPhone?.value.isNullOrEmpty() && workPhone?.isVisible == true) workPhone.additional
        else null

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

            groupPhoneWork.apply {
                tvAdditionalNumber.additionalNumber(workPhoneAdditional)
                setTextDataOrHide(
                    tvPhoneWork,
                    workPhone?.value?.parsePhone(context),
                    workPhone?.isVisible ?: false
                )
            }
            groupPhoneMobile.apply {
                setTextDataOrHide(
                    tvPhoneMobile,
                    mobilePhone?.value?.parsePhone(context),
                    mobilePhone?.isVisible ?: false
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
                val scNetworks =
                    if (socialNetworks?.absent == true) context.getString(R.string.user_profile_no_social_networks)
                    else socialNetworks?.values?.filter { it.showInProfile == true }
                        ?.joinToString("\n") { it.value ?: "" }

                text = scNetworks
                removeUrlUnderline()
                groupSocialNetworks.isVisible = !scNetworks.isNullOrEmpty()
            }
            tvSite.apply {
                val site =
                    if (sites?.absent == true) context.getString(R.string.user_profile_no_site)
                    else sites?.values?.filter { it.showInProfile == true }
                        ?.joinToString("\n") { it.value ?: "" }

                text = site
                removeUrlUnderline()
                groupSites.isVisible = !site.isNullOrEmpty()
            }

            tvPhoneConfirmed.isVisible = mobilePhoneConfirmed
        }
    }

    override fun initializeViewBinding(view: View) = ItemProfileDataPersonalBinding.bind(view)
    override fun getLayout() = R.layout.item_profile_data_personal
}