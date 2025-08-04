package com.example.navigation

sealed class Route(val route: String) {
    data object Authorization : Route(route = "authorization")

}