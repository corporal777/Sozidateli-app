package com.example.ui.event.registration

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.EventGroup
import com.example.data.models.EventRegisterField
import com.example.data.models.EventRegisterFieldData
import com.example.data.models.EventRegistration
import com.example.extensions.forEachGroups
import com.example.extensions.formatToInterval
import com.example.extensions.setRequired
import com.example.holders.ActionButtonItem
import com.example.holders.ActionButtonItem.Companion.ACTION_EVENT_REQUEST
import com.example.holders.registerEvent.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.BottomDialog
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_request.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class EventRegistrationFragment : BaseFragment(), EventRegistrationContract.View, ToolbarFragment {

    override val title: CharSequence
        get() = getString(R.string.request_label)

    @InjectPresenter
    lateinit var presenter: EventRegistrationPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventRegistrationPresenter>

    @ProvidePresenter
    fun providePresenter(): EventRegistrationPresenter = presenterProvider.get().apply {
        eventId = EventRegistrationFragmentArgs.fromBundle(arguments!!).eventId
    }

    private val section = Section()
    private val adapter by lazy { GroupAdapter<GroupieViewHolder>().apply { add(section) } }
    private val saveButtonItem by lazy {
        ActionButtonItem(-200L, ACTION_EVENT_REQUEST) {
            presenter.onRegisterClick()
        }
    }

    private val personalDataFileClickListener: OnPersonalDataFileClickListener = { presenter.onPersonalDataFileClick(it) }
    private val onFieldDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit = { presenter.onDataChange(it) }

    private var bottomDialog: BottomDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply { adapter = this@EventRegistrationFragment.adapter }
    }

    override fun setFields(event: EventRegistration,
                           groupField: EventRegisterField?,
                           selectedGroup: String?,
                           groups: List<EventGroup>,
                           fieldsData: List<EventRegisterFieldData<*>>,
                           withConfirm: Boolean) {
        section.apply {
            setHeader(RegisterEventHeaderItem(
                    -100L,
                    event.organization?.name,
                    event.conferenceStart?.formatToInterval(event.conferenceFinish),
                    event.description,
                    event.registrationName,
                    event.registrationSubtitle
            ))

            if (withConfirm) setFooter(saveButtonItem)

            if (groupField != null) {
                add(EventRegistrationGroupsItem(groupField.id.toLong(), groupField.description, groups, selectedGroup) {
                    presenter.onSelectedGroupChange(it)
                }.withEventRegistrationTitle(groupField.name))
            }

            addAll(fieldsData.map {
                when (it) {
                    is EventRegisterFieldData.String ->
                        RegisterEventStringItem(it, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.Date ->
                        RegisterEventDateItem(it, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.SelectBox ->
                        EventRegistrationSelectBoxItem(it, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.RadioBox ->
                        RegisterEventRadioBoxItem(it, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.Checkbox ->
                        RegisterEventCheckboxItem(it, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.Boolean ->
                        RegisterEventBooleanItem(it, onFieldDataChange).createFieldItemFrom(it, withTitle = false)
                    is EventRegisterFieldData.Passport ->
                        RegisterEventPassportItem(it, onFieldDataChange).createFieldItemFrom(it, getString(R.string.event_register_passport))
                    is EventRegisterFieldData.File ->
                        EventRegistrationFileGroup(it, onFieldDataChange) { presenter.onAddFileClick(it) }.createFieldItemFrom(it)
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
                    it.withEventRegistrationPersonalDataFile(file?.file, field.rightFileDescription
                            ?: file?.filename, personalDataFileClickListener)
                }
    }

    override fun showEventRegisterConfirmation() {
        bottomDialog?.dismiss()
        BottomDialog(requireContext()).apply {
            setTitle(getString(R.string.event_register_no_form_confirmation_title))
            setMessage(getString(R.string.event_register_no_form_confirmation_message))
            positiveButton {
                text = getString(R.string.event_register_request)
                clickListener = {
                    presenter.onRegisterClick()
                    true
                }
            }

            negativeButton {
                text = getString(R.string.cancel)
                clickListener = {
                    presenter.onRegisterCancelClick()
                    true
                }
            }
            setCancelable(false)
            bottomDialog = this
        }.show()
    }

    override fun showSuccessRegister(canGoToEvent: Boolean) {
        bottomDialog?.dismiss()
        BottomDialog(requireContext()).apply {
            setTitle(getString(if (canGoToEvent) R.string.event_register_sent_title else R.string.event_register_sent_moderate_title))
            setMessage(getString(if (canGoToEvent) R.string.event_register_sent_message else R.string.event_register_sent_moderate_message))
            positiveButton {
                text = getString(if (canGoToEvent) R.string.event_register_sent_button else R.string.event_register_sent_moderate_button)
                clickListener = {
                    if (canGoToEvent) presenter.onSuccessGoToEvent() else presenter.onSuccessGoToList()
                    true
                }
            }

            setOnCancelListener { presenter.onSuccessCancel() }
            bottomDialog = this
        }.show()
    }

    override fun openUrl(url: String) {
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(viewIntent)
        } catch (e: Throwable) {
            Toast.makeText(requireContext(), R.string.error_title, Toast.LENGTH_LONG).show()
        }
    }

    override fun openFileSelector() {
        startActivityForResult(Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE), REQUEST_CODE_FILE)
    }

    override fun updateFileField(fieldId: String) {
        adapter.forEachGroups {
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

    private fun findEventRegistrationFileGroup(parent: NestedGroup, fieldId: String): EventRegistrationFileGroup? {
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

    override fun showEvent() {
        findNavController().apply {
            graph.startDestination = R.id.event_tabs_fragment
            val opts = NavOptions.Builder()
                    .setPopUpTo(R.id.recommendations_fragment, true)
                    .build()
            navigate(R.id.event_tabs_fragment, null, opts)
        }
    }

    override fun layout() = R.layout.fragment_request

    companion object {
        private const val REQUEST_CODE_FILE = 100
    }
}
