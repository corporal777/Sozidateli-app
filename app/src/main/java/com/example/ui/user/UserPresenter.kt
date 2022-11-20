package com.example.ui.user

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.BuildConfig
import com.example.data.AppData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.bodies.CreateChatBody
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.data.models.user.UserData
import com.example.extensions.defaultServerDateFormatter
import com.example.repository.ChatRepository
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.userSessions.UserSessionsContract
import com.example.util.ImageUtil
import com.example.util.loadBitmap
import com.example.util.loadBitmapNew
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.Function
import io.reactivex.functions.Function3
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withDelay
import withLoadingDialog
import withProgressBarLoadingDialog
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class UserPresenter
@Inject constructor(
    private val appData: AppData,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val commonRepository: CommonRepository,
    private val eventRepository: EventRepository,
) : BasePresenter<UserContract.View>(appData), UserContract.Presenter {

    private var mDy = 0f
    lateinit var userId: String
    private lateinit var profileUserData: ProfileUserData
    lateinit var context: Context


    override fun attachView(view: UserContract.View?) {
        super.attachView(view)
        viewState.setAppBarShadow(mDy)
    }

    override fun changeAppBarElevation(value: Int) {
        mDy = abs(value / 10f)
        viewState.setAppBarShadow(mDy)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadUserData(true)
    }

    private fun loadUserData(withLoading: Boolean) {
        if (withLoading) viewState.showShimmerPlaceholder()
        compositeDisposable += userRepository.getUserByIdNew(userId)
            .zipWith(commonRepository.getInterests())
            .flatMap {
                val user = it.first
                val avatar = it.first.image.uri.loadAvatarNew()
                val interests = it.second
                Maybe.just(UserData(user, avatar, groupUserInterests(user, interests)))
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showUserHiddenDialog()
                },
                onSuccess = {
                    profileUserData = ProfileUserData(it, false)
                    compositeDisposable += userAddressRequest()
                        .performOnBackgroundOutOnMain()
                        .subscribeSimple(
                            onError = {
                                viewState.apply {
                                    setUser(profileUserData)
                                    if (profileUserData.user.state?.isRegistered == true) {
                                        setSubscribeFavoriteAction(profileUserData.user.getUserSubscribeAction())
                                        setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                                    }
                                }
                            },
                            onSuccess = { add ->
                                viewState.apply {
                                    if (add.data?.isNotEmpty() == true)
                                        profileUserData.userData.user.address?.shortAddres =
                                            add.data[0].region

                                    setUser(profileUserData)
                                    if (profileUserData.user.state?.isRegistered == true) {
                                        setSubscribeFavoriteAction(profileUserData.user.getUserSubscribeAction())
                                        setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                                    }
                                }
                            })
                })
    }

    private fun groupUserInterests(
        user: /*User*/UserDetail,
        interests: List<InterestNew>
    ): MutableMap<InterestNew, MutableList<InterestNew>>? {
        val groups = mutableMapOf<InterestNew, MutableList<InterestNew>>()
        val headers = interests.filter { it.parent == 0 }
        headers.forEach {
            val parent = interests.filter { parent -> parent.parent == it.id }
            parent.let { it1 ->
                user.interests?.forEach { usIn ->
                    val isUserInterest = it1.find { it2 -> it2.id == usIn }
                    if (isUserInterest != null)
                        groups.getOrPut(it) { mutableListOf() }.add(isUserInterest)
                }
            }
        }
        return groups
    }

    override fun onWriteMessageClick() {
        val user = profileUserData.user
        if (user.binds?.chatRoomWithMe == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(user.id))
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple {
                    viewState.openChat(user.fullName, user.image.uri, it.id.toString())
                }
        } else {
            viewState.openChat(
                user.fullName,
                user.image.uri,
                profileUserData.user.binds?.chatRoomWithMe?.id.toString()
            )
        }
    }

    override fun onOrganizationClick(organization: /*Organization*/OrganizationNew) {
        viewState.showOrganization(organization)
    }

    override fun onFileClick(file: /*RecommendationFile*/FileModel) {
        file.uri?.let { viewState.downloadFile(it) }
    }

    override fun onSubscribeClick() {
        compositeDisposable += eventRepository.addToFavorites(
            AddToFavoriteModel(
                appData.getId(),
                AddToFavoriteEntityModel(AddToFavoriteEntityModel.FAVORITE_SPEAKER, userId.toInt())
            )
        )
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                profileUserData.user.binds?.userFavorite = EventUserFavorite(it.id, it.user)
                viewState.setSubscribeFavoriteAction(profileUserData.user.getUserSubscribeAction())
            }
    }

    override fun onUnsubscribeClick() {
        compositeDisposable += eventRepository.deleteFromFavorite(profileUserData.user.binds?.userFavorite?.id.toString())
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                profileUserData.user.binds?.userFavorite = null
                viewState.setSubscribeFavoriteAction(profileUserData.user.getUserSubscribeAction())
            }
    }

    override fun onUnblockClick() {
        val user = profileUserData.user
        if (user.binds?.chatRoomWithMe == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(userId.toInt()))
                .flatMapCompletable { chatRepository.deleteBan(user.binds?.isUserInBan?.id ?: 1) }
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribe({
                    profileUserData.user.binds?.isUserInBan = null
                    viewState.setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                    viewState.setEnableAddToFavoriteButton(true)
                }, { it.printStackTrace() })
        } else {
            compositeDisposable += chatRepository.deleteBan(user.binds?.isUserInBan?.id ?: 1)
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribe({
                    profileUserData.user.binds?.isUserInBan = null
                    viewState.setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                    viewState.setEnableAddToFavoriteButton(true)
                }, { it.printStackTrace() })
        }
    }

    override fun onBlockClick() {
        viewState.showBlockConfirmation()
    }

    override fun onBlockConfirm() {
        val user = profileUserData.user
        if (user.binds?.chatRoomWithMe == null) {
            compositeDisposable += chatRepository.createChat(CreateChatBody(userId.toInt()))
                .flatMap { chatRepository.chatBann(CreateChatBody(userId.toInt())) }
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribe({
                    profileUserData.user.binds?.isUserInBan = it
                    viewState.setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                    viewState.setEnableAddToFavoriteButton(false)
                }, { it.printStackTrace() })
        } else {
            compositeDisposable += chatRepository.chatBann(CreateChatBody(userId.toInt()))
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribe({
                    profileUserData.user.binds?.isUserInBan = it
                    viewState.setSubscribeBlockAction(profileUserData.user.getUserSubscribeAction())
                    viewState.setEnableAddToFavoriteButton(false)
                }, { it.printStackTrace() })
        }
    }

    private fun String?.loadAvatar(): Maybe<Optional<Bitmap>> {
        return loadBitmap()
    }

    private fun String?.loadAvatarNew(): Bitmap? {
        return loadBitmapNew(context)
    }

    private fun isCurrentUser() = userId == appData.getId().toString()

    override fun onRefreshRequest() {
        loadUserData(false)
    }

    private fun userAddressRequest(): Single<SearchAddressModel> {
        return userRepository.searchAddress(
            profileUserData.userData.user.address?.getShortAddress() ?: ""
        )
    }

}
