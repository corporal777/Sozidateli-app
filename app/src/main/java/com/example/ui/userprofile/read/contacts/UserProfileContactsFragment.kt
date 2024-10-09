package com.example.ui.userprofile.read.contacts

import com.example.extensions.additionalNumber
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.app.R
import com.example.data.models.UserDetail
import com.example.app.databinding.FragmentUserProfileContactsBinding
import com.example.extensions.parsePhone
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.PHONE_PERSONAL
import com.example.util.PHONE_WORK
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileContactsFragment : BaseFragment<FragmentUserProfileContactsBinding>(),
    UserProfileContactsContract.View, ToolbarFragment {

    override fun layout() = R.layout.fragment_user_profile_contacts

    @InjectPresenter
    lateinit var presenter: UserProfileContactsPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileContactsPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileContactsPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnEdit.setOnClickListener(presenter::onEditClick)
        }
    }

    override fun setUserData(user: UserDetail, state: String) {
        val phone =
            user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.value?.parsePhone(requireContext())
        mBinding.apply {
            tvPhoneMobile.isVisible = phone != null
            tvPhoneMobileTitle.isVisible = phone != null
            tvPhoneMobile.text = phone

            val workPhone = user.phone?.firstOrNull { it.type == PHONE_WORK }
            tvPhoneWork.text = workPhone?.value?.parsePhone(requireContext())

            tvAdditionalNumber.additionalNumber(workPhone?.additional)

            tvEmail.text = (user.email?.value ?: "")
            tvEmailPublic.text =
                user.contactInformation.emails?.joinToString("\n") { it.value ?: "" }

            tvSocialNetworks.text =
                if (user.contactInformation.socialLinks?.absent == true) getString(R.string.user_profile_no_social_networks)
                else user.contactInformation.socialLinks?.values?.joinToString("\n") { it.value ?: "" }

            tvSite.text =
                if (user.contactInformation.site?.absent == true) getString(R.string.user_profile_no_site)
                else user.contactInformation.site?.values?.joinToString("\n") { it.value ?: "" }

        }
    }

    override fun showEdit() {
        findNavController().navigate(R.id.editContactsFragment)
    }

    override val title: CharSequence by lazy { getString(R.string.user_profile_contacts) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {}
}
