package com.example.ui.support.newQuestion

import com.example.data.models.SupportFile
import com.example.ui.base.BaseContract
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

class SupportQuestionContract {

    interface View : MvpView {

        @OneExecution
        fun setFiles(files: List<SupportFile>)

        @OneExecution
        fun setFields(themes: List<String>, isHasConfirmedEmail : Boolean)

        @Skip
        fun showCustomLoading()

        @Skip
        fun hideCustomLoading()

        @Skip
        fun hideSupportQuestion()

        @Skip
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