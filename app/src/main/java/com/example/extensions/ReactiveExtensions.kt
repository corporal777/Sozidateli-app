import com.example.ui.base.BaseContract
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
            .doOnError(getHideLoadingConsumer(baseView,loadingDisposable))


}

fun <T> Single<T>.withLoadingDialog(baseView: BaseContract.LoadingView): Single<T> {
    val loadingDisposable = getLoadingDisposable(baseView)
    return this.doFinally(getHideLoadingAction(baseView, loadingDisposable))
            .doOnDispose(getHideLoadingAction(baseView,loadingDisposable))
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
    return this.doOnError(getHideLoadingConsumer(baseView, loadingDisposable))
            .doOnNext(getHideLoadingConsumer(baseView, loadingDisposable))
            .doFinally(getHideLoadingAction(baseView, loadingDisposable))
            .toObservable()
            .doOnDispose(getHideLoadingAction(baseView, loadingDisposable))
            .toFlowable(BackpressureStrategy.LATEST)
}

fun <T> Observable<T>.withLoadingDialog(baseView: BaseContract.LoadingView): Observable<T> {
    val loadingDisposable = getLoadingDisposable(baseView)
    return this.doOnError(getHideLoadingConsumer(baseView, loadingDisposable))
            .doOnNext(getHideLoadingConsumer(baseView, loadingDisposable))
            .doFinally(getHideLoadingAction(baseView, loadingDisposable))
            .doOnDispose(getHideLoadingAction(baseView, loadingDisposable))
}

private fun getLoadingDisposable(baseView: BaseContract.LoadingView): Disposable {
    return Completable.complete()
            .delay(200, TimeUnit.MILLISECONDS, Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                baseView.showLoadingDialog()
            }
            .doOnDispose {
                baseView.hideLoadingDialog()
            }
            .subscribe()
}

private fun getHideLoadingAction(baseView: BaseContract.LoadingView, loading: Disposable) = Action {
    hideLoading(baseView, loading)
}

private fun <T> getHideLoadingConsumer(baseView: BaseContract.LoadingView, loading: Disposable) = Consumer<T> {
    hideLoading(baseView, loading)
}

private fun hideLoading(baseView: BaseContract.LoadingView, loading: Disposable) {
    if (loading.isDisposed) baseView.hideLoadingDialog()
    else loading.dispose()
}