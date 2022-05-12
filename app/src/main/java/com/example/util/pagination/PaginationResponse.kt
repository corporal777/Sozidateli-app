package com.example.util.pagination

data class PaginationResponse<T>(
        val totalCount: Int?,
        val data: List<T>
)