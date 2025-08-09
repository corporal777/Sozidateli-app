package com.example.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat


fun <T, R> Flow<T>.flatMap(transform: suspend (value: T) -> Flow<R>): Flow<R> {
    return this.flatMapConcat(transform)
}
