package com.example.ui.userprofile.read.contacts

import additionalNumber
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.UserEditDataType
import com.example.data.models.user.User
import com.example.extensions.parsePhone
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.userprofile.passwordconfirm.PasswordConfirmFragmentDirections
import com.example.ui.userprofile.phoneconfirm.PhoneConfirmFragment
import com.example.util.PHONE_PERSONAL
import com.example.util.PHONE_WORK
import kotlinx.android.synthetic.main.fragment_user_profile_contacts.*
import kotlinx.android.synthetic.main.fragment_user_profile_contacts.tvAdditionalNumber
import kotlinx.android.synthetic.main.fragment_user_profile_contacts.tvEmail
import kotlinx.android.synthetic.main.fragment_user_profile_contacts.tvPhoneMobile
import kotlinx.android.synthetic.main.fragment_user_profile_contacts.tvPhoneMobileTitle
import kotlinx.android.synthetic.main.fragment_user_profile_contacts.tvPhoneWork
import kotlinx.android.synthetic.main.fragment_user_profile_contacts.tvSocialNetworks
import kotlinx.android.synthetic.main.item_profile_data_personal.*
import ru.ok.android.sdk.LOG_TAG
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileContactsFragment : BaseFragment(), UserProfileContactsContract.View, ToolbarFragment {

    override val title: String?
        get() = getString(R.string.user_profile_contacts)

    override fun layout() = R.layout.fragment_user_profile_contacts

    @InjectPresenter
    lateinit var presenter: UserProfileContactsPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileContactsPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileContactsPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnEdit.setOnClickListener(presenter::onEditClick)
    }

    override fun onUserUpdated(user: UserDetail?, state: String) {
        user ?: return

        val phone = user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.value?.parsePhone(requireContext())
        tvPhoneMobile.isVisible = phone != null
        tvPhoneMobileTitle.isVisible = phone != null

        tvPhoneMobile.text = phone
        val workPhone = user.phone?.firstOrNull { it.type == PHONE_WORK }
        tvPhoneWork.text = workPhone?.value?.parsePhone(requireContext())
        tvAdditionalNumber.additionalNumber(workPhone?.additional)
        tvEmail.text = (user.email?.value ?: "")
        tvEmailPublic.text = user.contactInformation.emails?.joinToString("\n") { it.value ?: "" }
        tvSocialNetworks.text = user.contactInformation.socialLinks?.values?.joinToString("\n") {
            it.value ?: ""
        }
        tvSite.text = user.contactInformation.site?.values?.joinToString("\n") { it.value ?: "" }
    }

    override fun showEdit() {
        findNavController().navigate(UserProfileContactsFragmentDirections.toEdit(UserEditDataType.CONTACTS))
    }
}
