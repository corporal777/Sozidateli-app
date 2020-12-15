package com.example.ui.views.suggestFieldView

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.models.DaDataItem
import com.example.repository.DaDataRepository
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.json.JSONObject
import performOnBackgroundOutOnMain
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class DaDataAutoCompleteTextViewPresenter @Inject constructor(
        private val daDataRepository: DaDataRepository
) : MvpPresenter<DaDataAutoCompleteTextViewContract.View>(), DaDataAutoCompleteTextViewContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    private var items: List<DaDataItem> = emptyList()

    override fun onQueryChange(query: String) {
        compositeDisposable.clear()
        compositeDisposable += Single.timer(350, TimeUnit.MILLISECONDS)
                .flatMap { daDataRepository.suggestCity(query, 10) }
                .performOnBackgroundOutOnMain()
                .subscribe({
                    items = it.suggestions
                    viewState.setSuggested(it.suggestions)
                }, {
                    it.printStackTrace()
                })
    }

    override fun onItemSelected(position: Int) {
        val item = items.getOrNull(position) ?: return
        compositeDisposable += daDataRepository.suggestCity(item.value, 1)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val firstItem = it.suggestions.firstOrNull() ?: item
                    viewState.performOnItemSelected(firstItem)
                }, {
                    it.printStackTrace()
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
