package com.hraj9258.celebrareassignment.texteditor.presentation.textEditorScreen

import com.hraj9258.celebrareassignment.texteditor.domain.TextItem

data class PageState(
    val id : Int,
    val textItems: List<TextItem> = emptyList(),
    val undoStack: List<List<TextItem>> = emptyList(),
    val redoStack: List<List<TextItem>> = emptyList()
)