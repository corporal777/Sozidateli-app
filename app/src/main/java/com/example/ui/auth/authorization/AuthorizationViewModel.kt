package com.example.ui.auth.authorization

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.models.DataState
import com.example.data.models.SnType
import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.ui.auth.snAuth.SnAuthCallbackHelper
import com.example.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel
@Inject constructor(
    private val repository: AuthRepository
) : BaseViewModel() {

    private val _stories = MutableStateFlow<List<String>>(repository.getStories())
    val stories: StateFlow<List<String>> = _stories.asStateFlow()


    fun onAuthVkClick(context: Context) {
        viewModelScope.launch {
            SnAuthCallbackHelper.start(context, SnType.VK)
                .map { it as DataState.Success }
                .collectLatest { sn ->
                    repository.authWithVk(sn.data.token, sn.data.uuid)
                        .withLoading()
                        .collectLatest {
                            if (it.accessData != null && !it.accessData.token.isNullOrEmpty()){
                                //appData.login(it.accessData.token)
                                //appData.saveId(it.accessData.id)
                            } else {
                                //viewState.showSnAuthorization(SnUser(sn.data, it.personalData))
                            }
                        }
                }
        }
    }
}