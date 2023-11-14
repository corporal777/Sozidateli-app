package com.example.ui.support.newQuestion

import android.content.Context
import com.example.data.AppData
import com.example.data.models.SupportFile
import com.example.repository.CommonRepository
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import performOnBackgroundOutOnMain
import java.io.File
import javax.inject.Inject

@InjectViewState
class SupportQuestionPresenter @Inject constructor(
    private val context: Context,
    private val appData: AppData,
    private val commonRepository: CommonRepository
) : MvpPresenter<SupportQuestionContract.View>(), SupportQuestionContract.Presenter {

    private val compositeDisposable = CompositeDisposable()
    private val listFiles = arrayListOf<SupportFile>()

    var isInfoAgree = false
    var isFileAgree = false
    var questionTheme: String? = null
    var questionEmail: String? = null
    var questionProblem: String? = null
    var questionDescription: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            setFields(getThemes().map { x -> x.key }.toMutableList(), appData.isUserEmailConfirmed())
            enableBtnSend(isDataValid())
        }
    }

    override fun onAddFile(file: SupportFile) {
        if (listFiles.size >= 3) return
        if (listFiles.any { x -> x.uri == file.uri }) return
        listFiles.add(file)
        viewState.setFiles(listFiles)
    }

    override fun onRemoveFile(file: SupportFile) {
        if (listFiles.contains(file)) listFiles.remove(file)
        viewState.setFiles(listFiles)
    }

    override fun onSendQuestion() {
        compositeDisposable += getRequestBody()
            .flatMapCompletable { commonRepository.sendSupportQuestion(it) }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeBy(
                onError = { it.printStackTrace() },
                onComplete = { viewState.hideSupportQuestion() }
            )
    }

    override fun onChangeTheme(theme: String?) {
        questionTheme = theme
        viewState.enableBtnSend(isDataValid())
    }

    override fun onChangeEmail(email: String?) {
        questionEmail = email
        viewState.enableBtnSend(isDataValid())
    }

    override fun onChangeProblem(problem: String?) {
        questionProblem = problem
        viewState.enableBtnSend(isDataValid())
    }

    override fun onChangeDescription(desc: String?) {
        questionDescription = desc
        viewState.enableBtnSend(isDataValid())
    }

    override fun onChangePrivacyFileAgree(isAgree: Boolean) {
        isFileAgree = isAgree
        viewState.enableBtnSend(isDataValid())
    }

    override fun onChangePrivacyInfoAgree(isAgree: Boolean) {
        isInfoAgree = isAgree
        viewState.enableBtnSend(isDataValid())
    }


    private fun isDataValid(): Boolean {
        return (!questionTheme.isNullOrEmpty()
                && (if (appData.isUserEmailConfirmed()) true else !questionEmail.isNullOrEmpty())
                && !questionProblem.isNullOrEmpty()
                && !questionDescription.isNullOrEmpty()
                && isFileAgree
                && isInfoAgree)
    }

    private fun getRequestBody(): Single<MultipartBody> {
        return Single.fromCallable {
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .apply {
                    addFormDataPart("user", appData.getId().toString())
                    if (!getThemes()[questionTheme].isNullOrBlank())
                        addFormDataPart("type", getThemes()[questionTheme]!!)
                    if (!questionEmail.isNullOrBlank())
                        addFormDataPart("email", questionEmail!!)
                    if (!questionProblem.isNullOrBlank())
                        addFormDataPart("message", questionProblem!!)
                    if (!questionDescription.isNullOrBlank())
                        addFormDataPart("description", questionDescription!!)

                    listFiles.forEachIndexed { index, it ->
                        val path = it.getAbsolutePath(context)
                        if (path != null) {
                            val file = File(path.first)
                            val body = file.asRequestBody(path.second.toMediaTypeOrNull())
                            addFormDataPart("[data][$index]", file.name, body)
                        }
                    }
                }.build()
        }
    }

    private fun getThemes(): Map<String, String> {
        return mapOf<String, String>(
            "Безопасность и доступ к аккаунту" to "security",
            "Настройки профиля" to "profile",
            "Приватность и смена имени" to "privacy",
            "Организации" to "organizations",
            "Мероприятия" to "events",
            "Конкурсы" to "contests",
            "Мобильные приложения" to "app",
            "Другие вопросы" to "other",
        )
    }


    private fun Completable.withCustomLoading(baseView: SupportQuestionContract.View): Completable {
        val loadingDisposable = Completable.complete()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { baseView.showCustomLoading() }
            .doOnDispose { baseView.hideCustomLoading() }
            .subscribe()
        val actionHide = Action {
            if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
            else loadingDisposable.dispose()
        }

        fun <T> actionConsumer() = Consumer<T> {
            if (loadingDisposable.isDisposed) baseView.hideCustomLoading()
            else loadingDisposable.dispose()
        }
        return this.doFinally(actionHide)
            .doOnDispose(actionHide)
            .doOnError(actionConsumer())
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


}