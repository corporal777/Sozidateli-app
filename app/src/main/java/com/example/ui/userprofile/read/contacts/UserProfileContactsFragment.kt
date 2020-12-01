package com.example.ui.userprofile.read.contacts

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserEditDataType
import com.example.data.models.user.User
import com.example.extensions.parsePhone
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_user_profile_contacts.*
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

    override fun onUserUpdated(user: User?) {
        user ?: return

        val phone = user.user_phone?.parsePhone(requireContext())
        tvPhoneMobile.isVisible = phone != null
        tvPhoneMobileTitle.isVisible = phone != null
        tvPhoneMobile.text = phone

        tvPhoneWork.text = user.user_phone_work?.parsePhone(requireContext())
        tvEmail.text = user.user_email
        tvSocialNetworks.text = user.social_links?.joinToString("\n") { it.value }
        tvSite.text = user.site?.joinToString("\n") { it }
    }

    override fun showEdit() {
        findNavController().navigate(UserProfileContactsFragmentDirections.toEdit(UserEditDataType.CONTACTS))
    }
}
