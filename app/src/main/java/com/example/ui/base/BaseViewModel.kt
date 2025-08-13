package com.example.ui.base

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.examle.data.AppData
import com.examle.domain.model.event.EventModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.compose
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onErrorReturn
import kotlinx.coroutines.flow.onStart

abstract class BaseViewModel(private val appData: AppData) : ViewModel() {

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading: StateFlow<Boolean> = _loading

    fun isProfileLevelLow(event: EventModel): Boolean {
        val state = event.userRegistrationState ?: return true
        return if (state.requiredLevel == "basic") !getHasBase()
        else !getHasMax()
    }

    fun getHasBase() = appData.hasBaseState
    fun getHasMax() = appData.hasMaxState



    fun <T> Flow<T>.withLoading(): Flow<T> {
        //_loading.value = true
        return this
            .onStart { _loading.value = true }
            .onCompletion { _loading.value = false }
    }

    fun <T> Flow<T>.withProgressLoading(progressLoading : MutableStateFlow<Boolean>): Flow<T> {
        return this
            .onStart { progressLoading.value = true }
            .onCompletion { progressLoading.value = false }
    }

    fun showLoading(){
        _loading.value = true
    }

    fun hideLoading(){
        _loading.value = false
    }
}