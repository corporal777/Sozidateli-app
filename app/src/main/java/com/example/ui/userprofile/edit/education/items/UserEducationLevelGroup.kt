package com.example.ui.userprofile.edit.education.items

import android.content.Context
import android.view.View
import com.example.app.R
import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationLevel
import com.example.data.models.ToggleIntModel
import com.example.extensions.findItemBy
import com.example.extensions.forEachGroups
import com.example.extensions.updateItem
import com.example.holders.ButtonAddMore
import com.example.holders.ProfileDataAcademicDegreeEditItem
import com.example.util.DEGREES_MAX_SIZE
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class UserEducationLevelGroup(
    context: Context,
    private val educationLevel: ToggleIntModel?,
    private val availableEducations: List<EducationLevel>,
    private val availableDegrees: List<EducationLevel>,
    private val sciences: List<EducationLevel>,
    private val userDegrees: List<AcademicDegreeModel>
) : NestedGroup() {

    var selectedLevel: (level: String?) -> Unit = {}
    var enableNextButton: (isValid: Boolean) -> Unit = {}
    private var isEducationLevelValid = false

    private val availableSciences = arrayListOf<EducationLevel>().apply {
        addAll(sciences)
        add(0, EducationLevel(null, "Не выбрано", null))
    }

    private val educationLevelSection = Section()
    private val academicDegreeSection = Section().apply {
        setHideWhenEmpty(true)
    }

    private val addDegreeButton = ButtonAddMore(context.getString(R.string.profile_sciences_add)) {
        if (academicDegreeSection.itemCount < DEGREES_MAX_SIZE)
            academicDegreeSection.add(
                createDegreeEditItem(
                    null,
                    availableDegrees.first(),
                    availableSciences.first().name,
                    false
                )
            )
        isDeleteVisible()
    }


    init {
        educationLevelSection.updateItem(UserEducationLevelItem(
            educationLevel?.value,
            availableEducations,
            userDegrees.isNotEmpty(),
            educationLevel?.showInProfile,
            { initAddDegreeButton(it) }, { selectedLevel.invoke(it) }
        ).apply {
            onDataValid = {
                isEducationLevelValid = it
                enableButtonNext()
            }
        })

        if (userDegrees.isNotEmpty()) {
            academicDegreeSection.update(
                userDegrees.map {
                    createDegreeEditItem(
                        it.id,
                        availableDegrees.firstOrNull { degree -> degree.id == it.degree },
                        availableSciences.firstOrNull { science -> science.id == it.speciality }?.name,
                        it.showInProfile
                    )
                }
            )
            isDeleteVisible()
        }

        initAddDegreeButton(userDegrees.isNotEmpty())

        educationLevelSection.registerGroupDataObserver(this)
        academicDegreeSection.registerGroupDataObserver(this)
        addDegreeButton.registerGroupDataObserver(this)
    }

    private fun createDegreeEditItem(
        id: Int?,
        degree: EducationLevel?,
        specialisation: String?,
        showInProfile: Boolean?
    ): ProfileDataAcademicDegreeEditItem {
        return ProfileDataAcademicDegreeEditItem(
            id,
            degree?.name,
            specialisation,
            availableDegrees,
            availableSciences,
            showInProfile
        ) {
            academicDegreeSection.remove(it)
            isDeleteVisible()
        }
    }

    private fun initAddDegreeButton(hasDegree: Boolean) {
        if (hasDegree) {
            addDegreeButton.setButtonVisibility(View.VISIBLE)
            if (academicDegreeSection.itemCount == 0) {
                if (userDegrees.isNotEmpty()) {
                    academicDegreeSection.update(
                        userDegrees.map {
                            createDegreeEditItem(
                                it.id,
                                availableDegrees.firstOrNull { degree -> degree.id == it.degree },
                                availableSciences.firstOrNull { science -> science.id == it.speciality }?.name,
                                it.showInProfile
                            )
                        }
                    )
                } else {
                    academicDegreeSection.add(
                        createDegreeEditItem(
                            null,
                            availableDegrees.first(),
                            availableSciences.first().name,
                            false
                        )
                    )
                }
                isDeleteVisible()
            }
        } else {
            addDegreeButton.setButtonVisibility(View.GONE)
            academicDegreeSection.updateItem(null)
        }
    }

    private fun isDeleteVisible() {
        val isDeleteD = academicDegreeSection.itemCount > 1
        academicDegreeSection.forEachGroups<ProfileDataAcademicDegreeEditItem> {
            it.isDeleteVisible = isDeleteD
            notifyChanged()
        }
    }

    fun isDataValid(): Boolean = isEducationLevelValid
    private fun enableButtonNext() = enableNextButton.invoke(isEducationLevelValid)

    fun checkDataValid(): Boolean {
        return educationLevelSection.findItemBy<UserEducationLevelItem> { true }?.isDataValid()
            ?: false
    }

    fun getDegreeToSave(): List<AcademicDegreeModel>? {
        val degrees = mutableListOf<AcademicDegreeModel>()
        if (academicDegreeSection.itemCount > 0) {
            academicDegreeSection.forEachGroups<ProfileDataAcademicDegreeEditItem> {
                degrees.add(it.getDataToSave())
            }
        }
        return degrees
    }

    fun getEducationLevelToSave(): ToggleIntModel? {
        return educationLevelSection.findItemBy<UserEducationLevelItem> { true }?.getDataToSave()
    }

    fun getSelectedLevel(): String? {
        return educationLevelSection.findItemBy<UserEducationLevelItem> { true }?.getLevel()
    }


    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> educationLevelSection
            1 -> academicDegreeSection
            2 -> addDegreeButton
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            educationLevelSection -> 0
            academicDegreeSection -> 1
            addDegreeButton -> 2
            else -> -1
        }
    }

    override fun getGroupCount(): Int {
        return 3
    }
}