package com.example.ui.userprofile.common.name

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.example.app.R
import com.example.app.databinding.BottomSheetChangeNameBinding
import com.example.data.models.Argument
import com.example.data.models.UserDetail
import com.example.extensions.onTextChanged
import com.example.extensions.parcelableArgument
import com.example.ui.base.bottomSheet.BaseBSFragment
import dev.androidbroadcast.vbpd.viewBinding
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class ChangeNameFragment : BaseBSFragment(), ChangeNameContract.View {

    private val viewBinding by viewBinding(BottomSheetChangeNameBinding::bind)
    private val args by parcelableArgument<Argument<UserDetail>>(CHANGE_NAME_FRAGMENT_TAG)

    @InjectPresenter(tag = CHANGE_NAME_FRAGMENT_TAG)
    lateinit var presenter: ChangeNamePresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangeNamePresenter>

    @ProvidePresenter(tag = CHANGE_NAME_FRAGMENT_TAG)
    fun providePresenter(): ChangeNamePresenter = presenterProvider.get().apply {
        userDetail = args.value
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.apply {
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
        viewBinding.apply {
            etLastName.setText(lastName)
            etName.setText(name)
            scNoMiddleName.isChecked = isMiddleNameAbsent
//            if (isMiddleNameAbsent) etMiddleName.isEnabled = false
//            else etMiddleName.setText(middleName)
            if (!isMiddleNameAbsent){
                if (!middleName.isNullOrBlank() && middleName != "-") etMiddleName.setText(middleName)
            }

        }
    }

    override fun enableMiddleNameInput(enable: Boolean) {
        viewBinding.etMiddleName.setText("")
        viewBinding.etMiddleName.isEnabled = !enable
    }

    override fun enableBtnSave(isEnable: Boolean) {
        viewBinding.btnSave.isEnabled = isEnable
    }

    override fun showFirstNameError(show: Boolean) {
        viewBinding.tilFirstName.apply {
            if (show) showError(getString(R.string.auth_error_no_first_name))
            else showError(null)
        }
    }

    override fun showLastNameError(show: Boolean) {
        viewBinding.tilLastName.apply {
            if (show) showError(getString(R.string.auth_error_no_last_name))
            else showError(null)
        }
    }

    override fun showMiddleNameError(show: Boolean) {
        viewBinding.tilMiddleName.apply {
            if (show) showError(getString(R.string.auth_error_no_middle_name))
            else showError(null)
        }
    }

    override fun showCustomLoading() = viewBinding.btnSave.showProgressLoading(true)
    override fun hideCustomLoading() = viewBinding.btnSave.showProgressLoading(false)

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, CHANGE_NAME_FRAGMENT_TAG)

    companion object {
        const val CHANGE_NAME_FRAGMENT_TAG = "change_name_dialog"
    }

    override fun layout(): Int = R.layout.bottom_sheet_change_name
}