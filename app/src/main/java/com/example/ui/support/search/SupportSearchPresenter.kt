package com.example.ui.support.search

import com.example.data.AppData
import com.example.data.models.SupportData
import com.example.repository.CommonRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class SupportSearchPresenter
@Inject constructor(
    private val appData: AppData,
    private val commonRepository: CommonRepository
) : BasePresenter<SupportSearchContract.View>(appData), SupportSearchContract.Presenter {

    private var listQuestions = listOf<SupportData>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Maybe.just(appData.supportQuestions)
            .doOnSuccess { listQuestions = it }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.setQuestions(it)
            }
    }

    override fun onQuestionClick(question: String) {
        val data = listQuestions.find { x -> x.question == question }
        if (data != null) viewState.showSupportQuestionAnswer(data)
    }

    override fun onSearchTextChange(text: String?) {
        onSearchTextSubmit(text)
    }

    override fun onSearchTextSubmit(text: String?) {
        compositeDisposable += Maybe.defer {
            if (text.isNullOrBlank()) Maybe.just(listQuestions)
            else Maybe.just(checkHasEqualQuestion(text))
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.setQuestions(it)
            }
    }

    private fun checkHasEqualQuestion(text: String): List<SupportData> {
        return listQuestions.filter { x -> x.question.contains(text, true) }
    }


}