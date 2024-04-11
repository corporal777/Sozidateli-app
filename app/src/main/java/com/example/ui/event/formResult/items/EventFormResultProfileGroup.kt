package com.example.ui.event.formResult.items

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.util.Log
import androidx.core.net.toUri
import androidx.room.util.joinIntoString
import com.example.R
import com.example.data.models.EventFile
import com.example.data.models.EventRegisterField
import com.example.data.models.EventRegisterFieldData
import com.example.data.models.FileModel
import com.example.data.models.ProfileFieldFiles
import com.example.data.models.ProfileFieldsFormResult
import com.example.extensions.updateItem
import com.example.extensions.updateItems
import com.example.ui.event.registration.items.RegisterEventAcademicDegreeItem
import com.example.ui.event.registration.items.RegisterEventEducationItem
import com.example.ui.event.registration.items.RegisterEventProfileEducationLevelItem
import com.example.ui.event.registration.items.RegisterEventProfileHeaderItem
import com.example.ui.event.registration.items.RegisterEventProfileMainItem
import com.example.ui.event.registration.items.RegisterEventProfileWorkItem
import com.example.ui.views.CustomSpannableString
import com.example.util.ClickableSpan
import com.example.util.ClickableSpanNew
import com.example.util.showCustomTabsBrowser
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class EventFormResultProfileGroup(
    val context: Context,
    val field: EventRegisterField,
    val value: ProfileFieldsFormResult?,
) : NestedGroup() {

    private val mainSection = Section().apply {
        setHeader(EventFormResultProfileItem(99, field.name, null, null))
    }

    private val options = value?.options ?: emptyList()


    init {
        setMainData()
        mainSection.registerGroupDataObserver(this)
    }


    private fun setMainData() {
        options.forEachIndexed { index, option ->
            when (option) {
                "user_fio" ->
                    addMainField(index, R.string.user_profile_fio, value?.user_name?.value)

                "user_birthday" ->
                    addMainField(index, R.string.user_profile_birthday, value?.user_birthday?.value)

                "user_gender" ->
                    addMainField(index, R.string.user_profile_gender, value?.user_gender?.value)

                "user_notes" ->
                    addMainField(index, R.string.user_profile_additional, value?.user_notes?.value)

                "user_phone" ->
                    addMainField(index, R.string.profile_phone_mobile, value?.user_phone?.value)

                "user_work_phone" ->
                    addMainField(index, R.string.profile_phone_work, value?.user_work_phone?.value)

                "user_email" ->
                    addMainField(index, R.string.profile_edit_main_email_hint, value?.user_email?.value)

                "address" ->
                    addMainField(index, R.string.user_profile_address, value?.address?.value)

                "socialNetwork" -> addLinkField(index, R.string.profile_sn, value?.user_links?.value)

                "site" -> addLinkField(index, R.string.profile_site, value?.user_sites?.value)

                "publicEmail" -> addMainField(index, R.string.profile_edit_public_email_hint, value?.user_public_email?.value)

                "recommendationFile" ->
                    mainSection.add(EventFormResultProfileFileItem(index + 100, "Файлы", value?.user_files))

                "workExperience" -> addWorkField(value!!)

                "education" -> addEducationField(value!!)

                else -> return@forEachIndexed
            }
        }
    }

    private fun addMainField(id: Int, title: Int, field: String?) {
        mainSection.add(EventFormResultProfileItem(id + 100, null, context.getString(title), field))
    }

    private fun addLinkField(id: Int, title: Int, field: String?) {
        val files =
            if (field.isNullOrEmpty()) emptyList()
            else field.split("\n").map { FileModel(uri = it, name = it) }
        val fields = ProfileFieldFiles(false, value = files)
        mainSection.add(EventFormResultProfileFileItem(id + 100, context.getString(title), fields))
    }


    private fun addWorkField(data: ProfileFieldsFormResult) {
        mainSection.addAll(data.work_experience.value?.mapIndexed { index, work ->
            EventFormResultWorkItem(index == 0, work)
        } ?: emptyList())
    }

    private fun addEducationField(data: ProfileFieldsFormResult) {
        mainSection.add(EventFormResultEducationLevelItem(data.educationLevel.value?.name))
        mainSection.addAll(data.academic_degree.value?.map { p ->
            EventFormResultAcademicDegreeItem(p.degree, p.speciality)
        } ?: emptyList())
        mainSection.addAll(data.education.value?.map { educationModel ->
            EventFormResultEducationItem(
                educationModel.organization,
                educationModel.speciality,
                educationModel.begin,
                educationModel.end,
            )
        } ?: emptyList())
    }


    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> mainSection
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            mainSection -> 0
            else -> -1
        }
    }

    override fun getGroupCount() = 1
}