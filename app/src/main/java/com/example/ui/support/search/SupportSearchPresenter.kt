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
    private var searchWord = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadQuestions()
    }

    private fun loadQuestions(){
        viewState.setQuestions(List(10) { null })
        compositeDisposable += commonRepository.searchSupportQuestion(searchWord)
            .doOnSuccess { listQuestions = it }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { viewState.setQuestions(emptyList()) },
                onSuccess = { viewState.setQuestions(it) }
            )
    }

    override fun onQuestionClick(question: String) {
        val data = listQuestions.find { x -> x.question == question }
        if (data != null) viewState.showSupportQuestionAnswer(data)
    }

    override fun onSearchTextChange(text: String?) {
        searchWord = text ?: ""
        loadQuestions()
    }

    override fun onSearchTextSubmit(text: String?) {
        searchWord = text ?: ""
        loadQuestions()
    }

    private fun checkHasEqualQuestion(text: String): List<SupportData> {
        val pair = getTextWithSynonym(text)
        return listQuestions.filter { x ->
            if (pair.second.isNullOrEmpty()) x.answer.contains(pair.first, true)
            else x.answer.contains(pair.first, true)
                    || x.answer.contains(pair.second!!, true)
        }
    }

    private fun getTextWithSynonym(text: String): Pair<String, String?> {
        if (text == "код" || text == "ключ" || text == "секрет" || text == "доступ" || text == "авторизация")
            return Pair("Пароль", text)

        else if (text == "проникли") return Pair("Взломали", text)

        else if (text == "обезопасить" || text == "предохранить") return Pair("Защитить", text)

        else if (text == "перепутал" || text == "потерял" || text == "помню" || text == "уверен")
            return Pair("Забыл", text)

        else if (text == "выход" || text == "закрыть" || text == "завершить" || text == "отключиться")
            return Pair("Выйти", text)

        else if (text == "имя" || text == "пользователь" || text == "индентификатор")
            return Pair("Логин", text)

        else if (text == "доступ" || text == "вернуть" || text == "восстановить" || text == "пароль")
            return Pair("Восстановить", text)

        else if (text == "удалиться" || text == "стереть" || text == "убрать")
            return Pair("Удалить", text)

        else if (text == "изменить" || text == "заменить" || text == "сменить")
            return Pair("Поменять", text)

        else if (text == "утвердить" || text == "согласовать" || text == "привязать" || text == "добавить")
            return Pair("Подтвердить", text)

        else if (text == "изменить" || text == "модифицировать" || text == "править" || text == "отредактировать")
            return Pair("Редактировать", text)

        else if (text == "настройки" || text == "установить" || text == "регулировать")
            return Pair("Настроить", text)

        else if (text == "спрятаться" || text == "замаскировать" || text == "приватность")
            return Pair("Скрыть", text)

        else if (text == "конфиденциальность" || text == "секретность" || text == "тайность")
            return Pair("Приватность", text)

        else return Pair(text, null)
    }

}