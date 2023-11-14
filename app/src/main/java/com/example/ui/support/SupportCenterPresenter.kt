package com.example.ui.support

import com.example.data.AppData
import com.example.data.models.SupportData
import com.example.repository.CommonRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class SupportCenterPresenter
@Inject constructor(
    private val appData: AppData,
    private val commonRepository: CommonRepository
) : BasePresenter<SupportCenterContract.View>(appData), SupportCenterContract.Presenter {

    private var mapQuestions = mapOf<String, List<SupportData>>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += commonRepository.getSupportData()
            .map { it.groupBy { x -> x.header } }
            .doOnSuccess { mapQuestions = it }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.setQuestions(it)
            }
    }

    override fun onQuestionClick(data: SupportData) {
        viewState.showSupportQuestionAnswer(data)
    }

    override fun onSearchClick() {
        viewState.showSupportSearch()
    }
}