package com.hraj9258.celebrareassignment.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hraj9258.celebrareassignment.auth.domain.AuthRepository
import com.hraj9258.celebrareassignment.auth.data.FirebaseAuthRepository
import com.hraj9258.celebrareassignment.auth.presentation.AuthViewModel
import com.hraj9258.celebrareassignment.texteditor.data.repository.FirebaseTextEditorRepository
import com.hraj9258.celebrareassignment.texteditor.domain.TextEditorRepository
import com.hraj9258.celebrareassignment.texteditor.presentation.textEditorScreen.TextEditorViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single {
        FirebaseAuth.getInstance()
    }
    single {
        FirebaseFirestore.getInstance()
    }

    singleOf(::FirebaseAuthRepository).bind<AuthRepository>()
    singleOf(::FirebaseTextEditorRepository).bind<TextEditorRepository>()

    viewModelOf(::AuthViewModel)
    viewModelOf(::TextEditorViewModel)
}
