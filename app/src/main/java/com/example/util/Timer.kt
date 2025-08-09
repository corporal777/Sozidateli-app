package com.example.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class PlayerMode {
    PLAYING,
    PAUSED,
    STOPPED
}

class Timer(
    private val millisInFuture: Long,
    private val countDownInterval: Long = 3000L,
    runAtStart: Boolean = false,
    val onFinish: (() -> Unit)? = null,
    val onTick: ((Long) -> Unit)? = null
) {
    private var job: Job = Job()
    private val _tick = MutableStateFlow(0L)
    val tick = _tick.asStateFlow()
    private val _playerMode = MutableStateFlow(PlayerMode.STOPPED)
    val playerMode = _playerMode.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        if (runAtStart) start()
    }

    fun start() {
        if (_tick.value == 0L) _tick.value = millisInFuture
        job.cancel()
        job = scope.launch(Dispatchers.IO) {
            _playerMode.value = PlayerMode.PLAYING
            while (isActive) {
                if (_tick.value <= 0) {
                    job.cancel()
                    onFinish?.invoke()
                    _playerMode.value = PlayerMode.STOPPED
                    return@launch
                }
                delay(timeMillis = countDownInterval)
                _tick.value -= countDownInterval
                onTick?.invoke(this@Timer._tick.value)
            }
        }
    }

    fun pause() {
        job.cancel()
        _playerMode.value = PlayerMode.PAUSED
    }

    fun stop() {
        job.cancel()
        _tick.value = 0
        _playerMode.value = PlayerMode.STOPPED
    }
}