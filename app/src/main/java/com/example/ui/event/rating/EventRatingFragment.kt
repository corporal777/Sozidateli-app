package com.example.ui.event.rating

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.*
import com.example.extensions.forEachGroups
import com.example.extensions.formatToEventDatesInterval
import com.example.extensions.setRequired
import com.example.holders.ActionButtonItem
import com.example.holders.ActionButtonItem.Companion.ACTION_SEND
import com.example.holders.RatingItem
import com.example.holders.registerEvent.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_request.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class EventRatingFragment : BaseFragment(), EventRatingContract.View, ToolbarFragment {

    override val title: CharSequence
        get() = getString(R.string.event_rating_title)

    @InjectPresenter
    lateinit var presenter: EventRatingPresenter

    @Inject
    lateinit var presenterProvider: Provider<EventRatingPresenter>

    @ProvidePresenter
    fun providePresenter(): EventRatingPresenter = presenterProvider.get().apply {
        eventId = EventRatingFragmentArgs.fromBundle(requireArguments()).eventId
    }

    private val section = Section()
    private val adapter by lazy { GroupAdapter<GroupieViewHolder>().apply { add(section) } }
    private val saveButtonItem by lazy {
        ActionButtonItem(-200L, ACTION_SEND) {
            presenter.onSendClick()
        }
    }

    private val personalDataFileClickListener: OnPersonalDataFileClickListener = { presenter.onPersonalDataFileClick(it) }
    private val onFieldDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit = { presenter.onDataChange(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply { adapter = this@EventRatingFragment.adapter }
    }

    override fun setFields(event: EventRegistration, groupField: EventRegisterField?, selectedGroup: String?,
                           groups: List<EventGroup>, fieldsData: List<EventRegisterFieldData<*>>, rating: Int, files: List<FileModel>?) {
        val editable = rating <= 0
        section.apply {
            setHeader(RegisterEventHeaderItem(
                    -100L,
                    event.name,
                    null,
                    event.conferenceStart?.formatToEventDatesInterval(event.conferenceFinish),
                    null,
                    event.registrationHeadline,
                    event.registrationSubtitle
            ))

            if (editable) setFooter(saveButtonItem)

            add(RatingItem(rating, presenter::onRatingChange))

            addAll(fieldsData.map {
                when (it) {
                    is EventRegisterFieldData.String ->
                        RegisterEventStringItem(it, editable, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.Date ->
                        RegisterEventDateItem(it, editable, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.SelectBox ->
                        EventRegistrationSelectBoxItem(it, editable, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.RadioBox ->
                        RegisterEventRadioBoxItem(it, editable, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.Checkbox ->
                        RegisterEventCheckboxItem(it, editable, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.Boolean ->
                        RegisterEventBooleanItem(it, editable, onFieldDataChange).createFieldItemFrom(it, withTitle = false)
                    is EventRegisterFieldData.Passport ->
                        RegisterEventPassportItem(it, editable, onFieldDataChange).createFieldItemFrom(it, getString(R.string.event_register_passport))
                    is EventRegisterFieldData.File ->
                        EventRegistrationFileGroup(requireContext(), it, onFieldDataChange) {
                            presenter.onAddFileClick(it)
                        }.createFieldItemFrom(it)
                }
            })

            val filesGroup = Section().apply {
                setHideWhenEmpty(true)
                setHeader(EventRegistrationTitleItem(getString(R.string.event_rating_documents)))
            }

            val filess = files?.mapNotNull {
                val link = it.uri
                if (link != null) {
                    EventRegistrationPersonalDataFileItem(link, it.name, personalDataFileClickListener)
                } else {
                    null
                }
            }

            filess?.let {
                filesGroup.addAll(it)
                add(filesGroup)
            }
        }
    }

    /*override fun setFields(
            event: EventData,
            fieldsData: List<EventRegisterFieldData<*>>,
            rating: Int
    ) {
        val editable = rating <= 0
        section.apply {
            setHeader(RegisterEventHeaderItem(
                    -100L,
                    event.name,
                    null,
                    event.conferenceStart?.formatToEventDatesInterval(event.conferenceFinish),
                    null,
                    event.ratingHeadline,
                    event.ratingSubtitle
            ))

            if (editable) setFooter(saveButtonItem)

            add(RatingItem(rating, presenter::onRatingChange))

            addAll(fieldsData.map {
                when (it) {
                    is EventRegisterFieldData.String ->
                        RegisterEventStringItem(it, editable, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.Date ->
                        RegisterEventDateItem(it, editable, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.SelectBox ->
                        EventRegistrationSelectBoxItem(it, editable, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.RadioBox ->
                        RegisterEventRadioBoxItem(it, editable, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.Checkbox ->
                        RegisterEventCheckboxItem(it, editable, onFieldDataChange).createFieldItemFrom(it)
                    is EventRegisterFieldData.Boolean ->
                        RegisterEventBooleanItem(it, editable, onFieldDataChange).createFieldItemFrom(it, withTitle = false)
                    is EventRegisterFieldData.Passport ->
                        RegisterEventPassportItem(it, editable, onFieldDataChange).createFieldItemFrom(it, getString(R.string.event_register_passport))
                    is EventRegisterFieldData.File ->
                        EventRegistrationFileGroup(requireContext(), it, onFieldDataChange) {
                            presenter.onAddFileClick(it)
                        }.createFieldItemFrom(it)
                }
            })

            val filesGroup = Section().apply {
                setHideWhenEmpty(true)
                setHeader(EventRegistrationTitleItem(getString(R.string.event_rating_documents)))
            }

            val files = event.ratingFiles?.mapNotNull {
                val link = it.fileLink
                if (link != null) {
                    EventRegistrationPersonalDataFileItem(link, it.fileName, personalDataFileClickListener)
                } else {
                    null
                }
            }

            files?.let {
                filesGroup.addAll(it)
                add(filesGroup)
            }
        }
    }*/

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

    override fun showSuccessRate() {
        Toast.makeText(requireContext(), getString(R.string.event_rating_success), Toast.LENGTH_LONG).show()
    }

    override fun layout() = R.layout.fragment_request

    companion object {
        private const val REQUEST_CODE_FILE = 100
    }
}
