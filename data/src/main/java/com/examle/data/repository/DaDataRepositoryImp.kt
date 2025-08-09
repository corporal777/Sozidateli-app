package com.examle.data.repository

import com.examle.data.api.Api
import com.examle.domain.repository.DaDataRepository
import javax.inject.Inject

class DaDataRepositoryImp
@Inject constructor(
        private val apiDataData: Api
) : DaDataRepository {

    //override fun suggestCity(query: String, count: Int) = apiDataData.searchAddress(query, count)
}