package com.example.ui.auth.authorization

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.lifecycle.viewModelScope
import com.example.data.models.DataState
import com.example.data.models.SnType
import com.examle.domain.repository.AuthRepository
import com.example.ui.auth.snAuth.SnAuthCallbackHelper
import com.example.ui.base.BaseViewModel
import com.example.util.Timer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthorizationViewModel
@Inject constructor(
    private val repository: AuthRepository
) : BaseViewModel() {

    private val _stories = MutableStateFlow<List<String>>(repository.getStories())
    val stories: StateFlow<List<String>> = _stories.asStateFlow()

    private val _nextStory = mutableLongStateOf(0)
    val nextStory: State<Long> = _nextStory

    private val timer = Timer(Int.MAX_VALUE.toLong())
    private var isTimerStarted = true

    init {
        timer.start()
        viewModelScope.launch {
            delay(3000)
            timer.tick.collectLatest {
                _nextStory.longValue = it
            }
        }
    }

    fun onAuthVkClick(context: Context) {
        viewModelScope.launch {
            SnAuthCallbackHelper.start(context, SnType.VK)
                .map { it as DataState.Success }
                .collectLatest { sn ->
                    repository.authWithVk(sn.data.token, sn.data.uuid)
                        .withLoading()
                        .onEach {
                            if (it.accessData != null && !it.accessData.token.isNullOrEmpty()){
                                //appData.login(it.accessData.token)
                                //appData.saveId(it.accessData.id)
                            } else {
                                //viewState.showSnAuthorization(SnUser(sn.data, it.personalData))
                            }
                        }
                        .catch { it.printStackTrace() }
                        .launchIn(this)
                }
        }
    }

    fun startTimer(){
        if (!isTimerStarted){
            isTimerStarted = true
            timer.start()
        }
    }

    fun stopTimer(){
        isTimerStarted = false
        timer.stop()
    }
}