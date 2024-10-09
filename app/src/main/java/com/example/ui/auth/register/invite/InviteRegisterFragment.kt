package com.example.ui.auth.register.invite

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.app.R
import com.example.app.databinding.FragmentInviteRegisterBinding
import com.example.extensions.showChangeEmailCompleteDialog
import com.example.ui.auth.login.LoginFragmentArgs
import com.example.ui.base.BaseFragment
import com.example.ui.event.list.recommendations.RecommendationsFragmentArgs
import com.example.util.getNameFilter
import com.example.util.showCustomTabsBrowser
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.onBackPressedCallback
import com.example.extensions.onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class InviteRegisterFragment : BaseFragment<FragmentInviteRegisterBinding>(),
    InviteRegisterContract.View {

    @InjectPresenter
    lateinit var presenter: InviteRegisterPresenter

    @Inject
    lateinit var presenterProvider: Provider<InviteRegisterPresenter>

    @ProvidePresenter
    fun providePresenter(): InviteRegisterPresenter = presenterProvider.get().apply {
        InviteRegisterFragmentArgs.fromBundle(requireArguments()).let { args ->
            oldEmail = args.email
            newEmail = args.email
            firstName = args.name
            lastName = args.lastName
            middleName = args.middleName
            invite = args.invite
            code = args.code
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressedCallback(true) { presenter.onClickClose() }
        mBinding.apply {
            etFirstName.apply {
                filters = getNameFilter()
                onTextChanged {
                    it?.toString()?.let { text -> presenter.onChangeFirstNameText(text) }
                }
            }
            etLastName.apply {
                filters = getNameFilter()
                onTextChanged {
                    it?.toString()?.let { text -> presenter.onChangeLastNameText(text) }
                }
            }

            etMiddleName.apply {
                filters = getNameFilter()
                onTextChanged {
                    it?.toString()?.let { text -> presenter.onChangeMiddleNameText(text) }
                }
            }
            scNoMiddleName.setOnCheckedChangeListener { _, checked ->
                presenter.onNoMiddleNameChecked(checked)
            }

            password.apply {
                setShowAgree(true)
                setHyperlinkClickCallback { showUserAgreement() }
                setPasswordValidCallback {
                    presenter.onChangePasswordText(it.password, it.isValid)
                }
                setChangedSelectionCallback {
                    presenter.onAgreeChecked(it)
                }
            }

            ivClose.setOnClickListener { presenter.onClickClose() }
            ibCancel.setOnClickListener { presenter.onClickClose() }
            ibRegister.setOnClickListener {
                presenter.onClickRegister()
            }

            ibRegistered.setOnClickListener {
                val args = LoginFragmentArgs.Builder()
                    .setEmail("")
                    .setIsRegistered(true)
                    .setInviteId(presenter.invite ?: 0)
                    .build().toBundle()
                findNavController().navigate(R.id.login_fragment, args)
            }
        }

    }

    override fun setData(
        firstName: String?,
        lastName: String?,
        middleName: String?,
        email: String?
    ) {
        mBinding.apply {
            etFirstName.setText(firstName)
            etLastName.setText(lastName)
            etMiddleName.apply {
                setText(middleName)
                isEnabled = middleName.isNullOrEmpty() || middleName == "-"
            }
            scNoMiddleName.isChecked = middleName.isNullOrEmpty() || middleName == "-"
            etEmail.setText(email)
        }
    }

    override fun showFirstNameError(show: Boolean) {
        mBinding.tilFirstName.apply {
            if (show) showError(getString(R.string.auth_error_no_first_name))
            else showError(null)
        }
    }

    override fun showLastNameError(show: Boolean) {
        mBinding.tilLastName.apply {
            if (show) showError(getString(R.string.auth_error_no_last_name))
            else showError(null)
        }
    }

    override fun showEmailError(show: Boolean) {
        mBinding.tilEmail.apply {
            if (show) showError(getString(R.string.auth_error_wrong_email))
            else showError(null)
        }
    }

    override fun enableMiddleNameInput(enable: Boolean) {
        mBinding.etMiddleName.isEnabled = enable
        if (!enable) {
            mBinding.etMiddleName.setText("")
            presenter.onChangeMiddleNameText("")
        }
    }

    override fun enableRegisterBtn(isEnable: Boolean) {
        mBinding.ibRegister.apply { isEnabled = isEnable }
    }

    override fun showUserAgreement() {
        showCustomTabsBrowser(requireContext(), getString(R.string.auth_agree_address))
    }

    override fun showEmailDialog(email: String) {
        showChangeEmailCompleteDialog(email)
    }

    override fun openHome() {
        val args = RecommendationsFragmentArgs.Builder(true).build().toBundle()
        findNavController().navigate(R.id.recommendations_fragment, args,
            navOptions { popUpTo(R.id.main_navigation) { inclusive = true } }
        )
    }

    override fun loggedOut() {
        findNavController().navigate(R.id.authorization_fragment, null,
            navOptions { popUpTo(R.id.main_navigation) { inclusive = true } }
        )
    }

    override fun layout(): Int = R.layout.fragment_invite_register
}