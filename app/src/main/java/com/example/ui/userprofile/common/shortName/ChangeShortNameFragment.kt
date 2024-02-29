package com.example.ui.userprofile.common.shortName

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.example.R
import com.example.data.models.UserDetail
import com.example.databinding.FragmentChangeShortNameBinding
import com.example.ui.base.BaseFragment
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.ui.views.CustomSpannableString
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class ChangeShortNameFragment : BaseFragment<FragmentChangeShortNameBinding>(),
    ChangeShortNameContract.View {

    @InjectPresenter
    lateinit var presenter: ChangeShortNamePresenter

    @Inject
    lateinit var presenterProvider: Provider<ChangeShortNamePresenter>

    @ProvidePresenter
    fun providePresenter(): ChangeShortNamePresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etShortName.initInput{
                presenter.onChangeShortName(it.toString())
            }
            btnSave.setOnClickListener {
                hideKeyboard(it)
                presenter.onSaveShortName()
            }
            btnClose.setOnClickListener {
                navigateUp()
            }
        }
    }

    override fun setUserShortName(name: String?, id: String) {
        mBinding.apply {
            val text = getString(R.string.your_short_name_link_in_sozidateli)
            val userShortName =
                if (name.isNullOrEmpty()) "$text sozidateli.ru/id$id"
                else if (name == id) "$text sozidateli.ru/id$id"
                else "$text sozidateli.ru/$name"

            tvShortName.text = CustomSpannableString(userShortName).apply {
                setColorSpanWithLength(R.color.main_brown_color_new, text.length, requireContext())
            }
            if (!name.isNullOrEmpty()) etShortName.setText(name)
        }
    }


    override fun setUserShortNameUnique(isUnique: Boolean, name: String?) {
        mBinding.tvShortNameInvalidError.apply {
            isVisible = !name.isNullOrEmpty()
            if (isUnique) {
                setTextColor(resources.getColor(R.color.profile_status_complete))
                text = getString(R.string.short_name_valid_message)
            } else {
                setTextColor(resources.getColor(R.color.red_new))
                text = getString(R.string.short_name_invalid_error)
            }
        }
    }

    override fun enableBtnSave(enabled: Boolean) {
        mBinding.btnSave.isEnabled = enabled
    }
    override fun showCustomLoading() = mBinding.btnSave.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnSave.showProgressLoading(false)

    override fun animationType(): AnimType = AnimType.FADE
    override fun layout(): Int = R.layout.fragment_change_short_name
}