package com.example.ui.user

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.user.User
import com.example.repository.ChatRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.views.UserSubscribeButton
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserPresenter
@Inject constructor(
        private val chatRepository: ChatRepository,
        private val userRepository: UserRepository
) : BasePresenter<UserContract.View>(), UserContract.Presenter {

    lateinit var userId: String
    private lateinit var user: User

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += userRepository.getUserById(userId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    user = it
                    viewState.setUser(it)
                }, { it.printStackTrace() })
    }

    override fun onWriteMessageClick() {
        compositeDisposable += chatRepository.startChat(user.user_id.toString())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.openChat(user.fullName, user.user_avatar, it.chat_id.toString())
                }, { it.printStackTrace() })
    }

    override fun onSubscribeClick() {
        compositeDisposable += userRepository.addToFavorite(userId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.setActionUnsubscribe() }, { it.printStackTrace() })
    }

    override fun onUnsubscribeClick() {
        compositeDisposable += userRepository.removeFromFavorite(userId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.setActionSubscribe() }, { it.printStackTrace() })
    }

    override fun onUnblockClick() {
        compositeDisposable += chatRepository.startChat(userId)
                .flatMapCompletable { chatRepository.chatUnban(it.chat_id.toString()) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({ viewState.setActionSubscribe() }, { it.printStackTrace() })
    }
}
