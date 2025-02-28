package com.example.ui.event.formResult.items

import android.content.Context
import com.example.data.models.EventFormFieldModel
import com.example.data.models.EventRegisterPrefilledFields
import com.example.data.models.FileModel
import com.example.data.models.PrefilledFieldContacts
import com.example.data.models.PrefilledFieldEducation
import com.example.data.models.PrefilledFieldFiles
import com.example.data.models.PrefilledFieldString
import com.example.data.models.PrefilledFieldWorkExperience
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class EventFormResultProfileGroup(
    val context: Context,
    val field: EventFormFieldModel,
    val value: EventRegisterPrefilledFields?,
) : NestedGroup() {

    private val mainSection = Section().apply {
        setHeader(EventFormResultProfileItem(99, field.name, null, null))
    }

    init {
        setMainData()
        mainSection.registerGroupDataObserver(this)
    }


    private fun setMainData() {
        if (value?.prefilledFields.isNullOrEmpty()) return
        value!!.prefilledFields.forEachIndexed { index, it ->
            if (it is PrefilledFieldString) addMainField(index, it.name, it.value)

            else if (it is PrefilledFieldContacts) {
                if (it.name == "Рабочий телефон" || it.name == "Публичный e-mail")
                    addMainField(index, it.name, it.value)
                else addLinkField(index, it.name, it.value)
            }

            else if (it is PrefilledFieldFiles) addFileField(index, it.name, it.value)

            else if (it is PrefilledFieldEducation) addEducationField(it)

            else if (it is PrefilledFieldWorkExperience) addWorkField(it)
            else null
        }
    }

    private fun addMainField(id: Int, title: String, field: String?) {
        mainSection.add(EventFormResultProfileItem(id + 100, null, title, field))
    }


    private fun addFileField(id: Int, title: String, field: List<FileModel>?) {
        mainSection.add(EventFormResultProfileFileItem(id + 100, title, field))
    }

    private fun addLinkField(id: Int, title: String, field: String?) {
        val files =
            if (field.isNullOrEmpty()) emptyList()
            else field.split("\n").map { FileModel(uri = it, name = it) }
        mainSection.add(EventFormResultProfileFileItem(id + 100, title, files))
    }


    private fun addWorkField(data: PrefilledFieldWorkExperience) {
        mainSection.addAll(data.value?.mapIndexed { index, work ->
            EventFormResultWorkItem(index == 0, work)
        } ?: emptyList())
    }

    private fun addEducationField(data: PrefilledFieldEducation) {
        mainSection.add(EventFormResultEducationLevelItem(data.educationLevel))
        mainSection.addAll(data.academicDegree?.map { p ->
            EventFormResultAcademicDegreeItem(p.degree, p.speciality)
        } ?: emptyList())
        mainSection.addAll(data.education?.map { educationModel ->
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