package com.example.ui.userprofile.read.contacts

import additionalNumber
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.data.models.UserEditDataType
import com.example.databinding.FragmentUserProfileContactsBinding
import com.example.extensions.parsePhone
import com.example.ui.base.BaseFragmentNew
import com.example.ui.views.toolbar.SimpleTitleToolbar
import com.example.util.PHONE_PERSONAL
import com.example.util.PHONE_WORK
import onScrolled
import setOnClickListener
import javax.inject.Inject
import javax.inject.Provider

class UserProfileContactsFragment : BaseFragmentNew<FragmentUserProfileContactsBinding>(), UserProfileContactsContract.View, SimpleTitleToolbar {

    override fun layout() = R.layout.fragment_user_profile_contacts

    @InjectPresenter
    lateinit var presenter: UserProfileContactsPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileContactsPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileContactsPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle(getString(R.string.user_profile_contacts))
        mBinding.apply {
            nestedScrollView.onScrolled { scrollY, oldScrollY, scrollX, oldScrollX ->
                presenter.changeAppBarElevation(scrollY - oldScrollY)
            }
            btnEdit.setOnClickListener(presenter::onEditClick)
        }
    }

    override fun onUserUpdated(user: UserDetail?, state: String) {
        user ?: return

        val phone = user.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.value?.parsePhone(requireContext())
        mBinding.apply {
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
    }

    override fun showEdit() {
        findNavController().navigate(UserProfileContactsFragmentDirections.toEdit(UserEditDataType.CONTACTS))
    }
}
