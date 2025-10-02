package com.hraj9258.celebrareassignment.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.hraj9258.celebrareassignment.auth.domain.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    override val isSignedIn: Flow<Boolean> = callbackFlow {
        // Emit initial state
        trySend(firebaseAuth.currentUser != null)
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        Unit
    }

    override suspend fun signUp(name: String, email: String, password: String): Result<Unit> = runCatching {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = firebaseAuth.currentUser
        if (user != null && name.isNotBlank()) {
            val request = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user.updateProfile(request).await()
        }
        Unit
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}