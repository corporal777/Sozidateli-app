package com.example.ui.request

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.RegisterEventFieldData
import com.example.data.models.RegistrationEvent
import com.example.extensions.forEachGroups
import com.example.extensions.formatToInterval
import com.example.extensions.setRequired
import com.example.holders.registerEvent.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import fileName
import kotlinx.android.synthetic.main.fragment_request.*
import javax.inject.Inject
import javax.inject.Provider

class RequestFragment : BaseFragment(), RequestContract.View, ToolbarFragment {

    override val title: CharSequence
        get() = getString(R.string.request_label)

    @InjectPresenter
    lateinit var presenter: RequestPresenter

    @Inject
    lateinit var presenterProvider: Provider<RequestPresenter>

    @ProvidePresenter
    fun providePresenter(): RequestPresenter = presenterProvider.get().apply {
        eventId = RequestFragmentArgs.fromBundle(arguments!!).eventId
    }

    private val section = Section()
    private val adapter by lazy { GroupAdapter<GroupieViewHolder>().apply { add(section) } }

    private val personalDataFileClickListener: OnPersonalDataFileClickListener = { presenter.onPersonalDataFileClick(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply { adapter = this@RequestFragment.adapter }
    }

    override fun setFields(event: RegistrationEvent, fieldsData: List<RegisterEventFieldData<*>>) {
        section.apply {
            setHeader(RegisterEventHeaderItem(
                    -100L,
                    event.organization?.name,
                    event.conferenceStart?.formatToInterval(event.conferenceFinish),
                    event.description,
                    event.registrationName,
                    event.registrationSubtitle
            ))

            addAll(fieldsData.map {
                when (it) {
                    is RegisterEventFieldData.String ->
                        RegisterEventStringItem(it).createFieldItemFrom(it)
                    is RegisterEventFieldData.Date ->
                        RegisterEventDateItem(it).createFieldItemFrom(it)
                    is RegisterEventFieldData.SelectBox ->
                        RegisterEventSelectBoxItem(it).createFieldItemFrom(it)
                    is RegisterEventFieldData.RadioBox ->
                        RegisterEventRadioBoxItem(it).createFieldItemFrom(it)
                    is RegisterEventFieldData.Checkbox ->
                        RegisterEventCheckboxItem(it).createFieldItemFrom(it)
                    is RegisterEventFieldData.Boolean ->
                        RegisterEventBooleanItem(it).createFieldItemFrom(it, withTitle = false)
                    is RegisterEventFieldData.Passport ->
                        RegisterEventPassportItem(it).createFieldItemFrom(it, getString(R.string.event_register_passport))
                    is RegisterEventFieldData.File ->
                        EventRegistrationFileGroup(it) { presenter.onAddFileClick(it.field.id) }.createFieldItemFrom(it)
                }
            })
        }
    }

    private fun Group.createFieldItemFrom(
            fieldData: RegisterEventFieldData<*>,
            customTitle: String? = null,
            withTitle: Boolean = true,
            withFile: Boolean = true
    ): Group {
        val field = fieldData.field
        return let {
            if (withTitle) {
                val title = customTitle ?: field.name
                it.withEventRegistrationTitle(title?.setRequired(field.required)?.toString())
            } else it
        }
                .let {
                    if (withFile) {
                        val file = field.rightFile
                        it.withEventRegistrationPersonalDataFile(file?.file, field.rightFileDescription
                                ?: file?.filename, personalDataFileClickListener)
                    } else it
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

    override fun updateFileField(fieldId: String, path: String) {
        adapter.forEachGroups {
            val fileGroup = if (it is NestedGroup) findEventRegistrationFileGroup(it, fieldId)
            else null
            if (fileGroup != null) {
                fileGroup.addFile(Uri.parse(path).fileName(requireContext()) ?: path, path)
                return@forEachGroups
            }
        }
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
                result?.data?.toString()
            } else null

            if (url != null) presenter.onFileSelected(url)
            else presenter.onFileSelectionCancel()
        }
    }

    override fun showSuccessRegister() {

    }

    override fun enableActionButton(enable: Boolean) {
        /* btnSendRequest.apply {
             isEnabled = enable

             val background: Int
             val textColor: Int
             if (enable) {
                 background = R.drawable.background_corners
                 textColor = Color.WHITE
             } else {
                 background = R.drawable.background_edittext_login
                 textColor = Color.DKGRAY
             }

             setBackgroundResource(background)
             setTextColor(textColor)
         }*/
    }


    override fun layout() = R.layout.fragment_request

    companion object {
        private const val REQUEST_CODE_FILE = 100
    }
}
