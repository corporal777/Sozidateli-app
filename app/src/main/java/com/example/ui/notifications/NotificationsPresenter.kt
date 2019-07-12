package com.example.ui.notifications

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Notification
import com.example.extensions.build
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.Collector
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import performOnBackgroundOutOnMain
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class NotificationsPresenter
@Inject constructor(
        private val userRepository: UserRepository,
        private val appData: AppData
) : BasePresenter<NotificationsContract.View>(), NotificationsContract.Presenter {

    private lateinit var notificationsToReadPublisher: PublishSubject<Notification>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        Observable.create<Collector<Notification>> { it.onNext(createNotificationsCollector(it)) }
                .flatMap { subscribeToNotificationsRead(it) }
                .map { messages -> messages.map { it.id }.distinct() }
                .flatMapMaybe { userRepository.markNotificationsAsRead(it) }
                .performOnBackgroundOutOnMain()
                .subscribe({
                    appData.notificationsCount -= it.countMarked
                }, { it.printStackTrace() })
                .call(compositeDisposable)

        PaginationDataSourceFactory { limit, offset -> userRepository.getNotifications(limit, offset) }
                .build()
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    private fun subscribeToNotificationsRead(collector: Collector<Notification>): Observable<List<Notification>> {
        return PublishSubject.create<Notification>().apply { notificationsToReadPublisher = this }
                .doOnNext { collector.add(it) }
                .debounce(500, TimeUnit.MILLISECONDS)
                .flatMapSingle { Single.just(collector.release()) }
    }

    private fun createNotificationsCollector(creatorEmitter: ObservableEmitter<Collector<Notification>>): Collector<Notification> {
        return Collector<Notification>().apply {
            doOnRelease = Runnable { creatorEmitter.onNext(createNotificationsCollector(creatorEmitter)) }
        }
    }

    override fun onNotificationUrlClick(url: String) {
        viewState.showUrl(url)
    }

    override fun onNotificationOnScreen(notification: Notification) {
        when (notification.status) {
            "",
            "none" -> {
                notification.status = "acknowledged"
                notificationsToReadPublisher.onNext(notification)
            }
        }
    }
}
