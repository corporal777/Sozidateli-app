package com.example.ui.user

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Speaker
import com.example.data.models.user.User
import com.example.repository.ChatRepository
import com.example.repository.SpeakerRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserPresenter
@Inject constructor(private val chatRepository: ChatRepository,
                    private val userRepository: UserRepository
) : BasePresenter<UserContract.View>(), UserContract.Presenter {


    var userId:Int=-1
    private lateinit var user: User

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        userRepository.getUserById(userId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    user = it
                    viewState.setUser(it)
                },{}).call(compositeDisposable)

    }

    override fun attachView(view: UserContract.View?) {
        super.attachView(view)
    }

    override fun onWriteMsgClick() {
        chatRepository.startChat(user.user_id)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.openChat(user.fullName, user.user_id.toString(), it.chat_id.toString())
                }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

}
