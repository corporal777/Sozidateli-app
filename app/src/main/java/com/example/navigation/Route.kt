package com.example.navigation

sealed class Route(val route: String) {
    data object Auth : Route(route = "auth")
    data object AuthorizationScreen : Route(route = "authorization")
    data object LoginScreen : Route(route = "login")

    data object Content : Route(route = "content")
    data object HomeScreen : Route(route = "home")

}