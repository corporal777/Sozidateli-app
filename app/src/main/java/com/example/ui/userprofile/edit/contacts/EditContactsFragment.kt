package com.example.ui.userprofile.edit.contacts

import androidx.core.os.bundleOf
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.extensions.findItemByShort
import com.example.extensions.updateItem
import com.example.holders.PlaceholderItem
import com.example.holders.ProfileContactsEditItem
import com.example.ui.userprofile.base.BaseUserProfileEditFragment
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.util.PHONE_PERSONAL
import com.example.util.PHONE_WORK
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class EditContactsFragment : BaseUserProfileEditFragment(), EditContactsContract.View {

    @InjectPresenter
    lateinit var presenter: EditContactsPresenter

    @Inject
    lateinit var presenterProvider: Provider<EditContactsPresenter>

    @ProvidePresenter
    fun providePresenter(): EditContactsPresenter = presenterProvider.get()

    private lateinit var data: ProfileContactsEditItem

    override fun setPlaceholder() {
        groupAdapter.updateItem(PlaceholderItem(PlaceholderItem.Type.CONTACTS))
    }

    override fun setContactsData(user: UserDetail) {
        val userPhone = user.phone?.firstOrNull { it.type == PHONE_PERSONAL }
        val workPhone = user.phone?.firstOrNull { it.type == PHONE_WORK }

        val item = ProfileContactsEditItem(
            requireContext(),
            userPhone,
            workPhone,
            user.email,
            user.contactInformation.socialLinks,
            user.contactInformation.site,
            user.contactInformation.emails,
            presenter::onChangeEmailClick
        ) { presenter.checkPhoneIsUnique(it, false) }.apply { data = this }

        groupAdapter.updateItem(item)

        onSaveClick = {
            if (item.checkDataValid()) {
                if (userPhone?.isConfirmed == true) {
                    if (item.newPhoneIsConfirmed())
                        showEditWarning { presenter.onSaveContactsClick(item.getDataToSave()) }
                    else presenter.checkPhoneIsUnique(item.getValidatedPhone(), true)
                } else presenter.onSaveContactsClick(item.getDataToSave())
            }
        }
    }

    override fun showPhoneNotUnique(phone: String, withUpdate: Boolean) {
        DefaultAlertDialog(
            requireContext(),
            null,
            getString(R.string.confirm_phone_text, phone),
            getString(R.string.confirm_phone_positive),
            getString(R.string.event_register_no_form_negative)
        ).setSelectCallback { showPhoneConfirmation(phone, withUpdate) }
    }

    override fun showPhoneConfirmation(phone: String, withUpdate: Boolean) {
        findNavController().navigate(
            R.id.phoneCodeConfirmFragment,
            bundleOf("phone" to phone, "fromRegister" to false),
        )
        setFragmentResultListener("confirm") { _, _ ->
            presenter.onUpdatePhone()
            if (withUpdate) presenter.onSaveContactsClick(data.getDataToSave())
            clearFragmentResultListener("confirm")
        }
    }

    override fun updatePhone(phone: FieldDetails?) {
        groupAdapter.findItemByShort<ProfileContactsEditItem> { true }?.setPhoneConfirmed(phone)
    }

    override fun showChangeEmail() {
        DefaultAlertDialog(
            requireContext(),
            null,
            getString(R.string.change_email_text),
            getString(R.string.change_email_positive_button),
            getString(R.string.revoke)
        ).setSelectCallback { findNavController().navigate(R.id.user_profile_settings_fragment) }
    }

    private fun showEditWarning(block: () -> Unit) {
        showEditWarning(
            presenter.getBaseUserState(),
            presenter.getMaxUserState(),
            data.checkBaseFieldsValid(),
            data.checkMaxFieldsValid()
        ) { block.invoke() }
    }

    override val title: CharSequence by lazy { getString(R.string.user_profile_contacts) }
}