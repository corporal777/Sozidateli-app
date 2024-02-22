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
import withDelay
import withProgressBarDialogLoading
import withProgressBarLoading
import javax.inject.Inject

@InjectViewState
class AuthWebsitePresenter
@Inject constructor(
    private val appData: AppData,
    private val authRepository: AuthRepository,
    private val socket: SocketIOManager
) : BasePresenter<AuthWebsiteContract.View>(appData), AuthWebsiteContract.Presenter {

    var token = ""
    var socketId = "null"

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
                    Log.e("DATA", it.toString())
                    viewState.apply {
                        setEnterData(it)
                        showContent()
                    }
                }
            )
    }


    override fun onConfirmEnterToWebsiteClick(view: CustomLoadingButton) {
        compositeDisposable += socket.confirmAuthWithQrCode(token, socketId)
            .andThen(socket.subscribeAcceptAuthQrCode())
            .performOnBackgroundOutOnMain()
            .withLoading(view)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onNext = {
                    Log.e("DATA", it.toString())
                    //viewState.showEventList()
                })

    }

    override fun onDoNotConfirmToEnterWebsiteClick(view: CustomLoadingButton) {
        compositeDisposable += authRepository.authWebWithQrCode(QrBody(token, false))
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showEventList()
                },
                onSuccess = {
                    viewState.showEventList()
                })
    }

    private fun <T> Flowable<T>.withLoading(view: CustomLoadingButton): Flowable<T> {
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
        return this.doOnNext(actionConsumer())
            .doFinally(actionHide)
            .doOnComplete(actionHide)
            .doOnTerminate(actionHide)
            .doOnError(actionConsumer())
    }

}