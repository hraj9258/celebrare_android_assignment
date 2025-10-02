package com.hraj9258.celebrareassignment.navigation.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hraj9258.celebrareassignment.navigation.domain.NavigationRoute
import com.hraj9258.celebrareassignment.auth.presentation.AuthViewModel
import com.hraj9258.celebrareassignment.auth.presentation.SignInScreenRoot
import com.hraj9258.celebrareassignment.auth.presentation.SignUpScreenRoot
import com.hraj9258.celebrareassignment.texteditor.presentation.textEditorScreen.TextEditorScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = koinViewModel()
    val authState by authViewModel.state.collectAsStateWithLifecycle()

    // Navigate based on auth state changes (sign-in or sign-out)
    LaunchedEffect(authState.isSignedIn, authState.initialized) {
        if (authState.isSignedIn) {
            navController.navigate(NavigationRoute.Home) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        } else if (authState.initialized) {
            navController.navigate(NavigationRoute.SignIn) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Loading,
        modifier = modifier
    ) {
        composable<NavigationRoute.Loading> {
            // Redirect after initial auth check (persisted session)
            LaunchedEffect(authState.initialized) {
                if (authState.initialized) {
                    if (authState.isSignedIn) {
                        navController.navigate(NavigationRoute.Home) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(NavigationRoute.SignIn) {
                            popUpTo<NavigationRoute.Loading> { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        composable<NavigationRoute.SignIn> {
            SignInScreenRoot(
                onNavigateToSignUp = {
                    navController.navigate(NavigationRoute.SignUp) {
                        popUpTo<NavigationRoute.SignIn> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<NavigationRoute.SignUp> {
            SignUpScreenRoot(
                onNavigateToSignIn = {
                    navController.navigate(NavigationRoute.SignIn) {
                        popUpTo<NavigationRoute.SignUp> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<NavigationRoute.Home> {
            TextEditorScreen(
                onLogout = {
                    authViewModel.signOut()
                }
            )
        }
    }
}