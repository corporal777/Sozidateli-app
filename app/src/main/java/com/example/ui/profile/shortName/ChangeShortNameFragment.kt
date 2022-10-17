package com.example.ui.profile.shortName

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.BottomSheetChangeShortNameBinding
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.userprofile.read.settings.change_password.ChangePasswordFragmentArgs
import com.example.ui.userprofile.read.settings.change_password.ChangePasswordPresenter
import com.example.ui.views.dialogs_new.CalendarBottomSheet
import onTextChanged
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class ChangeShortNameFragment(val user: UserDetail) :
    BaseBottomSheetFragment<BottomSheetChangeShortNameBinding>(), ChangeShortNameContract.View {

    private var userShortName = ""
    private var setUserShortName : (user : UserDetail) -> Unit = {}

    @InjectPresenter
    lateinit var presenter: ChangeShortNamePresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangeShortNamePresenter>

    @ProvidePresenter
    fun providePresenter(): ChangeShortNamePresenter = presenterProvider.get().apply {
    }

    init {
        userShortName = if (user.id.toString() == user.shortName){
            "sozidateli.ru/id" + user.shortName
        }else {
            "sozidateli.ru/" + user.shortName
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        focusOnInput(mBinding.etShortName, true)
        enableActionButton(false)
        mBinding.apply {
            tvShortName.text = userShortName
            if (user.id.toString() != user.shortName){
                etShortName.setText(user.shortName)
            }
            etShortName.apply {
                onTextChanged {
                    it?.toString()?.let { text ->
                        if (text.isNullOrEmpty()){
                            enableActionButton(false)
                            tvShortNameInvalidError.isVisible = false
                        }else {
                            presenter.checkUserShortNameUnique(text)
                            enableActionButton(true)
                        }
                    }
                }
            }
            btnSave.setOnClickListener {
                presenter.updateUserShortName(user.id, etShortName.text.toString())
            }
        }
    }

    private fun enableActionButton(enable : Boolean){
        mBinding.btnSave.isEnabled = enable
    }


    override fun setUserShortNameUnique(isUnique: Boolean) {
        mBinding.tvShortNameInvalidError.apply {
            isVisible = true
            if (isUnique){
                setTextColor(resources.getColor(R.color.profile_status_complete))
                text = getString(R.string.short_name_valid_message)
            }else {
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


    override fun layout(): Int = R.layout.bottom_sheet_change_short_name
}