package com.example.ui.userprofile.read.experience

import com.example.data.AppData
import com.example.data.models.EducationModel
import com.example.data.models.UserDetail
import com.example.data.models.WorkExperience
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class UserProfileExperiencePresenter @Inject constructor(
        appData: AppData
) : BaseUserProfilePresenter<UserProfileExperienceContract.View>(appData), UserProfileExperienceContract.Presenter {


    override fun onEditClick() = viewState.showEdit()

    override fun onUserUpdated(user: UserDetail) {
        val work = user.binds?.workExperience?.models ?: emptyList()
        compositeDisposable += Maybe.just(work)
            .map {
                val endWork = it.filter { x -> x.end != null }
                val notEndWork = it.filter { x -> x.end == null }

                arrayListOf<WorkExperience>().apply {
                    addAll(endWork.sortedBy { x -> x.end })
                    addAll(notEndWork.sortedBy { x -> x.begin })
                }
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.setUserExperience(it)
            }

    }
}
