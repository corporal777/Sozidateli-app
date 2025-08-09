package com.example.util.paginationNew.flow

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import com.example.data.models.EventNew
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.PagingSourceFactory
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class PagingFlow <T : Any>(
    private val pagination: Flow<PagingData<T>>,
    private val dataSourceFactory: PagingSourceFactory<T>
) : Flow<PagingData<T>> {


    private lateinit var collector: FlowCollector<PagingData<T>>

    override suspend fun collect(collector: FlowCollector<PagingData<T>>) {
        this.collector = collector
        pagination.collect(collector)
    }


    suspend fun invalidate() {
        if (dataSourceFactory.source == null) return
        if (this::collector.isInitialized) collector.emit(PagingData.empty())
        //dataSourceFactory.source!!.invalidateFromStart()
    }

    fun invalidateStart(){
        if (dataSourceFactory.source == null) return
        //dataSourceFactory.source!!.invalidateFromStart()
    }

    suspend fun invalidateFrom(list: List<T>){
        if (this::collector.isInitialized) collector.emit(PagingData.from(list))
    }
}