package com.hraj9258.celebrareassignment.texteditor.domain

import com.hraj9258.celebrareassignment.texteditor.presentation.textEditorScreen.PageState

interface TextEditorRepository {
    suspend fun loadPages(userId: String): List<PageState>?
    suspend fun savePages(userId: String, pages: List<PageState>)
}