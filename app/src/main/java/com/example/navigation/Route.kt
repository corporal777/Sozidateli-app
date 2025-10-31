package com.example.navigation

sealed class Route(val route: String) {

    data object HomeScreen : Route(route = "home")

    data object AuthorizationScreen : Route(route = "authorization")

    data object LoginScreen : Route(route = "login")

    data object RegistrationScreen : Route(route = "registration")

    data object EventDetailScreen : Route(route = "event-detail/{id}") {
        fun createWay(id: String): String = "event-detail/$id"
    }
}