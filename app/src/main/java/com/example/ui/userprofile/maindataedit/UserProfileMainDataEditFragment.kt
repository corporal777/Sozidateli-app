package com.example.ui.userprofile.maindataedit

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.user.User
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.util.USER_MIDDLE_NAME_EMPTY
import kotlinx.android.synthetic.main.fragment_user_profile_main_data_edit.*
import javax.inject.Inject
import javax.inject.Provider

class UserProfileMainDataEditFragment : BaseFragment(), UserProfileMainDataEditContract.View, ToolbarFragment {

    override val title: String?
        get() = getString(R.string.user_profile_main_info)

    private val emptyInputError by lazy {
        getString(R.string.profile_edit_empty_field_error)
    }

    override fun layout() = R.layout.fragment_user_profile_main_data_edit

    @InjectPresenter
    lateinit var presenter: UserProfileMainDataEditPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserProfileMainDataEditPresenter>

    @ProvidePresenter
    fun providePresenter(): UserProfileMainDataEditPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSave.setOnClickListener {
            presenter.onSaveClick(
                    etSurname.text?.toString(),
                    etName.text?.toString(),
                    etMiddleName.text?.toString(),
                    scNoMiddleName.isChecked
            )
        }

        etSurname.doAfterTextChanged { tilSurname.error = null }
        etName.doAfterTextChanged { tilName.error = null }
        etMiddleName.doAfterTextChanged { tilMiddleName.error = null }

        scNoMiddleName.setOnCheckedChangeListener { _, isChecked ->
            tilMiddleName.isEnabled = !isChecked
            tilMiddleName.error = null
            etMiddleName.text = null
        }
    }

    override fun onUserUpdated(user: User?) {
        user ?: return

        val canChangeName = user.user_middle_name?.isNotEmpty() == false
        tilSurname.isEnabled = canChangeName
        tilName.isEnabled = canChangeName
        tilMiddleName.isEnabled = canChangeName

        etSurname.setText(user.user_last_name)
        etName.setText(user.user_name)
        etMiddleName.setText(user.user_middle_name?.takeIf { it != USER_MIDDLE_NAME_EMPTY })

        scNoMiddleName.apply {
            isChecked = user.user_middle_name == USER_MIDDLE_NAME_EMPTY
            isEnabled = canChangeName
        }
    }

    override fun showEmptyLastNameError() {
        tilSurname.error = emptyInputError
    }

    override fun showEmptyNameError() {
        tilName.error = emptyInputError
    }

    override fun showEmptyMiddleNameError() {
        tilMiddleName.error = emptyInputError
    }
}
