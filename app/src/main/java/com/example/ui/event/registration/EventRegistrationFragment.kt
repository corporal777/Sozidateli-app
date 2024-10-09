package com.example.ui.event.registration

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.R
import com.example.data.models.EventFile
import com.example.data.models.EventRegisterData
import com.example.data.models.EventRegisterFieldData
import com.example.data.models.EventRegistration
import com.example.app.databinding.FragmentRequestBinding
import com.example.extensions.findGroupBy
import com.example.extensions.findItemBy
import com.example.extensions.onBackPressedCallback
import com.example.extensions.updateItems
import com.example.holders.ActionButtonItem
import com.example.holders.PlaceholderItem
import com.example.holders.registerEvent.BaseRegisterItem
import com.example.holders.registerEvent.RegisterEventBooleanItem
import com.example.holders.registerEvent.RegisterEventCheckboxItem
import com.example.holders.registerEvent.RegisterEventDateItem
import com.example.holders.registerEvent.RegisterEventDropdownItem
import com.example.holders.registerEvent.RegisterEventFileGroup
import com.example.holders.registerEvent.RegisterEventPassportItem
import com.example.holders.registerEvent.RegisterEventPhoneItem
import com.example.holders.registerEvent.RegisterEventRadioBoxItem
import com.example.holders.registerEvent.RegisterEventStringItem
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.registration.items.PrefilledFieldClickType
import com.example.holders.registerEvent.RegisterEventHeaderItem
import com.example.ui.event.registration.items.RegisterEventProfileItemsGroup
import com.example.holders.registerEvent.RegisterEventTitleItem
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.ui.views.dialogs.EventRegistrationSuccessBottomDialog
import com.example.ui.views.toolbar.ToolbarContent
import com.example.util.setTint
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Provider

class EventRegistrationFragment : BaseFragment<FragmentRequestBinding>(),
    EventRegistrationContract.View, ToolbarFragment {

    @InjectPresenter
    lateinit var presenter: EventRegistrationPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventRegistrationPresenter>

    @ProvidePresenter
    fun providePresenter(): EventRegistrationPresenter = presenterProvider.get().apply {
        eventId = EventRegistrationFragmentArgs.fromBundle(requireArguments()).eventId
    }

    private val fieldsSection = Section().apply {
        updateItems(
            PlaceholderItem(PlaceholderItem.Type.REGISTER_HEADER),
            List(2) { PlaceholderItem(PlaceholderItem.Type.REGISTER_FIELD) }
        )
    }
    private val groupAdapter by lazy { GroupAdapter<GroupieViewHolder>().apply { add(fieldsSection) } }
    private val saveButtonItem by lazy { ActionButtonItem(-200L) { presenter.onRegisterClick() } }


    private val onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit =
        { presenter.onDataChange(it) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressedCallback(true) {
            presenter.onNavigateUpClick()
        }
        mBinding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = groupAdapter
            }
        }
    }


    override fun setFormFields(event: EventRegistration, fieldsData: List<EventRegisterFieldData<*>>) {
        fieldsSection.apply {
            setHeader(RegisterEventHeaderItem(event.id.toLong(), event))
            setFooter(saveButtonItem)

            update(fieldsData.map {
                when (it) {
                    is EventRegisterFieldData.Prefilled ->
                        RegisterEventProfileItemsGroup(it) { showEditProfile(it) }

                    is EventRegisterFieldData.Title -> RegisterEventTitleItem(it.field.name)

                    is EventRegisterFieldData.String -> RegisterEventStringItem(it, onDataChange)

                    is EventRegisterFieldData.Phone -> RegisterEventPhoneItem(it, onDataChange)

                    is EventRegisterFieldData.Date -> RegisterEventDateItem(it, onDataChange)

                    is EventRegisterFieldData.SelectBox ->
                        RegisterEventDropdownItem(it, onDataChange)

                    is EventRegisterFieldData.RadioBox ->
                        RegisterEventRadioBoxItem(it, onDataChange)

                    is EventRegisterFieldData.Checkbox ->
                        RegisterEventCheckboxItem(it, onDataChange)

                    is EventRegisterFieldData.Boolean -> RegisterEventBooleanItem(it, onDataChange)

                    is EventRegisterFieldData.Passport ->
                        RegisterEventPassportItem(it, onDataChange)

                    is EventRegisterFieldData.File ->
                        RegisterEventFileGroup(it, onDataChange) { presenter.onAddFileClick(it) }
                }
            })
        }
    }

    override fun showErrors(invalidFields: MutableSet<EventRegisterFieldData<*>>) {
        invalidFields.filter { x -> x.field.required }.forEach {
            if (it is EventRegisterFieldData.Prefilled) {
                val item = fieldsSection.findGroupBy<RegisterEventProfileItemsGroup> { true }
                item?.showError()
            } else if (it is EventRegisterFieldData.File) {
                val item = fieldsSection.findGroupBy<RegisterEventFileGroup> { x -> x.getId() == it.field.id }
                item?.showError(true)
            } else {
                val item = fieldsSection.findItemBy<BaseRegisterItem<*>> { x -> x.id == it.field.id.toLong() }
                item?.showError(true)
            }
        }
    }

    override fun updateProfileFields(profileForm: EventRegisterFieldData.Prefilled) {
        fieldsSection.findGroupBy<RegisterEventProfileItemsGroup> { true }?.updateData()
    }

    override fun updateFileField(fieldId: String) {
        fieldsSection.findGroupBy<RegisterEventFileGroup> { x -> x.getId() == fieldId }?.checkFile()
    }


    override fun enableActionButton(enable: Boolean) {
        saveButtonItem.apply {
            if (isEnabled == enable) return
            isEnabled = enable
            notifyChanged()
        }
    }

    override fun showSaveFormResultDraftDialog() {
        DefaultAlertDialog(
            requireContext(),
            "Анкета",
            "Вы можете сохранить черновик анкеты и вернуться к ее заполнению позже.",
            positiveText = "Сохранить",
            negativeText = "Закрыть"
        ).setSelectCallback { presenter.saveEventFormResultDraft() }
            .setCancelCallback { findNavController().navigateUp() }
    }

    override fun showSavedFormResultDraftDialog(res: EventRegisterData) {
        DefaultAlertDialog(
            requireContext(),
            "Анкета",
            "У вас есть черновик анкеты. Хотите продолжить заполнение?",
            positiveText = "Продолжить",
            negativeText = "Начать заново"
        )
            .setSelectCallback { presenter.initFormResultData(res.event, res.getDraftFields()) }
            .setCancelCallback { presenter.initFormResultData(res.event, res.getSortedFields()) }
    }

    override fun showEventRegistrationSuccessDialog() {
        EventRegistrationSuccessBottomDialog(requireContext())
            .setSelectCallback { presenter.onSuccessGoToList() }
            .show()
    }

    override fun openFileSelector(field: EventRegisterFieldData<EventFile?>) {
        DefaultAlertDialog(
            requireContext(),
            null,
            "Открыть файлы или галерею?",
            getString(R.string.file_alert_gallery),
            getString(R.string.photo_alert_gallery)
        )
            .setSelectCallback { presenter.onTakeFile(field) }
            .setCancelCallback { presenter.onTakeImage(field) }
    }

    override fun showWrongFileExtensions(availableExtensions: List<String>) {
        val message = getString(R.string.event_register_file_extension_wrong)
            .format(availableExtensions.joinToString { it.toLowerCase(Locale.getDefault()) })
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun showEventLists() {
        if (!findNavController().popBackStack(R.id.recommendations_fragment, false)) {
            findNavController().navigate(R.id.recommendations_fragment, null,
                navOptions { popUpTo(R.id.request_fragment) { inclusive = true } }
            )
        }
    }

    override fun showEditProfile(type: PrefilledFieldClickType) {
        when (type) {
            PrefilledFieldClickType.PROFILE ->
                findNavController().navigate(R.id.user_profile_fragment)
            PrefilledFieldClickType.NAME ->
                findNavController().navigate(R.id.user_profile_settings_fragment)
            PrefilledFieldClickType.MAIN ->
                findNavController().navigate(R.id.user_edit_fragment)
            PrefilledFieldClickType.CONTACTS ->
                findNavController().navigate(R.id.editContactsFragment)
            PrefilledFieldClickType.EDUCATION ->
                findNavController().navigate(R.id.editEducationFragment)
            PrefilledFieldClickType.WORK ->
                findNavController().navigate(R.id.editWorksFragment)
        }
    }

    override fun showCustomLoading() = saveButtonItem.run { notifyChanged(1) }
    override fun hideCustomLoading() = saveButtonItem.run { notifyChanged(0) }

    override fun navigateUp() = presenter.onNavigateUpClick()
    override fun navigateUpClick() = super.navigateUp()


    override fun layout() = R.layout.fragment_request
    override val title: CharSequence by lazy { getString(R.string.event_registration_title) }
    override fun actionIconContainer(view: ViewGroup) {}
    override fun scrollValue(scroll: Int) {}
    override fun setupToolbarContent(toolbarContent: ToolbarContent) {
        toolbarContent.getBackButton().setTint(R.color.main_brown_color_new)
    }
}
