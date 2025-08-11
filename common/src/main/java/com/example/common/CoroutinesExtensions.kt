package com.example.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.update


fun <T, R> Flow<T>.flatMap(transform: suspend (value: T) -> Flow<R>): Flow<R> {
    return this.flatMapConcat(transform)
}
