package com.example.repository

import com.example.api.Api
import javax.inject.Inject

class DaDataRepositoryImp
@Inject constructor(
        private val apiDataData: Api
) : DaDataRepository {

    override fun suggestCity(query: String, count: Int) = apiDataData.searchAddress(query, count)
}