package com.example.ui.qrscanner.auth

import android.app.NotificationManager
import android.util.Log
import com.example.data.AppData
import com.example.data.bodies.QrBody
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.views.loading.CustomLoadingButton
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomLoading
import withDelay
import withProgressBarDialogLoading
import withProgressBarLoading
import javax.inject.Inject

@InjectViewState
class AuthWebsitePresenter
@Inject constructor(
    private val appData: AppData,
    private val socket: SocketIOManager
) : BasePresenter<AuthWebsiteContract.View>(appData), AuthWebsiteContract.Presenter {

    var token = ""
    var socketId = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        //compositeDisposable += authRepository.sendQrCode(QrBody(token, null))
        compositeDisposable += socket.connectToAuthWithQrCode(token, socketId)
            .andThen(socket.subscribeAuthQrCode())
            .performOnBackgroundOutOnMain()
            .withProgressBarLoading(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.hideContent()
                },
                onNext = {
                    viewState.apply {
                        setEnterData(it)
                        showContent()
                    }
                }
            )
    }


    override fun onConfirmAuthClick(view: CustomLoadingButton) {
        compositeDisposable += socket.confirmAuthWithQrCode(token, socketId, true)
            .performOnBackgroundOutOnMain()
            .withLoading(view)
            .subscribeSimple(
                onError = { viewState.showEventList() },
                onComplete = { viewState.showEventList() })

    }

    override fun onNotConfirmAuthClick(view: CustomLoadingButton) {
        compositeDisposable += socket.confirmAuthWithQrCode(token, socketId, false)
            .performOnBackgroundOutOnMain()
            .withLoading(view)
            .subscribeSimple(
                onError = { viewState.showEventList() },
                onComplete = { viewState.showEventList() })
    }

    private fun Completable.withLoading(view: CustomLoadingButton): Completable {
        val loadingDisposable = Completable.complete()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { viewState.showCustomLoading(view) }
            .doOnDispose { viewState.hideCustomLoading(view) }
            .subscribe()

        val actionHide = Action {
            if (loadingDisposable.isDisposed) viewState.hideCustomLoading(view)
            else loadingDisposable.dispose()
        }
        fun <T> actionConsumer() = Consumer<T> {
            if (loadingDisposable.isDisposed) viewState.hideCustomLoading(view)
            else loadingDisposable.dispose()
        }
        return this.doFinally(actionHide)
            .doOnDispose(actionHide)
            .doOnError(actionConsumer())
    }

}