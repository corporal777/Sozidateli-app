package com.example.ui.event.registration.items

import com.example.data.models.EventRegisterFieldData
import com.example.data.models.PrefilledFieldContacts
import com.example.data.models.PrefilledFieldEducation
import com.example.data.models.PrefilledFieldFiles
import com.example.data.models.PrefilledFieldString
import com.example.data.models.PrefilledFieldWorkExperience
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class RegisterEventProfileItemsGroup(
    private val profileForm: EventRegisterFieldData.Prefilled,
    private val onShowProfileClick: (type: PrefilledFieldClickType) -> Unit,
) : NestedGroup() {

    private val headerSection = Section().apply {
        updateItem(REProfileHeaderItem(profileForm.field.id.toLong()) { observeClick(null) })
    }
    private val mainSection = Section()

    init {
        updateData()

        headerSection.registerGroupDataObserver(this)
        mainSection.registerGroupDataObserver(this)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> headerSection
            1 -> mainSection
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            headerSection -> 0
            mainSection -> 1
            else -> -1
        }
    }

    fun updateData() {
        if (profileForm.value == null) return
        mainSection.update(profileForm.value!!.prefilledFields.mapNotNull {
            if (it is PrefilledFieldString)
                REProfileStringItem(it.name, it.value) { observeClick(it.name) }

            else if (it is PrefilledFieldContacts)
                REProfileContactItem(it.name, it.value, it.isAbsent) { observeClick(it.name) }

            else if (it is PrefilledFieldFiles) REProfileFileItem(it.name, it.value)

            else if (it is PrefilledFieldEducation)
                Section().apply {
                    setHeader(REProfileStringItem(it.name, it.educationLevel) { observeClick(it.name) })
                    it.education?.forEach { e ->
                        add(REProfileEducationItem(e.id, e) { observeClick(it.name) })
                    }
                    it.academicDegree?.forEach { d ->
                        add(REProfileAcademicItem(d.id, d) { observeClick(it.name) })
                    }
                }

            else if (it is PrefilledFieldWorkExperience)
                if (it.isAbsent)
                    REProfileStringItem(it.name, "Нет опыта работы") { observeClick(it.name) }
                else if (!it.isValid)
                    REProfileStringItem(it.name, "") { observeClick(it.name) }
                else Section().apply {
                    it.value?.forEachIndexed { index, work ->
                        add(REProfileWorkItem(work.id, work,index) { observeClick(it.name) })
                    }
                }

            else null
        })
    }

    fun showError() {
        profileForm.value?.prefilledFields?.filter { x -> !x.isValid }?.forEach {
            val item = when (it) {
                is PrefilledFieldString ->
                    mainSection.findItemBy<REProfileStringItem> { x -> x.name == it.name }

                is PrefilledFieldContacts ->
                    mainSection.findItemBy<REProfileContactItem> { x -> x.name == it.name }

                is PrefilledFieldFiles ->
                    mainSection.findItemBy<REProfileFileItem> { x -> x.name == it.name }

                is PrefilledFieldEducation ->
                    mainSection.findItemBy<REProfileStringItem> { x -> x.name == it.name }

                is PrefilledFieldWorkExperience ->
                    mainSection.findItemBy<REProfileStringItem> { x -> x.name == it.name }
            }
            if (item != null) {
                item.isErrorShown = true
                item.notifyChanged()
            }

        }
    }

    private fun observeClick(name: String?) {
        val type = when (name) {
            "ФИО" -> PrefilledFieldClickType.NAME

            "Дата рождения", "Дополнительные сведения",
            "Пол", "Регион и населенный пункт фактического проживания",
            "Файлы" -> PrefilledFieldClickType.MAIN

            "Основной e-mail", "Мобильный телефон", "Рабочий телефон",
            "Социальные сети", "Публичный e-mail", "Сайт" -> PrefilledFieldClickType.CONTACTS

            "Уровень образования" -> PrefilledFieldClickType.EDUCATION

            "Опыт работы" -> PrefilledFieldClickType.WORK

            else -> PrefilledFieldClickType.PROFILE
        }

        onShowProfileClick.invoke(type)
    }

    override fun getGroupCount() = 2
}

enum class PrefilledFieldClickType {
    PROFILE, NAME, MAIN, CONTACTS, EDUCATION, WORK
}