package com.example.ui.agreement

import com.example.data.models.EventNew
import com.example.repository.EventRepository
import com.example.ui.base.BaseContract
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import moxy.MvpPresenter
import performOnBackgroundOutOnMain
import withCustomLoading
import javax.inject.Inject

@InjectViewState
class UserAgreementBottomSheetPresenter
@Inject constructor(
    private val eventRepository: EventRepository
) : MvpPresenter<UserAgreementBottomSheetContract.View>(), UserAgreementBottomSheetContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun acceptAgreement(eventNew: EventNew) {
        compositeDisposable += eventRepository.acceptRegistrationAgreement(eventNew.id.toString())
            .doOnSuccess { if (it.isAccepted()) eventNew.state?.agreement?.setAccepted() }
            .performOnBackgroundOutOnMain()
            .withCustomLoading()
            .subscribeBy(
                onError = {
                    it.printStackTrace()
                    viewState.setAcceptAgreement(false, eventNew)
                },
                onSuccess = {
                    viewState.setAcceptAgreement(true, eventNew)
                })
    }

    override fun notAcceptAgreement(eventNew: EventNew) {
        viewState.setAcceptAgreement(false, eventNew)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    fun <T> Single<T>.withCustomLoading(): Single<T> {
        val loadingDisposable = Completable.complete()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { viewState.showCustomLoading(true) }
            .doOnDispose { viewState.showCustomLoading(false) }
            .subscribe()
        val actionHide = Action {
            if (loadingDisposable.isDisposed) viewState.showCustomLoading(false)
            else loadingDisposable.dispose()
        }

        fun <T> actionConsumer() = Consumer<T> {
            if (loadingDisposable.isDisposed) viewState.showCustomLoading(false)
            else loadingDisposable.dispose()
        }
        return this.doFinally(actionHide)
            .doOnDispose(actionHide)
            .doOnSuccess(actionConsumer())
            .doOnError(actionConsumer())
    }
}