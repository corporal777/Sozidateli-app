import com.example.exceptions.NoInternetConnectionException
import com.example.ui.base.BaseContract
import com.example.ui.profile.ProfileContract
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
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

fun Completable.withLoadingDialog(baseView: BaseContract.LoadingView): Completable {
    val loadingDisposable = getLoadingDisposable(baseView)
    return this.doOnDispose(getHideLoadingAction(baseView, loadingDisposable))
        .doFinally(getHideLoadingAction(baseView, loadingDisposable))
        .doOnError(getHideLoadingConsumer(baseView, loadingDisposable))
}

fun <T> Single<T>.withLoadingDialog(baseView: BaseContract.LoadingView): Single<T> {
    val loadingDisposable = getLoadingDisposable(baseView)
    return this.doFinally(getHideLoadingAction(baseView, loadingDisposable))
        .doOnDispose(getHideLoadingAction(baseView, loadingDisposable))
        .doOnSuccess(getHideLoadingConsumer(baseView, loadingDisposable))
        .doOnError(getHideLoadingConsumer(baseView, loadingDisposable))
}

fun <T> Maybe<T>.withLoadingDialog(baseView: BaseContract.LoadingView): Maybe<T> {
    val loadingDisposable = getLoadingDisposable(baseView)
    return this.doFinally(getHideLoadingAction(baseView, loadingDisposable))
        .doOnDispose(getHideLoadingAction(baseView, loadingDisposable))
        .doOnSuccess(getHideLoadingConsumer(baseView, loadingDisposable))
        .doOnError(getHideLoadingConsumer(baseView, loadingDisposable))
}

fun <T> Flowable<T>.withLoadingDialog(baseView: BaseContract.LoadingView): Flowable<T> {
    val loadingDisposable = getLoadingDisposable(baseView)
    var isFirstHidden = false
    return this.doOnError(getHideLoadingConsumer(baseView, loadingDisposable))
        .doOnNext {
            if (!isFirstHidden) {
                isFirstHidden = true
                hideLoading(baseView, loadingDisposable)
            }
        }
        .doFinally(getHideLoadingAction(baseView, loadingDisposable))
        .doOnTerminate(getHideLoadingAction(baseView, loadingDisposable))
}

fun <T> Observable<T>.withLoadingDialog(baseView: BaseContract.LoadingView): Observable<T> {
    val loadingDisposable = getLoadingDisposable(baseView)
    var isFirstHidden = false
    return this.doOnError(getHideLoadingConsumer(baseView, loadingDisposable))
        .doOnNext {
            if (!isFirstHidden) {
                isFirstHidden = true
                hideLoading(baseView, loadingDisposable)
            }
        }
        .doFinally(getHideLoadingAction(baseView, loadingDisposable))
        .doOnDispose(getHideLoadingAction(baseView, loadingDisposable))
}


fun Completable.withProgressBarLoadingDialog(baseView: BaseContract.LoadingView): Completable {
    val loadingDisposable = getLoadingProgressBarDisposable(baseView)
    return this.doOnDispose(getHideProgressBarLoadingAction(baseView, loadingDisposable))
        .doFinally(getHideProgressBarLoadingAction(baseView, loadingDisposable))
        .doOnError(getHideProgressBarLoadingConsumer(baseView, loadingDisposable))
}

fun <T> Single<T>.withProgressBarLoadingDialog(baseView: BaseContract.LoadingView): Single<T> {
    val loadingDisposable = getLoadingProgressBarDisposable(baseView)
    return this.doFinally(getHideProgressBarLoadingAction(baseView, loadingDisposable))
        .doOnDispose(getHideProgressBarLoadingAction(baseView, loadingDisposable))
        .doOnSuccess(getHideProgressBarLoadingConsumer(baseView, loadingDisposable))
        .doOnError(getHideProgressBarLoadingConsumer(baseView, loadingDisposable))
}

fun <T> Maybe<T>.withProgressBarLoadingDialog(baseView: BaseContract.LoadingView): Maybe<T> {
    val loadingDisposable = getLoadingProgressBarDisposable(baseView)
    return this.doFinally(getHideProgressBarLoadingAction(baseView, loadingDisposable))
        .doOnDispose(getHideProgressBarLoadingAction(baseView, loadingDisposable))
        .doOnSuccess(getHideProgressBarLoadingConsumer(baseView, loadingDisposable))
        .doOnError(getHideProgressBarLoadingConsumer(baseView, loadingDisposable))
}

fun <T> Observable<T>.withProgressBarLoadingDialog(baseView: BaseContract.LoadingView): Observable<T> {
    val loadingDisposable = getLoadingProgressBarDisposable(baseView)
    var isFirstHidden = false
    return this.doOnError(getHideProgressBarLoadingConsumer(baseView, loadingDisposable))
        .doOnNext {
            if (!isFirstHidden) {
                isFirstHidden = true
                hideProgressBarLoading(baseView, loadingDisposable)
            }
        }
        .doFinally(getHideProgressBarLoadingAction(baseView, loadingDisposable))
        .doOnDispose(getHideProgressBarLoadingAction(baseView, loadingDisposable))
}

fun Completable.withCustomProgressBarLoadingDialog(baseView: BaseContract.LoadingView): Completable {
    val loadingDisposable = getCustomLoadingProgressBarDisposable(baseView)
    return this.doOnDispose(getHideCustomProgressBarLoadingAction(baseView, loadingDisposable))
        .doFinally(getHideCustomProgressBarLoadingAction(baseView, loadingDisposable))
        .doOnError(getHideCustomProgressBarLoadingConsumer(baseView, loadingDisposable))
}

fun <T> Single<T>.withCustomProgressBarLoadingDialog(baseView: BaseContract.LoadingView): Single<T> {
    val loadingDisposable = getCustomLoadingProgressBarDisposable(baseView)
    return this.doFinally(getHideCustomProgressBarLoadingAction(baseView, loadingDisposable))
        .doOnDispose(getHideCustomProgressBarLoadingAction(baseView, loadingDisposable))
        .doOnSuccess(getHideCustomProgressBarLoadingConsumer(baseView, loadingDisposable))
        .doOnError(getHideCustomProgressBarLoadingConsumer(baseView, loadingDisposable))
}

fun <T> Maybe<T>.withCustomProgressBarLoadingDialog(baseView: BaseContract.LoadingView): Maybe<T> {
    val loadingDisposable = getCustomLoadingProgressBarDisposable(baseView)
    return this.doFinally(getHideCustomProgressBarLoadingAction(baseView, loadingDisposable))
        .doOnDispose(getHideCustomProgressBarLoadingAction(baseView, loadingDisposable))
        .doOnSuccess(getHideCustomProgressBarLoadingConsumer(baseView, loadingDisposable))
        .doOnError(getHideCustomProgressBarLoadingConsumer(baseView, loadingDisposable))
}

fun <T> Observable<T>.withCustomProgressBarLoadingDialog(baseView: BaseContract.LoadingView): Observable<T> {
    val loadingDisposable = getCustomLoadingProgressBarDisposable(baseView)
    var isFirstHidden = false
    return this.doOnError(getHideCustomProgressBarLoadingConsumer(baseView, loadingDisposable))
        .doOnNext {
            if (!isFirstHidden) {
                isFirstHidden = true
                hideCustomProgressBarLoading(baseView, loadingDisposable)
            }
        }
        .doFinally(getHideCustomProgressBarLoadingAction(baseView, loadingDisposable))
        .doOnDispose(getHideCustomProgressBarLoadingAction(baseView, loadingDisposable))
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


private fun getLoadingDisposable(baseView: BaseContract.LoadingView): Disposable {
    return Completable.complete()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnComplete { baseView.showLoadingDialog() }
        .doOnDispose { baseView.hideLoadingDialog() }
        .subscribe()
}

private fun getLoadingProgressBarDisposable(baseView: BaseContract.LoadingView): Disposable {
    return Completable.complete()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnComplete { baseView.showProgressBarLoadingDialog() }
        .doOnDispose { baseView.hideProgressBarLoadingDialog() }
        .subscribe()
}

private fun getCustomLoadingProgressBarDisposable(baseView: BaseContract.LoadingView): Disposable {
    return Completable.complete()
        .observeOn(AndroidSchedulers.mainThread())
        //.doOnSubscribe { baseView.showCustomProgressDialog() }
        .doOnComplete { baseView.showCustomProgressDialog() }
        .doOnDispose { baseView.hideCustomProgressDialog() }
        .subscribe()
}

private fun getHideLoadingAction(baseView: BaseContract.LoadingView, loading: Disposable) = Action {
    hideLoading(baseView, loading)
}

private fun getHideProgressBarLoadingAction(
    baseView: BaseContract.LoadingView,
    loading: Disposable
) = Action {
    hideProgressBarLoading(baseView, loading)
}

private fun getHideCustomProgressBarLoadingAction(
    baseView: BaseContract.LoadingView,
    loading: Disposable
) = Action {
    hideCustomProgressBarLoading(baseView, loading)
}

private fun <T> getHideLoadingConsumer(baseView: BaseContract.LoadingView, loading: Disposable) =
    Consumer<T> {
        hideLoading(baseView, loading)
    }

private fun <T> getHideProgressBarLoadingConsumer(
    baseView: BaseContract.LoadingView,
    loading: Disposable
) = Consumer<T> {
    hideProgressBarLoading(baseView, loading)
}

private fun <T> getHideCustomProgressBarLoadingConsumer(
    baseView: BaseContract.LoadingView,
    loading: Disposable
) = Consumer<T> {
    hideCustomProgressBarLoading(baseView, loading)
}


private fun hideLoading(baseView: BaseContract.LoadingView, loading: Disposable) {
    if (loading.isDisposed) baseView.hideLoadingDialog()
    else loading.dispose()
}

private fun hideProgressBarLoading(baseView: BaseContract.LoadingView, loading: Disposable) {
    if (loading.isDisposed) baseView.hideProgressBarLoadingDialog()
    else loading.dispose()
}

private fun hideCustomProgressBarLoading(baseView: BaseContract.LoadingView, loading: Disposable) {
    if (loading.isDisposed) baseView.hideCustomProgressDialog()
    else loading.dispose()
}

fun Completable.withCheckInternetConnectivity(): Completable {
    return ReactiveNetwork.checkInternetConnectivity()
        .flatMapCompletable {
            if (it) this
            else Completable.error(NoInternetConnectionException())
        }
}

fun <T> Single<T>.withCheckInternetConnectivity(): Single<T> {
    return Completable.complete()
        .withCheckInternetConnectivity()
        .andThen(this)
}

fun <T> Maybe<T>.withCheckInternetConnectivity(): Maybe<T> {
    return Completable.complete()
        .withCheckInternetConnectivity()
        .andThen(this)
}

fun <T> Observable<T>.withCheckInternetConnectivity(): Observable<T> {
    return Completable.complete()
        .withCheckInternetConnectivity()
        .andThen(this)
}

fun <T> Flowable<T>.withCheckInternetConnectivity(): Flowable<T> {
    return Completable.complete()
        .withCheckInternetConnectivity()
        .andThen(this)
}