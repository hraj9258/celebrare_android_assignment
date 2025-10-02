package com.hraj9258.celebrareassignment.auth.presentation

data class AuthState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val isSignedIn: Boolean = false,
    val initialized: Boolean = false,
)