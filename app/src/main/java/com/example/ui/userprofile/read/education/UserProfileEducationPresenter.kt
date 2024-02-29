package com.example.ui.userprofile.read.education

import android.util.Log
import com.example.data.AppData
import com.example.data.models.EducationModel
import com.example.data.models.UserDetail
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class UserProfileEducationPresenter @Inject constructor(
    private val appData: AppData
) : BaseUserProfilePresenter<UserProfileEducationContract.View>(appData),
    UserProfileEducationContract.Presenter {

    override fun onEditClick() = viewState.showEdit()


    override fun onUserUpdated(user: UserDetail) {
        val educationLevel = getEducationLevels().firstOrNull { it.id == user.educationLevel?.value }?.name ?: ""
        val userAcademicDegrees = user.binds?.academicDegree ?: emptyList()
        val userEducation = user.binds?.education ?: emptyList()

        compositeDisposable += Maybe.just(userEducation)
            .map {
                val endEducation = it.filter { x -> x.end != null }
                val notEndEducation = it.filter { x -> x.end == null }

                arrayListOf<EducationModel>().apply {
                    addAll(endEducation.sortedBy { x -> x.end })
                    addAll(notEndEducation.sortedBy { x -> x.begin })
                }
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.setUserEducation(educationLevel, userAcademicDegrees, it)
            }

    }
}
