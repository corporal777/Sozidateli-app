import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Extension function to subscribe on the background thread and observe on the main thread for a [Completable]
 * */
fun Completable.performOnBackgroundOutOnMain(): Completable {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread for a [Flowable]
 * */
fun <T> Flowable<T>.performOnBackgroundOutOnMain(): Flowable<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread  for a [Single]
 * */
fun <T> Single<T>.performOnBackgroundOutOnMain(): Single<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread  for a [Maybe]
 * */
fun <T> Maybe<T>.performOnBackgroundOutOnMain(): Maybe<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread for a [Observable]
 * */
fun <T> Observable<T>.performOnBackgroundOutOnMain(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to add a Disposable to a CompositeDisposable
 */
@Deprecated("Use rxkotlin", ReplaceWith("compositeDisposable += this"))
fun Disposable.call(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

/**
 * Extension function to subscribe on the background thread for a Flowable
 * */
fun <T> Flowable<T>.performOnBackground(): Flowable<T> {
    return this.subscribeOn(Schedulers.io())
}

/**
 * Extension function to subscribe on the background thread for a Maybe
 * */
fun <T> Maybe<T>.performOnBackground(): Maybe<T> {
    return this.subscribeOn(Schedulers.io())
}

/**
 * Extension function to subscribe on the background thread for a Completable
 * */
fun Completable.performOnBackground(): Completable {
    return this.subscribeOn(Schedulers.io())
}

/**
 * Extension function to subscribe on the background thread for a Observable
 * */
fun <T> Observable<T>.performOnBackground(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
}

/**
 * Extension function to subscribe on the main thread for a Observable
 * */
fun <T> Observable<T>.performOnMain(): Observable<T> {
    return this.subscribeOn(AndroidSchedulers.mainThread())
}



fun <T> Single<T>.withDelay(time: Long): Single<T> {
    return delay(time, TimeUnit.MILLISECONDS)
}

fun <T> Maybe<T>.withDelay(time: Long): Maybe<T> {
    return delay(time, TimeUnit.MILLISECONDS)
}

fun <T> Observable<T>.withDelay(time: Long): Observable<T> {
    return delay(time, TimeUnit.MILLISECONDS)
}

fun Completable.withDelay(time: Long): Completable {
    return delay(time, TimeUnit.MILLISECONDS)
}

fun <T> Single<T>.withTimeOut(time: Long): Single<T> {
    return timeout(time, TimeUnit.MILLISECONDS)
}

fun Completable.withTimeOut(time: Long): Completable {
    return timeout(time, TimeUnit.MILLISECONDS)
}

//fun Completable.withCheckInternetConnectivity(): Completable {
//    return ReactiveNetwork.checkInternetConnectivity()
//        .flatMapCompletable {
//            if (it) this
//            else Completable.error(NoInternetConnectionException())
//        }
//}

//fun <T> Single<T>.withCheckInternetConnectivity(): Single<T> {
//    return Completable.complete()
//        .withCheckInternetConnectivity()
//        .andThen(this)
//}
//
//fun <T> Maybe<T>.withCheckInternetConnectivity(): Maybe<T> {
//    return Completable.complete()
//        .withCheckInternetConnectivity()
//        .andThen(this)
//}
//
//fun <T> Observable<T>.withCheckInternetConnectivity(): Observable<T> {
//    return Completable.complete()
//        .withCheckInternetConnectivity()
//        .andThen(this)
//}
//
//fun <T> Flowable<T>.withCheckInternetConnectivity(): Flowable<T> {
//    return Completable.complete()
//        .withCheckInternetConnectivity()
//        .andThen(this)
//}


//fun Completable.withInfinityCustomLoading(baseView: BaseContract.LoadingView): Completable {
//    val loadingDisposable = Completable.complete()
//        .observeOn(AndroidSchedulers.mainThread())
//        .doOnComplete { baseView.showCustomLoading() }
//        .doOnDispose { baseView.hideCustomLoading() }
//        .subscribe()
//    val actionHide = Action {
//        if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
//        else loadingDisposable.dispose()
//    }
//
//    fun <T> actionConsumer() = Consumer<T> {
//        if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
//        else loadingDisposable.dispose()
//    }
//    return this.doOnDispose(actionHide).doOnError(actionConsumer())
//}
//
//fun <T> Maybe<T>.withInfinityCustomLoading(baseView: BaseContract.LoadingView): Maybe<T> {
//    val loadingDisposable = Completable.complete()
//        .observeOn(AndroidSchedulers.mainThread())
//        .doOnComplete { baseView.showCustomLoading() }
//        .doOnDispose { baseView.hideCustomLoading() }
//        .subscribe()
//    val actionHide = Action {
//        if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
//        else loadingDisposable.dispose()
//    }
//
//    fun <T> actionConsumer() = Consumer<T> {
//        if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
//        else loadingDisposable.dispose()
//    }
//    return this.doOnDispose(actionHide).doOnError(actionConsumer())
//}
//
//fun <T> Single<T>.withInfinityCustomLoading(baseView: BaseContract.LoadingView): Single<T> {
//    val loadingDisposable = Completable.complete()
//        .observeOn(AndroidSchedulers.mainThread())
//        .doOnComplete { baseView.showCustomLoading() }
//        .doOnDispose { baseView.hideCustomLoading() }
//        .subscribe()
//    val actionHide = Action {
//        if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
//        else loadingDisposable.dispose()
//    }
//
//    fun <T> actionConsumer() = Consumer<T> {
//        if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
//        else loadingDisposable.dispose()
//    }
//    return this.doOnDispose(actionHide).doOnError(actionConsumer())
//}
//
//fun <T> Maybe<T>.withCustomLoading(baseView: BaseContract.LoadingView): Maybe<T> {
//    val loadingDisposable = Completable.complete()
//        .observeOn(AndroidSchedulers.mainThread())
//        .doOnComplete { baseView.showCustomLoading() }
//        .doOnDispose { baseView.hideCustomLoading() }
//        .subscribe()
//    val actionHide = Action {
//        if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
//        else loadingDisposable.dispose()
//    }
//
//    fun <T> actionConsumer() = Consumer<T> {
//        if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
//        else loadingDisposable.dispose()
//    }
//    return this.doFinally(actionHide)
//        .doOnDispose(actionHide)
//        .doOnSuccess(actionConsumer())
//        .doOnError(actionConsumer())
//}
//
//fun <T> Single<T>.withCustomLoading(baseView: BaseContract.LoadingView): Single<T> {
//    val loadingDisposable = Completable.complete()
//        .observeOn(AndroidSchedulers.mainThread())
//        .doOnComplete { baseView.showCustomLoading() }
//        .doOnDispose { baseView.hideCustomLoading() }
//        .subscribe()
//    val actionHide = Action {
//        if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
//        else loadingDisposable.dispose()
//    }
//
//    fun <T> actionConsumer() = Consumer<T> {
//        if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
//        else loadingDisposable.dispose()
//    }
//    return this.doFinally(actionHide)
//        .doOnDispose(actionHide)
//        .doOnSuccess(actionConsumer())
//        .doOnError(actionConsumer())
//}
//
//fun <T> Observable<T>.withCustomLoading(baseView: BaseContract.LoadingView): Observable<T> {
//    val loadingDisposable = Completable.complete()
//        .observeOn(AndroidSchedulers.mainThread())
//        .doOnComplete { baseView.showCustomLoading() }
//        .doOnDispose { baseView.hideCustomLoading() }
//        .subscribe()
//    val actionHide = Action {
//        if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
//        else loadingDisposable.dispose()
//    }
//
//    fun <T> actionConsumer() = Consumer<T> {
//        if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
//        else loadingDisposable.dispose()
//    }
//    var isFirstHidden = false
//    return doOnSubscribe {
//        baseView.showCustomLoading()
//    }
//        .doOnError {
//            baseView.hideCustomLoading()
//        }
//        .doOnComplete {
//            baseView.hideCustomLoading()
//        }
////    this
////        .doFinally(actionHide)
////        .doOnDispose(actionHide)
////        .doOnNext{
////            baseView.hideCustomLoading()
////        }
//////        .doOnEach {
//////            baseView.showCustomLoading()
//////        }
////
////        .doOnComplete {
////            baseView.hideCustomLoading()
////        }
////        .doOnError(actionConsumer())
//
//
//
//}
//
//fun Completable.withCustomLoading(baseView: BaseContract.LoadingView): Completable {
//    val loadingDisposable = Completable.complete()
//        .observeOn(AndroidSchedulers.mainThread())
//        .doOnComplete { baseView.showCustomLoading() }
//        .doOnDispose { baseView.hideCustomLoading() }
//        .subscribe()
//    val actionHide = Action {
//        if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
//        else loadingDisposable.dispose()
//    }
//
//    fun <T> actionConsumer() = Consumer<T> {
//        if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
//        else loadingDisposable.dispose()
//    }
//    return this.doFinally(actionHide)
//        .doOnDispose(actionHide)
//        .doOnError(actionConsumer())
//}
//
//fun <T> Maybe<T>.withEventLoading(baseView: BaseContract.LoadingEventView,position: Int): Maybe<T> {
//    val loadingDisposable = Completable.complete()
//        .observeOn(AndroidSchedulers.mainThread())
//        .doOnComplete { baseView.showEventLoading(position) }
//        .doOnDispose { baseView.hideEventLoading(position) }
//        .subscribe()
//    val actionHide = Action {
//        if (loadingDisposable.isDisposed) baseView.hideEventLoading(position)
//        else loadingDisposable.dispose()
//    }
//
//    fun <T> actionConsumer() = Consumer<T> {
//        if (loadingDisposable.isDisposed) baseView.hideEventLoading(position)
//        else loadingDisposable.dispose()
//    }
//
//    return doOnDispose(actionHide)
//        .doOnSuccess(actionConsumer())
//        .doOnError(actionConsumer())
//
//}