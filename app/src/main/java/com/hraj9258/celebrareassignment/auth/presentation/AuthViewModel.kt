package com.hraj9258.celebrareassignment.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hraj9258.celebrareassignment.auth.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state
        .onStart {
            getAuthState()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _state.value
        )

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.OnSignIn -> signIn()
            is AuthAction.OnSignUp -> signUp()
            is AuthAction.OnEmailChange -> { onEmailChange(action.email) }
            is AuthAction.OnNameChange -> { onNameChange(action.name) }
            is AuthAction.OnPasswordChange -> { onPasswordChange(action.password) }
        }
    }

    private fun getAuthState() = viewModelScope.launch {
        _state.update { it.copy(loading = true) }
        repository.isSignedIn.collectLatest { signedIn ->
            _state.update {
                it.copy(
                    isSignedIn = signedIn,
                    initialized = true,
                    loading = false,
                    error = null
                )
            }
        }
    }

    fun onNameChange(value: String) = _state.update { it.copy(name = value, error = null) }
    fun onEmailChange(value: String) = _state.update { it.copy(email = value.trim(), error = null) }
    fun onPasswordChange(value: String) = _state.update { it.copy(password = value, error = null) }

    fun signIn() {
        val current = _state.value
        if (!isValidEmail(current.email)) {
            _state.update { it.copy(error = "Please enter a valid email address") }
            return
        }
        if (current.password.length < 6) {
            _state.update { it.copy(error = "Password must be at least 6 characters") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            val result = repository.signIn(current.email, current.password)
            _state.update { s ->
                s.copy(loading = false, error = result.exceptionOrNull()?.toUserMessage())
            }
        }
    }

    fun signUp() {
        val current = _state.value
        if (current.name.isBlank()) {
            _state.update { it.copy(error = "Please enter your name") }
            return
        }
        if (!isValidEmail(current.email)) {
            _state.update { it.copy(error = "Please enter a valid email address") }
            return
        }
        if (current.password.length < 6) {
            _state.update { it.copy(error = "Password must be at least 6 characters") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            val result = repository.signUp(current.name, current.email, current.password)
            _state.update { s ->
                s.copy(loading = false, error = result.exceptionOrNull()?.toUserMessage())
            }
        }
    }

    fun signOut() {
        repository.signOut()
        _state.update { AuthState(initialized = true) }
        val x =100
    }

    private fun isValidEmail(email: String): Boolean =
        email.contains('@') && email.substringAfter('@').contains('.')
}

private fun Throwable.toUserMessage(): String = when (message) {
    null -> "Something went wrong"
    else -> this.message ?: "Something went wrong"
}