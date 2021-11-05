package com.example.ui.editwork

data class EditWorksModel(
        var id: Int,
        var type: Int,
        var works: WorkExperienceNew? = null,
        var hasWork: Boolean,
        var isDeleteVisible: Boolean,
        var birthday: String? = null,
        var isDataValid: Boolean,
        var showInProfileButton: Boolean,
        var showErrors: Boolean,
        var isNotFinishedSelected: Boolean
) {

    companion object {
        const val HAS_WORK = 1
        const val WORK_ITEM = 2
        const val ADD_WORK = 3
    }
}

data class WorkExperienceNew(
        var id: Int?,
        var begin: String?,
        var end: String?,
        var organization: String?,
        var position: String?,
        var description: String?,
        var showInProfile: Boolean? = null
)