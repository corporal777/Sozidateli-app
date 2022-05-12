package com.example.util.pagination

import androidx.paging.DataSource

abstract class DataSourceFactory<K, V> : DataSource.Factory<K, V>() {

    var source: DataSource<K, V>? = null
        private set

    override fun create(): DataSource<K, V> {
        val source = createDataSource()
        this.source = source
        return source
    }

    abstract fun createDataSource(): DataSource<K, V>
}