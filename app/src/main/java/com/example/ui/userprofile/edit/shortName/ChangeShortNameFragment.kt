package com.example.ui.userprofile.edit.shortName

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.BottomSheetChangeShortNameBinding
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class ChangeShortNameFragment(
    val id: Int?,
    val shortName: String?
) : BaseBottomSheetFragment<BottomSheetChangeShortNameBinding>(), ChangeShortNameContract.View {

    private var setUserShortName: (user: UserDetail) -> Unit = {}

    @InjectPresenter(tag = CHANGE_SHORT_NAME_FRAGMENT_TAG)
    lateinit var presenter: ChangeShortNamePresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangeShortNamePresenter>

    @ProvidePresenter(tag = CHANGE_SHORT_NAME_FRAGMENT_TAG)
    fun providePresenter(): ChangeShortNamePresenter = presenterProvider.get().apply {
        userId = id.toString()
        userShortName = shortName ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enableActionButton(false)
        mBinding.apply {
            etShortName.onTextChanged {
                it?.toString()?.let { text ->
                    if (text.isNullOrEmpty()) {
                        enableActionButton(false)
                        tvShortNameInvalidError.isVisible = false
                    } else {
                        presenter.checkUserShortNameUnique(text)
                    }
                }
            }
            btnSave.setOnClickListener {
                presenter.updateUserShortName(etShortName.text.toString())
            }
            btnClose.setOnClickListener {
                dismiss()
            }
            focusOnInput(etShortName, true)
        }
    }

    private fun enableActionButton(enable: Boolean) {
        mBinding.btnSave.isEnabled = enable
    }

    override fun setUserShortName(name: String) {
        mBinding.apply {
            tvShortName.text = name
            if (id.toString() != shortName) etShortName.setText(shortName)
        }
    }


    override fun setUserShortNameUnique(isUnique: Boolean) {
        mBinding.tvShortNameInvalidError.apply {
            isVisible = true
            if (isUnique) {
                enableActionButton(true)
                setTextColor(resources.getColor(R.color.profile_status_complete))
                text = getString(R.string.short_name_valid_message)
            } else {
                enableActionButton(false)
                setTextColor(resources.getColor(R.color.red_new))
                text = getString(R.string.short_name_invalid_error)
            }
        }
    }

    fun getUpdatedUserShortName(block: (user: UserDetail) -> Unit): ChangeShortNameFragment {
        setUserShortName = block
        return this
    }

    override fun showUserShortNameSuccessUpdated() {
        showToast("Короткое имя изменено")
    }

    override fun updateUserShortNameInProfile(user: UserDetail) {
        setUserShortName.invoke(user)
    }

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, "change_short_name")

    companion object {
        const val CHANGE_SHORT_NAME_FRAGMENT_TAG = "change_short_name_tag"
    }


    override fun layout(): Int = R.layout.bottom_sheet_change_short_name
}