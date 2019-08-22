package com.example.repository

import com.example.data.models.DaDataResponse
import io.reactivex.Single

interface DaDataRepository {
    fun suggestCity(query: String): Single<DaDataResponse>
}