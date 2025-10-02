package com.hraj9258.celebrareassignment.navigation.domain

import kotlinx.serialization.Serializable

sealed interface NavigationRoute {
    @Serializable
    data object Loading : NavigationRoute

    @Serializable
    data object SignIn : NavigationRoute

    @Serializable
    data object SignUp : NavigationRoute

    @Serializable
    data object Home : NavigationRoute

}