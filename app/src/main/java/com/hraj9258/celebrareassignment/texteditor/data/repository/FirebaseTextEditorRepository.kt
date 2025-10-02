package com.hraj9258.celebrareassignment.texteditor.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hraj9258.celebrareassignment.texteditor.data.dto.TextEditorDocumentDto
import com.hraj9258.celebrareassignment.texteditor.data.dto.toDomain
import com.hraj9258.celebrareassignment.texteditor.data.dto.toDto
import com.hraj9258.celebrareassignment.texteditor.domain.TextEditorRepository
import com.hraj9258.celebrareassignment.texteditor.presentation.textEditorScreen.PageState
import kotlinx.coroutines.tasks.await

class FirebaseTextEditorRepository(
    private val firestore: FirebaseFirestore,
) : TextEditorRepository {

    private fun docRef(userId: String) =
        firestore.collection("users").document(userId)
            .collection("text_editor").document("current")

    override suspend fun loadPages(userId: String): List<PageState>? {
        val snap = docRef(userId).get().await()
        if (!snap.exists()) return null
        val dto = snap.toObject(TextEditorDocumentDto::class.java) ?: return null
        return dto.pages?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun savePages(userId: String, pages: List<PageState>) {
        val dto = TextEditorDocumentDto(
            pages = pages.map { it.toDto() }
        )
        docRef(userId).set(dto).await()
    }
}