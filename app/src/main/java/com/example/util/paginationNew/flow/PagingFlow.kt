package com.example.util.paginationNew.flow

import androidx.paging.PagingData
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.PagingSourceFactory
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

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