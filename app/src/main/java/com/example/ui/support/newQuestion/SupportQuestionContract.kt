package com.example.ui.support.newQuestion

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SupportFile
import com.example.ui.base.BaseContract
import com.example.ui.views.calendarView.CalendarDay
import java.util.*

class SupportQuestionContract {

    interface View : MvpView {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setFiles(files: List<SupportFile>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setFields(themes: List<String>, isHasConfirmedEmail : Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showCustomLoading()

        @StateStrategyType(SkipStrategy::class)
        fun hideCustomLoading()

        @StateStrategyType(SkipStrategy::class)
        fun hideSupportQuestion()

        @StateStrategyType(SkipStrategy::class)
        fun enableBtnSend(enable: Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onAddFile(file: SupportFile)
        fun onRemoveFile(file: SupportFile)
        fun onSendQuestion()

        fun onChangeTheme(theme: String?)
        fun onChangeEmail(email: String?)
        fun onChangeProblem(problem: String?)
        fun onChangeDescription(desc: String?)

        fun onChangePrivacyFileAgree(isAgree : Boolean)
        fun onChangePrivacyInfoAgree(isAgree : Boolean)
    }

}