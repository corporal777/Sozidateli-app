package com.example.ui.userprofile.read.settings.change_name

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.BottomSheetChangeNameBinding
import com.example.databinding.BottomSheetChangePasswordBinding
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.userprofile.read.settings.change_password.ChangePasswordContract
import com.example.ui.userprofile.read.settings.change_password.ChangePasswordFragment
import com.example.ui.userprofile.read.settings.change_password.ChangePasswordPresenter
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class ChangeNameFragment(private val user: UserDetail) :
    BaseBottomSheetFragment<BottomSheetChangeNameBinding>(), ChangeNameContract.View {

    @InjectPresenter(type = PresenterType.WEAK, tag = CHANGE_NAME_FRAGMENT_TAG)
    lateinit var presenter: ChangeNamePresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangeNamePresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = CHANGE_NAME_FRAGMENT_TAG)
    fun providePresenter(): ChangeNamePresenter = presenterProvider.get().apply {
        userDetail = user
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etLastName.onTextChanged {
                presenter.performChangeLastName(it.toString())
            }
            etName.onTextChanged {
                presenter.performChangeName(it.toString())
            }
            etMiddleName.onTextChanged {
                presenter.performChangeMiddleName(it.toString())
            }
            scNoMiddleName.setOnCheckedChangeListener { _, checked ->
                presenter.performSetNoMiddleName(checked)
            }
            btnSave.setOnClickListener {
                presenter.onSaveNameClick()
            }
        }
    }

    override fun setUserName(name: String?, lastName: String?, middleName: String?, isMiddleNameAbsent: Boolean) {
        mBinding.apply {
            etLastName.setText(lastName)
            etName.setText(name)
            scNoMiddleName.isChecked = isMiddleNameAbsent
//            if (isMiddleNameAbsent) etMiddleName.isEnabled = false
//            else etMiddleName.setText(middleName)

            if (!middleName.isNullOrBlank() && middleName != "-") etMiddleName.setText(middleName)
        }
    }

    override fun enableMiddleNameInput(enable: Boolean) {
        mBinding.etMiddleName.setText("")
        mBinding.etMiddleName.isEnabled = !enable
    }

    override fun enableBtnSave(isEnable: Boolean) {
        mBinding.btnSave.isEnabled = isEnable
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

    override fun showMiddleNameError(show: Boolean) {
        mBinding.tilMiddleName.apply {
            if (show) showError(getString(R.string.auth_error_no_middle_name))
            else showError(null)
        }
    }


    companion object {
        const val CHANGE_NAME_FRAGMENT_TAG = "change_name_tag"
    }

    override fun layout(): Int = R.layout.bottom_sheet_change_name
}