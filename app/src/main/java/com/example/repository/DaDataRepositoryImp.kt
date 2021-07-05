package com.example.repository

import com.example.api.NewApi
import javax.inject.Inject

class DaDataRepositoryImp
@Inject constructor(
        private val apiDataData: NewApi
) : DaDataRepository {

    override fun suggestCity(query: String, count: Int) = apiDataData.searchAddress(query, count)
}