package com.example.ui.profile.profileFull

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.user.User
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import performOnBackgroundOutOnMain
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ProfileFullPresenter
@Inject constructor(private val appData:AppData
) : BasePresenter<ProfileFullContract.View>(), ProfileFullContract.Presenter{
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.addOnUserChangeListener(object:AppData.OnUserChangeListener{
            override fun onUserChange(user: User?) {
                user?.let {
                    viewState.setUser(it)
                }
            }
        })
    }


    override fun onEditClick() {
        viewState.showEditProfile()
    }
}
