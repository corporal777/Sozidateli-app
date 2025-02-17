package com.example.ui.state.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.R
import com.example.app.databinding.FragmentMainInfoBinding
import com.example.data.models.FieldDetails
import com.example.data.models.ImageModel
import com.example.data.models.UserDetail
import com.example.extensions.findItemByShort
import com.example.extensions.formatToDefaultDate
import com.example.extensions.initAsDatePicker
import com.example.extensions.onCheckedChanged
import com.example.extensions.onTextChanged
import com.example.extensions.showSearchRegionDialog
import com.example.extensions.showSearchSettlementDialog
import com.example.extensions.updateItem
import com.example.holders.MainInfoEditItem
import com.example.holders.PlaceholderItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseToolbarFragment
import com.example.ui.base.BaseVBFragment
import com.example.ui.gallery.GalleryBottomSheet
import com.example.ui.state.UserState
import com.example.ui.state.maxNew.MaxStateScreenType
import com.example.ui.views.dialogs.AddPhoneEmailDialog
import com.example.ui.views.dialogs.ContactsType
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.ui.views.suggestFieldView.region.SearchRegionBottomSheet
import com.example.ui.views.suggestFieldView.settlement.SearchSettlementBottomSheet
import com.example.ui.views.toolbar.ToolbarContent
import com.example.ui.views.toolbar.ToolbarIconView
import com.example.util.DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.Utils.formatMobilePhone
import com.example.util.Utils.maxStateScreen
import com.example.util.initDropDownAdapter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Provider


class MainInfoFragment : BaseToolbarFragment<FragmentMainInfoBinding>(), MainInfoContract.View {

    @InjectPresenter
    lateinit var presenter: MainInfoPresenter

    @Inject
    lateinit var presenterProvider: Provider<MainInfoPresenter>

    @ProvidePresenter
    fun providePresenter(): MainInfoPresenter = presenterProvider.get().apply {
        type = MainInfoFragmentArgs.fromBundle(requireArguments()).type
        screen = MainInfoFragmentArgs.fromBundle(requireArguments()).screen
    }


    private lateinit var dialog: AddPhoneEmailDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etBirthday.apply {
                val maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, -16) }.time
                tilBirthday.initAsDatePicker(null, null, maxDate) { y, m, d ->
                    String.format(DATE_STRING_FORMAT_SHORT_MONTH_FULL_YEAR, d, m + 1, y)
                }
                onTextChanged { presenter.onChangeBirthday(it.toString()) }
            }
            scNoBirthday.onCheckedChanged { presenter.onChangeShowBirthday(it) }

            etGender.apply {
                initDropDownAdapter(mutableListOf("Мужской", "Женский"))
                onTextChanged { presenter.onChangeGender(it.toString()) }
            }
            scNoGender.onCheckedChanged { presenter.onChangeShowGender(it) }

            tvRegion.onTextChanged { presenter.onChangeRegion(it.toString()) }
            tvCity.onTextChanged { presenter.onChangeCity(it.toString()) }
            scNoAddress.onCheckedChanged { presenter.onChangeShowAddress(it) }

            tvEditImage.setOnClickListener { showChangeImage() }
            tvEditPhone.setOnClickListener { showChangePhone() }

            btnSave.setOnClickListener { presenter.onSaveData() }
        }
    }


    override fun setPersonalData(user: UserDetail) {
        mBinding.apply {
            etBirthday.setText(user.birthday?.value?.formatToDefaultDate())
            scNoBirthday.isChecked = user.birthday?.isVisible ?: false

            etGender.setText(presenter.setGender(user.gender?.value))
            scNoGender.isChecked = user.gender?.showInProfile ?: false

            tvRegion.apply {
                text = user.address?.region
                setOnClickListener {
                    showSearchRegionDialog {
                        if (it?.name != tvRegion.text) tvCity.text = null
                        text = it?.name
                        tvCity.isEnabled = !tvRegion.text.isNullOrEmpty()
                    }
                }
            }
            tvCity.apply {
                isEnabled = !tvRegion.text.isNullOrEmpty()
                setOnClickListener {
                    showSearchSettlementDialog(tvRegion.text.toString()) { text = it?.name }
                }
            }
            scNoAddress.isChecked = user.address?.showInProfile ?: false
        }
    }

    override fun setUserAvatar(user: UserDetail) {
        mBinding.apply {
            ivAvatar.setImage(user.loadUserImage(), user.avatarIsDefault ?: true)
            tvEditImage.text =
                if (user.loadUserImage().isNullOrEmpty() || user.avatarIsDefault == true)
                    getString(R.string.profile_add_photo)
                else getString(R.string.edit_title)
            tvMobilePhone.text = formatMobilePhone(user.personalPhone?.value)
        }
    }


    override fun goToNext() {
        when (presenter.type) {
            UserState.MAX -> {
                val args = bundleOf("screen" to presenter.screen)
                when (maxStateScreen(presenter.getUserData())) {
                    MaxStateScreenType.BASE ->
                        findNavController().navigate(R.id.maxStatusContactsFragment, args)

                    MaxStateScreenType.INTERESTS ->
                        findNavController().navigate(R.id.maxStatusInterestsFragment, args)

                    MaxStateScreenType.EDUCATION ->
                        findNavController().navigate(R.id.maxStatusEducationFragment, args)

                    MaxStateScreenType.WORK ->
                        findNavController().navigate(R.id.maxStatusWorkFragment, args)

                    MaxStateScreenType.DONE -> showStateSuccessDialog(R.string.you_got_max_state)
                }
            }

            UserState.BASE -> baseActionsWithSuccess()
        }
    }

    private fun baseActionsWithSuccess() {
        if (presenter.getEmail()?.value != null && presenter.getEmail()?.isConfirmed != null) {
            showStateSuccessDialog(R.string.you_got_base_state)
        } else {
            dialog = AddPhoneEmailDialog(requireContext(), ContactsType.EMAIL)
                .setSelectEmailCallback {
                    presenter.checkEmailIsUnique(true, it)
                    dialog.hideDialog()
                }.setNegativeClickCallback { baseActions() }
        }
    }

    private fun baseActions() {
        when (presenter.screen) {
            1 -> findNavController().popBackStack(R.id.profile_fragment, false)
            2 -> findNavController().popBackStack(R.id.userStateFragment, false)
            3 -> findNavController().popBackStack()
        }
    }

    private fun showStateSuccessDialog(text: Int) {
        DefaultAlertDialog(
            requireContext(),
            null,
            getString(text),
            withCancel = false
        ).setSelectCallback { baseActions() }
    }

    override fun showEmailNotUnique(email: String) {
        DefaultAlertDialog(
            requireContext(),
            null,
            getString(R.string.confirm_email_text, email),
            getString(R.string.confirm_phone_positive),
            getString(R.string.event_register_no_form_negative),
            true
        ).setSelectCallback { presenter.checkEmailIsUnique(false, email) }
    }

    override fun showEmailConfirm(email: String) {
        findNavController().navigate(R.id.emailCodeConfirmFragment, bundleOf("email" to email))
        setFragmentResultListener("confirm") { _, bundle ->
            val emailConfirm = bundle.getString("email")
            if (!emailConfirm.isNullOrEmpty()) baseActions()
            clearFragmentResultListener("confirm")
        }
    }

    override fun showChangePhone() = findNavController().navigate(R.id.changePhoneFragment)

    override fun showChangeImage() = GalleryBottomSheet()
        .setPhotoUpdated { presenter.onUpdateImage() }
        .show(childFragmentManager)

    override fun enableBtnSave(isEnable: Boolean) = mBinding.btnSave.let { it.isEnabled = isEnable }

    override fun showCustomLoading() = mBinding.btnSave.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnSave.showProgressLoading(false)


    override fun binding() = FragmentMainInfoBinding::class.java
    override fun layout(): Int = R.layout.fragment_main_info
    override val title: CharSequence by lazy { getString(R.string.user_profile_increase_base_state) }
    override fun animationType(): AnimType {
        return if (isPreviousDestination(R.id.userStateFragment)) AnimType.FADE else AnimType.NONE
    }
    override fun actionIconContainer(view: ViewGroup) {
        view.addView(createIconView(R.drawable.ic_close_new, true) { navigateUp() })
    }
    override fun scrollingView(): View = mBinding.scrollViewMainInfo
}
