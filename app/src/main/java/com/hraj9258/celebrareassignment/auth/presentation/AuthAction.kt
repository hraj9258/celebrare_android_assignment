package com.hraj9258.celebrareassignment.auth.presentation

interface AuthAction{
    data object OnSignIn : AuthAction
    data object OnSignUp : AuthAction
    data class OnEmailChange(val email: String) : AuthAction
    data class OnNameChange(val name: String) : AuthAction
    data class OnPasswordChange(val password: String) : AuthAction
}