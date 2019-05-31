package com.example.repository

import com.example.data.models.DataDataResponse
import io.reactivex.Completable
import io.reactivex.Single

interface DataDataRepository {
    fun suggestCity(query:String):Single<DataDataResponse>
}