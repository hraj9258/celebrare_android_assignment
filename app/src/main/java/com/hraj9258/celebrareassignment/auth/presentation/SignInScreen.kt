package com.hraj9258.celebrareassignment.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
    onNavigateToSignUp: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SignInScreen(
        modifier = modifier,
        state = state,
        onAction = {action -> viewModel.onAction(action) },
        onNavigateToSignUp = onNavigateToSignUp
    )

}

@Composable
fun SignInScreen(
    state: AuthState,
    modifier: Modifier = Modifier,
    onAction: (AuthAction) -> Unit,
    onNavigateToSignUp: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign In", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = state.email,
            onValueChange = { onAction(AuthAction.OnEmailChange(it))},
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = { onAction(AuthAction.OnPasswordChange(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(text = state.error, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(16.dp))
        Button(
            enabled = !state.loading,
            onClick = { onAction(AuthAction.OnSignIn) },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (state.loading) {
                CircularProgressIndicator()
            } else {
                Text("Sign In")
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Don't have an account? Sign up",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(enabled = !state.loading) { onNavigateToSignUp() })
    }
}

@Preview
@Composable
private fun SignInScreenPreview() {
    MaterialTheme {
        SignInScreen(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            state = AuthState(),
            onAction = {}
        )
    }
}