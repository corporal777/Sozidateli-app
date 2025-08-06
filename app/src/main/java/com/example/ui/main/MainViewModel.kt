package com.example.ui.main

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.data.AppData
import com.example.data.models.AuthResponse
import com.example.data.models.UserProfileFieldsModel
import com.example.data.socket.SocketConnectionState
import com.example.data.socket.SocketIOManager
import com.example.exceptions.InvalidTokenException
import com.example.extensions.flatMap
import com.example.navigation.Route
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onErrorResumeNext
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val socket: SocketIOManager,
) : BaseViewModel() {

    private val _startDestination = mutableStateOf(Route.HomeScreen.route)
    val startDestination: State<String> = _startDestination

    private val _splashCondition = mutableStateOf(true)
    val splashCondition: State<Boolean> = _splashCondition

    private var isAuthRequired = false

    init {
        viewModelScope.launch {
            delay(1000)
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
        userRepository.getUserProfileAdditionalData()
            .catch { it.printStackTrace() }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    private fun loadUser() {
        authRepository.checkUserAuthFlow()
            .flatMap { userRepository.checkUserProfileFlow() }
            .flatMap { connectToSocket() }
            .catch {
                if (it is HttpException && it.code() == 400) { appData.logoutInvalidation() }
                else it.printStackTrace()
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    private fun connectToSocket(): Flow<SocketConnectionState> {
        return socket.connect()
            .onEach {
                val connected = it == SocketConnectionState.CONNECTED

                if (connected) {
                    subscribeToNotifications()
                    socket.connectToUpdates()
                }
            }
    }

    private fun subscribeToNotifications() {
        socket.subscribeToTotalNotificationsCount()
            .onEach {
                Log.e("REQUEST INFO NOTIFICATION", it.toString())
                appData.notificationsCount = it
            }
            .catch { appData.notificationsCount = 0 }
            .launchIn(viewModelScope)
    }
}