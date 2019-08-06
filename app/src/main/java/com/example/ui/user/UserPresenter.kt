package com.example.ui.user

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.Interest
import com.example.data.models.Organization
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.data.models.user.UserInterests
import com.example.repository.ChatRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Single
import io.reactivex.functions.BiFunction
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
        compositeDisposable += Single.zip(
                userRepository.getUserById(userId),
                userRepository.getInterests(),
                BiFunction<User, List<Interest>, UserInterests> { user, interests ->
                    val groupedInterests: MutableMap<Interest, MutableList<Interest>>? = user.interests?.let { userInterests ->
                        val groups = mutableMapOf<Interest, MutableList<Interest>>()
                        userInterests.forEach {
                            val key = interests.find { interest -> interest.id == it.parent }
                            if (key != null) {
                                val list = groups.getOrPut(key) { mutableListOf() }
                                list.add(it)
                            }
                        }
                        return@let groups
                    }

                    return@BiFunction UserInterests(user, groupedInterests)
                }
        )
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    user = it.user
                    viewState.setUser(it.user, it.interests)
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

    override fun onOrganizationClick(organization: Organization) {
        viewState.showOrganization(organization)
    }

    override fun onFileClick(file: RecommendationFile) {
        file.url?.let { viewState.downloadFile(it) }
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
