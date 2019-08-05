package com.example.data.models.user

import com.example.data.models.Interest

data class UserInterests(
        val user: User,
        val interests: Map<Interest, List<Interest>>?
)