package com.example.ui.event.registration

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.R
import com.example.data.models.EventFile
import com.example.data.models.EventRegisterData
import com.example.data.models.EventRegisterFieldData
import com.example.data.models.EventRegistration
import com.example.data.models.EventRegistration.Companion.MODERATION_AUTO_APPROVE
import com.example.data.models.EventRegistration.Companion.MODERATION_AUTO_DISMISS
import com.example.data.models.EventRegistration.Companion.MODERATION_MANUAL
import com.example.databinding.FragmentRequestBinding
import com.example.extensions.findGroupBy
import com.example.extensions.forEachGroups
import com.example.extensions.setRequired
import com.example.holders.ActionButtonItem
import com.example.holders.ActionButtonItem.Companion.ACTION_EVENT_REQUEST
import com.example.holders.PlaceholderItem
import com.example.holders.registerEvent.*
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.data.models.ProfileFieldsFormResult
import com.example.extensions.updateItems
import com.example.ui.event.registration.items.RegisterEventImageHeaderItem
import com.example.ui.event.registration.items.RegisterEventProfileItemsGroup
import com.example.ui.views.BottomDialog
import com.example.ui.views.LinearLayoutManagerAccurateOffset
import com.example.ui.views.dialogs.EventRegistrationRequestDialog
import com.example.util.showCustomTabsBrowser
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.example.extensions.onBackPressedCallback
import com.example.extensions.onScrolled
import com.example.extensions.statusBarColorValue
import com.example.ui.views.dialogs.EventRegistrationSuccessBottomDialog
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class EventRegistrationFragment : BaseFragment<FragmentRequestBinding>(),
    EventRegistrationContract.View {

    @InjectPresenter
    lateinit var presenter: EventRegistrationPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventRegistrationPresenter>

    @ProvidePresenter
    fun providePresenter(): EventRegistrationPresenter = presenterProvider.get().apply {
        eventId = EventRegistrationFragmentArgs.fromBundle(requireArguments()).eventId
    }

    private val fieldsDataSection = Section().apply {
        updateItems(
            PlaceholderItem(PlaceholderItem.Type.REGISTER_HEADER),
            List(2) { PlaceholderItem(PlaceholderItem.Type.REGISTER_FIELD) }
        )
    }
    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(fieldsDataSection)
        }
    }
    private val saveButtonItem by lazy {
        ActionButtonItem(-200L, ACTION_EVENT_REQUEST) { presenter.onRegisterClick() }
    }

    private val personalDataFileClickListener: OnPersonalDataFileClickListener =
        { presenter.onPersonalDataFileClick(it) }
    private val onFieldDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit =
        { presenter.onDataChange(it) }

    private var bottomDialog: Dialog? = null
    private val customLayoutManager by lazy { LinearLayoutManagerAccurateOffset(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressedCallback(true) {
            hideKeyboard()
            presenter.onBackClick()
        }
        mBinding.apply {
            recyclerView.apply {
                layoutManager = customLayoutManager
                adapter = groupAdapter
                onScrolled { _, _ ->
                    presenter.changeAppBarBackground(this.computeVerticalScrollOffset())
                }
            }
            ivBack.setOnClickListener {
                hideKeyboard()
                presenter.onBackClick()
            }
        }
    }


    override fun setFormFields(
        event: EventRegistration,
        fieldsData: List<EventRegisterFieldData<*>>,
        withConfirm: Boolean
    ) {
        fieldsDataSection.apply {
            setHeader(
                RegisterEventImageHeaderItem(
                    -100L,
                    event.image,
                    event.backgroundColor,
                    event.registrationHeadline,
                    event.registrationSubtitle,
                    event.conferenceStart,
                    event.conferenceFinish
                )
            )
            if (withConfirm) setFooter(saveButtonItem)

            update(fieldsData.mapNotNull {
                when (it) {
                    is EventRegisterFieldData.Prefilled -> {
                        if (it.value == null) null
                        else RegisterEventProfileItemsGroup(it.value!!) { showEditProfile() }
                    }

                    is EventRegisterFieldData.String ->
                        RegisterEventStringItem(
                            it,
                            onDataChange = onFieldDataChange
                        ).createFieldItemFrom(it)

                    is EventRegisterFieldData.Date ->
                        RegisterEventDateItem(
                            it,
                            onDataChange = onFieldDataChange
                        ).createFieldItemFrom(it)

                    is EventRegisterFieldData.SelectBox ->
                        EventRegistrationSelectBoxItem(
                            it,
                            onDataChange = onFieldDataChange
                        ).createFieldItemFrom(it)

                    is EventRegisterFieldData.RadioBox ->
                        RegisterEventRadioBoxItem(
                            it,
                            onDataChange = onFieldDataChange
                        ).createFieldItemFrom(it)

                    is EventRegisterFieldData.Checkbox ->
                        RegisterEventCheckboxItem(
                            it,
                            onDataChange = onFieldDataChange
                        ).createFieldItemFrom(it)

                    is EventRegisterFieldData.Boolean ->
                        RegisterEventBooleanItem(
                            it,
                            onDataChange = onFieldDataChange
                        ).createFieldItemFrom(it, withTitle = false)

                    is EventRegisterFieldData.Passport ->
                        RegisterEventPassportItem(
                            it,
                            onDataChange = onFieldDataChange
                        ).createFieldItemFrom(it)

                    is EventRegisterFieldData.File ->
                        EventRegistrationFileGroup(
                            requireContext(),
                            it,
                            onFieldDataChange
                        ) { presenter.onAddFileClick(it) }.createFieldItemFrom(it)
                }
            })
        }
    }

    override fun updateProfileFields(profileForm: ProfileFieldsFormResult) {
        fieldsDataSection.findGroupBy<RegisterEventProfileItemsGroup> { true }
            ?.updateProfileFields(profileForm)
    }

    override fun updateFileField(fieldId: String) {
        groupAdapter.forEachGroups {
            val fileGroup = if (it is NestedGroup) findEventRegistrationFileGroup(it, fieldId)
            else null
            if (fileGroup != null) {
                fileGroup.checkFile()
                return@forEachGroups
            }
        }
    }

    override fun showSaveFormResultDraftDialog() {
        EventRegistrationRequestDialog(
            requireContext(),
            "Анкета",
            "Вы можете сохранить черновик анкеты и вернуться к ее заполнению позже.",
            "Сохранить",
            "Закрыть",
            true
        ).setSelectCallback { state ->
            if (state) presenter.saveEventFormResultDraft()
            else findNavController().navigateUp()
        }
    }

    override fun showSavedFormResultDraftDialog(result: EventRegisterData) {
        EventRegistrationRequestDialog(
            requireContext(),
            "Анкета",
            "У вас есть черновик анкеты. Хотите продолжить заполнение?",
            "Продолжить",
            "Начать заново",
            false
        ).setSelectCallback { state ->
            if (state) presenter.initFormResultData(result.event, result.getSortedDraftFields())
            else presenter.initFormResultData(result.event, result.getSortedFields())
        }
    }

    override fun showSuccessRegister(moderation: String?) {
        EventRegistrationSuccessBottomDialog(requireContext())
            .setSelectCallback {
                presenter.onSuccessGoToList()
            }.show()
    }

    override fun openUrl(url: String) = showCustomTabsBrowser(requireContext(), url)

    override fun openFileSelector(field: EventRegisterFieldData<EventFile?>) {
        AlertDialog.Builder(requireContext())
            .setTitle("Открыть файлы или галерею?")
            .setPositiveButton(R.string.file_alert_gallery) { _, _ -> presenter.onTakeFile(field) }
            .setNegativeButton(R.string.photo_alert_gallery) { _, _ -> presenter.onTakeImage(field) }
            .show()
    }

    override fun showWrongFileExtensions(availableExtensions: List<String>) {
        val message = getString(R.string.event_register_file_extension_wrong)
            .format(availableExtensions.joinToString { it.toLowerCase(Locale.getDefault()) })
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun Group.createFieldItemFrom(
        fieldData: EventRegisterFieldData<*>,
        customTitle: String? = null,
        withTitle: Boolean = true,
        withFile: Boolean = true
    ): Group {
        val field = fieldData.field
        return let {
            val title = if (withTitle) customTitle ?: field.name else null
            it.withEventRegistrationTitle(title?.setRequired(field.required)?.toString())
        }
            .let {
                val file = if (withFile) field.rightFile else null
                it.withEventRegistrationPersonalDataFile(
                    file?.file, field.rightFileDescription
                        ?: file?.filename, personalDataFileClickListener
                )
            }
    }

    private fun findEventRegistrationFileGroup(
        parent: NestedGroup,
        fieldId: String
    ): EventRegistrationFileGroup? {
        if (parent is EventRegistrationFileGroup) return parent
        for (i in 0 until parent.groupCount) {
            val group = parent.getGroup(i)
            if (group is EventRegistrationFileGroup && group.fieldData.field.id == fieldId) return group
            else if (group is NestedGroup) {
                val child = findEventRegistrationFileGroup(group, fieldId)
                if (child != null) return child
            }
        }

        return null
    }

    override fun enableActionButton(enable: Boolean) {
        saveButtonItem.apply {
            if (isEnabled != enable) {
                isEnabled = enable
                notifyChanged()
            }
        }
    }


    override fun showEventLists() {
        if (!findNavController().popBackStack(R.id.recommendations_fragment, false)) {
            findNavController().navigate(R.id.recommendations_fragment, null,
                navOptions { popUpTo(R.id.request_fragment) { inclusive = true } }
            )
        }
    }

    override fun showEditProfile() {
        findNavController().navigate(R.id.user_profile_fragment)
    }

    override fun showCustomLoading() = saveButtonItem.run { showLoading(true) }
    override fun hideCustomLoading() = saveButtonItem.run { showLoading(false) }

    override fun updateAppBarBackgroundColorValue(offset: Int) {
        mBinding.apply {
            if (offset <= 0) tbBackground.alpha = 0f
            else tbBackground.apply { alpha = abs(offset / (900).toFloat()) }

            if (offset >= 1070) appBar.changeAppBarElevation(abs(offset / 120f))
            else appBar.changeAppBarElevation(0f)

            statusBarColorValue = if (offset >= 500) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
        }
    }

    override fun layout() = R.layout.fragment_request

    companion object {
        private const val REQUEST_CODE_FILE = 100
    }
}
