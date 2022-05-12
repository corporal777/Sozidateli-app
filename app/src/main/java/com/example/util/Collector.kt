package com.example.util

class Collector<T> {

    private val collection = mutableListOf<T>()
    var doOnRelease: Runnable? = null
    var isReleased = false
        private set

    fun add(item: T) {
        if (isReleased) return
        collection.add(item)
    }

    fun release(): List<T> {
        if (isReleased) throw RuntimeException("Collector already released")
        val collection = this.collection.toList()
        this.collection.clear()
        doOnRelease?.run()
        return collection
    }
}