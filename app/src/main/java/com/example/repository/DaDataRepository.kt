package com.example.repository

import com.example.data.models.DaDataResponse
import com.example.data.models.SearchAddressModel
import io.reactivex.Single

interface DaDataRepository {
    fun suggestCity(query: String, count: Int): Single<SearchAddressModel>
}