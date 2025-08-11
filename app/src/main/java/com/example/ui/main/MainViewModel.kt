package com.example.ui.main

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.examle.data.AppData
import com.examle.data.models.DataState
import com.examle.domain.interactor.AuthInteractor
import com.examle.domain.interactor.UserInteractor
import com.examle.domain.model.SocketConnectionState
import com.examle.domain.repository.SocketIOManager
import com.example.common.flatMap
import com.example.navigation.Route
import com.examle.domain.repository.AuthRepository
import com.examle.domain.repository.UserRepository
import com.example.data.UiStateData
import com.example.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val appData: AppData,
    private val uiStateData: UiStateData,
    private val socket: SocketIOManager,
    private val userInteractor : UserInteractor,
    private val authInteractor : AuthInteractor
) : BaseViewModel() {

    private val _startDestination = mutableStateOf(Route.HomeScreen.route)
    val startDestination: State<String> = _startDestination

    private val _splashCondition = mutableStateOf(true)
    val splashCondition: State<Boolean> = _splashCondition

    val progressLoading: StateFlow<Boolean> get() = appData._progressLoading

    private var isAuthRequired = false

    init {
        viewModelScope.launch {
            delay(700)
            _splashCondition.value = false
        }

        appData.tokenChangeFlow.onEach { token ->
            getAdditionalData()
            if (token.value.isNullOrEmpty()) {
                isAuthRequired = true
            } else loadUser()

        }.launchIn(viewModelScope)
    }

    private fun getAdditionalData() {
        userInteractor.getUserProfileAdditionalData()
            .catch { it.printStackTrace() }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    private fun loadUser() {
        authInteractor.checkUserAuth()
            .flatMap { userInteractor.checkUserProfileState() }
            //.flatMap { connectToSocket() }
            .catch { it.printStackTrace() }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

//    private fun connectToSocket(): Flow<SocketConnectionState> {
//        return socket.connect()
//            .onEach {
//                val connected = it == SocketConnectionState.CONNECTED
//
//                if (connected) {
//                    subscribeToNotifications()
//                    socket.connectToUpdates()
//                }
//            }
//    }

    private fun subscribeToNotifications() {
//        socket.subscribeToTotalNotificationsCount()
//            .onEach {
//                Log.e("REQUEST INFO NOTIFICATION", it.toString())
//                appData.notificationsCount = it
//            }
//            .catch { appData.notificationsCount = 0 }
//            .launchIn(viewModelScope)
    }
}