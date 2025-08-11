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

inline fun <T> Iterable<T>.findItem(predicate: (T) -> Boolean): Pair<Int, T?> {
    for ((index, item) in this.withIndex()) {
        if (predicate(item))
            return Pair(index, item)
    }
    return Pair(-1, null)
}
