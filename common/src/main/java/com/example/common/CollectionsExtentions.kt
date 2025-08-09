package com.example.common

inline fun <T, K> Iterable<T>.groupByNotNull(keySelector: (T) -> K?): Map<K, List<T>> {
    return groupByNotNullTo(linkedMapOf(), keySelector)
}

inline fun <T, K, M : MutableMap<in K, MutableList<T>>> Iterable<T>.groupByNotNullTo(destination: M, keySelector: (T) -> K?): M {
    forEach { child ->
        val key = keySelector(child)
        if (key != null) {
            destination.getOrPut(key) { mutableListOf() }.apply {
                add(child)
            }
        }
    }

    return destination
}
