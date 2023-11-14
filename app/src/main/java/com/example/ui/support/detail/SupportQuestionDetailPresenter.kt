package com.example.ui.support.detail

import com.example.data.AppData
import com.example.repository.CommonRepository
import com.example.ui.base.BasePresenter
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class SupportQuestionDetailPresenter
@Inject constructor(
    private val appData: AppData,
    private val commonRepository: CommonRepository
) : BasePresenter<SupportQuestionDetailContract.View>(appData), SupportQuestionDetailContract.Presenter {

    var questionTitle = ""
    var questionAnswer = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setQuestion(questionTitle, questionAnswer)
    }


}