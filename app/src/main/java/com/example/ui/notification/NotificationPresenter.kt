package com.example.ui.notification

import android.app.NotificationManager
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Notification
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class NotificationPresenter
@Inject constructor(
        private val userRepository: UserRepository,
        private val appData: AppData,
        private val notificationManager: NotificationManager,
        private val eventRepository: EventRepository
) : BasePresenter<NotificationContract.View>(), NotificationContract.Presenter {

    lateinit var notification: Notification

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(notification)
        if (!notification.wasRead && notification.type != Notification.Type.RATE) {
            compositeDisposable += userRepository.markNotificationsAsRead(listOf(notification.id))
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        notification.wasRead = true
                    }
        }

        compositeDisposable += appData.notificationReadSubject
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    val id = it.first
                    val state = it.second
                    if (id == notification.id) {
                        notification.apply {
                            wasRead = true
                            acceptState = state
                        }
                        viewState.setData(notification)
                    }
                }
    }

    override fun onNotificationUrlClick(url: String) {
        viewState.showUrl(url)
    }

    override fun onNotificationAcceptClick() {
        compositeDisposable += userRepository.getUserFull()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    val errors = mutableListOf<String>()
                    if (it.user_name.isEmpty()) errors.add("-Имя")
                    if (it.user_last_name.isEmpty()) errors.add("-Фамилия")
                    if (it.user_middle_name?.isEmpty() == true) errors.add("-Отчество")
                    if (it.user_email?.isEmpty() == true) errors.add("-Електронная почта")
                    if (it.user_phone?.isEmpty() == true) errors.add("-Мобилный телефон")
                    if (it.user_birthday?.isEmpty() == true) errors.add("-Дата рождения")
                    if (it.user_avatar?.isEmpty() == true) errors.add("-Фотография")
                    if (it.user_short_address?.isEmpty() == true) errors.add("-Адрес")
                    if (it.social_links?.isEmpty() == true) errors.add("-Социальные сети")
                    if (it.education?.isEmpty() == true) errors.add("-Образование")
                    if (it.user_notes?.isEmpty() == true) errors.add("-Дополнительно")
                    if (it.work?.isEmpty() == true) errors.add("-Опыт работы")
                    if (errors.isEmpty())
                        updateNotificationInvite(userRepository.notificationsInviteAccept(notification.id), notification.id)
                    else
                        viewState.showErrorDialog(errors, notification.event?.name?: "")
                }, { it.printStackTrace() })

        //updateNotificationInvite(userRepository.notificationsInviteAccept(notification.id), notification.id)
    }

    override fun onNotificationCancelClick() {
        compositeDisposable += userRepository.getUserFull()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    val errors = mutableListOf<String>()
                    if (it.user_name.isEmpty()) errors.add("-Имя")
                    if (it.user_last_name.isEmpty()) errors.add("-Фамилия")
                    if (it.user_middle_name?.isEmpty() == true) errors.add("-Отчество")
                    if (it.user_email?.isEmpty() == true) errors.add("-Електронная почта")
                    if (it.user_phone?.isEmpty() == true) errors.add("-Мобилный телефон")
                    if (it.user_birthday?.isEmpty() == true) errors.add("-Дата рождения")
                    if (it.user_avatar?.isEmpty() == true) errors.add("-Фотография")
                    if (it.user_short_address?.isEmpty() == true) errors.add("-Адрес")
                    if (it.social_links?.isEmpty() == true) errors.add("-Социальные сети")
                    if (it.education?.isEmpty() == true) errors.add("-Образование")
                    if (it.user_notes?.isEmpty() == true) errors.add("-Дополнительно")
                    if (it.work?.isEmpty() == true) errors.add("-Опыт работы")
                    if (errors.isEmpty())
                        updateNotificationInvite(userRepository.notificationsInviteDecline(notification.id), notification.id)
                    else
                        viewState.showErrorDialog(errors, notification.event?.name?: "")
                }, { it.printStackTrace() })


        //updateNotificationInvite(userRepository.notificationsInviteDecline(notification.id), notification.id)
    }

    override fun onNotificationChangeDecisionClick() {
        notification.acceptState = Notification.AcceptState.NONE
        viewState.setData(notification)
    }

    override fun onNotificationRateClick() {
        notification.rateId?.let { viewState.showRating(it) }
    }

    private fun updateNotificationInvite(request: Completable, notificationId: Int) {
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { notificationManager.cancel(notificationId) }
    }
}
