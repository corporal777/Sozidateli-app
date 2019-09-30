package com.example.holders

import com.example.data.models.user.SocialRoles
import com.example.data.models.user.User
import com.example.holders.ProfileDataEditAddItem.Companion.ACTION_ADD_RECORD
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class ProfileDataEducationEditGroup(
        educationLevel: String?,
        education: List<SocialRoles>,
        private val saveClickListener: (data: Map<String, Any?>) -> Unit,
        private val cancelClickListener: () -> Unit
) : NestedGroup() {

    private val educationLevelItem = ProfileDataEducationLevelEditItem(educationLevel)
    private val educations = mutableListOf<ProfileDataEducationEditItem>()
    private val addItem = ProfileDataEditAddItem(0L, ACTION_ADD_RECORD) { add(createEducationItem(null)) }
    private val saveItem = ProfileDataEditSaveItem(1L, {
        if (checkDataValid()) saveClickListener(getDataToSave())
    }, {
        cancelClickListener()
    })

    init {
        add(educationLevelItem)
        education.map { createEducationItem(it) }.let {
            educations.addAll(it)
            addAll(it)
        }
        add(addItem)
        add(saveItem)
    }

    override fun getGroup(position: Int): Group {
        return if (position == 0) {
            educationLevelItem
        } else {
            var itemPosition = position - 1
            if (itemPosition > educations.size - 1) {
                itemPosition -= educations.size
                when (itemPosition) {
                    0 -> addItem
                    1 -> saveItem
                    else -> throw IndexOutOfBoundsException("Invalid item position: $position")
                }
            } else {
                educations[itemPosition]
            }
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            educationLevelItem -> 0
            addItem -> educations.size + 1
            saveItem -> educations.size + 2
            else -> educations.indexOf(group) + 1
        }
    }

    private fun createEducationItem(socialRoles: SocialRoles?): ProfileDataEducationEditItem {
        return ProfileDataEducationEditItem(
                socialRoles?.begin,
                socialRoles?.end,
                socialRoles?.organization,
                socialRoles?.specialty
        ) { item ->
            val position = getItemCountBeforeGroup(item)
            remove(item)
            educations.remove(item)
            notifyItemRemoved(position)
        }
    }

    private fun add(item: ProfileDataEducationEditItem) {
        educations.add(item)
        super.add(item)
        notifyItemInserted(educations.size)
    }

    private fun checkDataValid(): Boolean {
        var isValid = true
        educations.forEach {
            if (!it.isDataValid()) {
                isValid = false
                it.notifyChanged(true)
            }
        }
        return isValid
    }

    private fun getDataToSave(): Map<String, Any?> {
        return mapOf(
                User.FIELD_USER_EDUCATION to educationLevelItem.mEducationLevel,
                User.FIELD_EDUCATION to educations.map {
                    mapOf(
                            SocialRoles.FIELD_BEGIN to it.mStart,
                            SocialRoles.FIELD_END to if (it.isNotFinished) null else it.mFinish,
                            SocialRoles.FIELD_ORGANIZATION to it.mInstitution,
                            SocialRoles.FIELD_SPECIALITY to it.mSpeciality
                    )
                }
        )
    }

    override fun getGroupCount(): Int {
        return educations.size + 3
    }
}