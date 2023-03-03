package com.example.ui.event.registration

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.*
import com.example.data.models.EventRegistration.Companion.MODERATION_AUTO_APPROVE
import com.example.data.models.EventRegistration.Companion.MODERATION_AUTO_DISMISS
import com.example.data.models.EventRegistration.Companion.MODERATION_MANUAL
import com.example.databinding.FragmentRequestBinding
import com.example.extensions.findGroupBy
import com.example.extensions.forEachGroups
import com.example.extensions.setRequired
import com.example.holders.ActionButtonItem
import com.example.holders.ActionButtonItem.Companion.ACTION_EVENT_REQUEST
import com.example.holders.registerEvent.*
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.redesign.AboutEventFragmentNewArgs
import com.example.ui.event.registration.items.*
import com.example.ui.views.BottomDialog
import com.example.ui.views.LinearLayoutManagerAccurateOffset
import com.example.ui.views.dialogs_new.EventAgreementRegisterDialog
import com.example.ui.views.dialogs_new.EventRegistrationRequestDialog
import com.example.util.showCustomTabsBrowser
import com.xwray.groupie.*
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import onBackPressedCallback
import onScrolled
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

class EventRegistrationFragment : BaseFragmentNew<FragmentRequestBinding>(),
    EventRegistrationContract.View {

    private lateinit var mListState: Parcelable

    @InjectPresenter
    lateinit var presenter: EventRegistrationPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventRegistrationPresenter>

    @ProvidePresenter
    fun providePresenter(): EventRegistrationPresenter = presenterProvider.get().apply {
        eventId = EventRegistrationFragmentArgs.fromBundle(requireArguments()).eventId
    }

    private val headerSection = Section()
    private val fieldsDataSection = Section()
    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(headerSection)
            add(fieldsDataSection)
        }
    }
    private val saveButtonItem by lazy {
        ActionButtonItem(-200L, ACTION_EVENT_REQUEST) {
            presenter.onRegisterClick()
        }
    }

    private val personalDataFileClickListener: OnPersonalDataFileClickListener =
        { presenter.onPersonalDataFileClick(it) }
    private val onFieldDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit =
        { presenter.onDataChange(it) }

    private var bottomDialog: Dialog? = null
    private val customLayoutManager by lazy { LinearLayoutManagerAccurateOffset(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressedCallback(true){
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


    override fun setFormHeader(event: EventRegistration) {
        headerSection.setHeader(
            RegisterEventImageHeaderItem(
                -100L,
                event.image,
                event.backgroundColor,
                event.registrationHeadline,
                event.registrationSubtitle,
                event.conferenceStart,
            )
        )
    }


    override fun updateProfileFields(profileForm: ProfileFieldsFormResult) {
        fieldsDataSection.findGroupBy<RegisterEventProfileItemsGroup> { true }
            ?.updateProfileFields(profileForm)
    }

    override fun setFormFields(
        event: EventRegistration,
        fieldsData: List<EventRegisterFieldData<*>>,
        withConfirm: Boolean
    ) {

        fieldsDataSection.apply {
            if (withConfirm) {
                setFooter(saveButtonItem)
            }

            update(fieldsData.map {
                when (it) {
                    is EventRegisterFieldData.Prefilled -> {
                        if (it.value != null) RegisterEventProfileItemsGroup(it.value!!) { showEditProfile() }
                        else null
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
                    else -> null
                }
            })
        }
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

    override fun showEventRegisterConfirmation() {
        bottomDialog?.dismiss()
        BottomDialog(requireContext()).apply {
            setTitle(getString(R.string.event_register_no_form_confirmation_title))
            setMessage(getString(R.string.event_register_no_form_confirmation_message))
            positiveButton {
                text = getString(R.string.event_register_no_form_positive)
                clickListener = {
                    presenter.onRegisterClick()
                    true
                }
            }

            negativeButton {
                text = getString(R.string.event_register_no_form_negative)
                clickListener = {
                    presenter.onRegisterCancelClick()
                    true
                }
            }
            setCancelable(false)
            bottomDialog = this
        }.show()
    }

    override fun showAgreementRegisterDialog(url: String) {
        bottomDialog?.dismiss()

        EventAgreementRegisterDialog(requireContext(), url).setSelectCallback {
            presenter.onRegisterClick()
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

    override fun showLoadSavedFormResultDraftDialog(result: EventRegisterData) {
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
        val canGoToEvent: Boolean
        val maybeApproved: Boolean
        when (moderation) {
            MODERATION_MANUAL -> {
                canGoToEvent = false
                maybeApproved = true
            }
            MODERATION_AUTO_APPROVE -> {
                canGoToEvent = true
                maybeApproved = true
            }
            MODERATION_AUTO_DISMISS -> {
                canGoToEvent = false
                maybeApproved = false
            }
            else -> {
                canGoToEvent = false
                maybeApproved = true
            }
        }
        bottomDialog?.dismiss()
        BottomDialog(requireContext()).apply {
            setTitle(getString(if (canGoToEvent) R.string.event_register_sent_title else R.string.event_register_sent_moderate_title))
            if (maybeApproved)
                setMessage(getString(if (canGoToEvent) R.string.event_register_sent_message else R.string.event_register_sent_moderate_message))
            positiveButton {
                text =
                    getString(if (canGoToEvent) R.string.event_register_sent_button else R.string.event_register_sent_moderate_button)
                clickListener = {
                    if (canGoToEvent) presenter.onSuccessGoToEvent() else presenter.onSuccessGoToList()
                    true
                }
            }

            setOnCancelListener { presenter.onSuccessCancel() }
            bottomDialog = this
        }.show()
    }

    override fun openUrl(url: String) = showCustomTabsBrowser(requireContext(), url)

    override fun openFileSelector() {
        startActivityForResult(
            Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE), REQUEST_CODE_FILE
        )
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

    override fun showWrongFileExtensions(availableExtensions: List<String>) {
        val message = getString(R.string.event_register_file_extension_wrong)
            .format(availableExtensions.joinToString { it.toLowerCase(Locale.getDefault()) })
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        super.onActivityResult(requestCode, resultCode, result)
        if (requestCode == REQUEST_CODE_FILE) {
            val url = if (resultCode == RESULT_OK) {
                result?.data
            } else null

            if (url != null) presenter.onFileSelected(url)
            else presenter.onFileSelectionCancel()
        }
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
            findNavController().navigate(R.id.recommendations_fragment, null, navOptions {
                popUpTo(R.id.request_fragment) { inclusive = true }
            })
        }
    }

    override fun showEvent(eventId: String) {
        findNavController().navigate(
            R.id.about_event_fragment_new,
            AboutEventFragmentNewArgs.Builder(eventId).build().toBundle(),
            navOptions {
                popUpTo(R.id.request_fragment) { inclusive = true }
            })
    }

    override fun showEditProfile() {
        findNavController().navigate(EventRegistrationFragmentDirections.requestToUserProfile())
    }


    override fun updateAppBarBackgroundColorValue(offset: Int) {
        Log.e("OFFSET", offset.toString())
        mBinding.apply {
            if (offset <= 0) {
                tbBackground.alpha = 0f
            } else {
                tbBackground.apply {
                    alpha = abs(offset / (900).toFloat())
                }
            }
            if (offset >= 1070) {
                appBar.changeAppBarElevation(abs(offset / 120f))
            } else {
                appBar.changeAppBarElevation(0f)
            }

            if (offset >= 500) {
                setBlackIcons()
            } else {
                setWhiteIcons()
            }

        }

    }

    private fun setBlackIcons() {
        mBinding.apply {
            ivBack.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.vk_black)
            requireActivity().window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun setWhiteIcons() {
        mBinding.apply {
            requireActivity().window.decorView.systemUiVisibility = 0
            ivBack.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
    }


    override fun layout() = R.layout.fragment_request

    companion object {
        private const val REQUEST_CODE_FILE = 100
    }
}
