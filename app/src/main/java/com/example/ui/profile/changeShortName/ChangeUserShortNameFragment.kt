package com.example.ui.profile.changeShortName

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.BottomSheetChangeShortNameBinding
import com.example.ui.base.BaseBottomSheetFragment
import com.example.ui.profile.ProfileFragmentArgs
import com.example.ui.profile.ProfilePresenter
import com.example.ui.userprofile.read.settings.ChangePasswordBottomSheetFragment
import kotlinx.android.synthetic.main.calendar_view.view.*
import onTextChanged
import javax.inject.Inject
import javax.inject.Provider

class ChangeUserShortNameFragment(val user: UserDetail) :
    BaseBottomSheetFragment<BottomSheetChangeShortNameBinding>() {

    private var onSaveUserShortName: (name: String) -> Unit = { }
    private var checkUserShortNameUnique: (name: String) -> Unit = {}

    private var userShortName = StringBuilder()
    init {
        userShortName = getUserShortName(user.shortName)
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
                            tvShortName.text = userShortName
                            tvShortNameInvalidError.isVisible = false
                        }else {
                            checkUserShortNameUnique.invoke(text)
                            tvShortName.text = getUserShortName(text)
                            enableActionButton(true)
                        }

                    }
                }
            }
            btnSave.setOnClickListener {
                hideKeyboard(mBinding.root)
                onSaveUserShortName(etShortName.text.toString())
            }
        }
    }

    private fun enableActionButton(enable : Boolean){
        mBinding.btnSave.isEnabled = enable
    }

    private fun getUserShortName(shortName: String?) : java.lang.StringBuilder{
        val link =  if (user.id.toString() == shortName){
            "sozidateli.ru/id"
        }else {
            "sozidateli.ru/"
        }
        return StringBuilder(link).append(shortName)
    }

    fun setShortNameUnique(isUnique: Boolean) {
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

    fun setOnSaveUserShortNameCallback(block: (name: String) -> Unit): ChangeUserShortNameFragment {
        onSaveUserShortName = block
        return this
    }

    fun setOnCheckUserShortNameUniqueCallback(block: (name: String) -> Unit): ChangeUserShortNameFragment {
        checkUserShortNameUnique = block
        return this
    }

    override fun layout(): Int = R.layout.bottom_sheet_change_short_name
}