package com.hraj9258.celebrareassignment.texteditor.presentation.textEditorScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hraj9258.celebrareassignment.texteditor.presentation.textEditorScreen.PageState
import com.hraj9258.celebrareassignment.texteditor.domain.TextAlignOption
import com.hraj9258.celebrareassignment.texteditor.domain.TextEditorRepository
import com.hraj9258.celebrareassignment.texteditor.domain.TextItem
import kotlin.collections.plus
import kotlinx.coroutines.launch

class TextEditorViewModel(
    private val auth: FirebaseAuth,
    private val repository: TextEditorRepository
) : ViewModel() {

    private var currentId by mutableIntStateOf(0)
    var pageStates by mutableStateOf(listOf(PageState(currentId)))
        private set

    var selectedId by mutableStateOf<Int?>(null)
        private set

    var showReorderScreen by mutableStateOf(false)

    private var userId: String? = null

    init {
        // Load persisted pages for current user
        userId = auth.currentUser?.uid
        val uid = userId
        if (uid != null) {
            viewModelScope.launch {
                val loaded = repository.loadPages(uid)
                if (loaded != null && loaded.isNotEmpty()) {
                    pageStates = loaded
                    // update currentId to max item id to avoid collisions
                    currentId = loaded.flatMap { it.textItems }.maxOfOrNull { it.id } ?: 0
                }
            }
        }
    }

    val availableFonts = listOf(
        "Default" to FontFamily.Companion.Default,
        "Serif" to FontFamily.Companion.Serif,
        "Monospace" to FontFamily.Companion.Monospace,
        "Cursive" to FontFamily.Companion.Cursive,
        "SansSerif" to FontFamily.Companion.SansSerif,
    )

    fun movePage(from: Int, to: Int) {
        if (from == to) return
        val mutablePages = pageStates.toMutableList()
        val page = mutablePages.removeAt(from)
        mutablePages.add(to, page)
        pageStates = mutablePages
        persist()
    }

    fun onTextSelected(id: Int) {
        selectedId = id
    }

    fun onUndo(currentPage: Int) {
        val currentUndoStack = pageStates[currentPage].undoStack
        if (currentUndoStack.isNotEmpty()) {
            val lastState = currentUndoStack.last()
            val newUndoStack = currentUndoStack.dropLast(1)
            val newRedoStack =
                pageStates[currentPage].redoStack + listOf(pageStates[currentPage].textItems)
            updatePageState(currentPage) {
                it.copy(
                    textItems = lastState,
                    undoStack = newUndoStack,
                    redoStack = newRedoStack
                )
            }
        }
    }

    fun onRedo(currentPage: Int) {
        val currentRedoStack = pageStates[currentPage].redoStack
        if (currentRedoStack.isNotEmpty()) {
            val nextState = currentRedoStack.last()
            val newRedoStack = currentRedoStack.dropLast(1)
            val newUndoStack =
                pageStates[currentPage].undoStack + listOf(pageStates[currentPage].textItems)
            updatePageState(currentPage) {
                it.copy(
                    textItems = nextState,
                    undoStack = newUndoStack,
                    redoStack = newRedoStack
                )
            }
        }
    }

    fun onAddText(currentPage: Int) {
        currentId++
        val newText = TextItem(
            id = currentId,
            text = "New Text",
            offsetX = 50f,
            offsetY = 50f,
            fontSize = 18f,
            isBold = false,
            isItalic = false,
            isUnderline = false,
            textAlign = TextAlignOption.Left,
            fontFamily = FontFamily.Companion.Default
        )
        updatePageState(currentPage) {
            it.copy(textItems = it.textItems + newText)
        }
        selectedId = newText.id
        saveState(currentPage)
    }

    fun onAddPage() {
        pageStates = pageStates + PageState(id = pageStates.size)
        persist()
    }

    fun onTextPositionChanged(pageIndex: Int, itemId: Int, newX: Float, newY: Float) {
        updatePageState(pageIndex) {
            it.copy(textItems = it.textItems.map { textItem ->
                if (textItem.id == itemId) textItem.copy(
                    offsetX = newX,
                    offsetY = newY
                ) else textItem
            })
        }
        saveState(pageIndex)
    }


    fun onFontFamilyChange(currentPage: Int, selId: Int, font: FontFamily) {
        updateItemInPage(currentPage, selId) { it.copy(fontFamily = font) }
        saveState(currentPage)
    }

    fun onFontSizeIncrease(currentPage: Int, selId: Int) {
        updateItemInPage(currentPage, selId) { it.copy(fontSize = it.fontSize + 2) }
        saveState(currentPage)
    }

    fun onFontSizeDecrease(currentPage: Int, selId: Int) {
        updateItemInPage(currentPage, selId) {
            if (it.fontSize > 8) it.copy(fontSize = it.fontSize - 2) else it
        }
        saveState(currentPage)
    }

    fun onBoldChange(currentPage: Int, selId: Int) {
        updateItemInPage(currentPage, selId) { it.copy(isBold = !it.isBold) }
        saveState(currentPage)
    }

    fun onItalicChange(currentPage: Int, selId: Int) {
        updateItemInPage(currentPage, selId) { it.copy(isItalic = !it.isItalic) }
        saveState(currentPage)
    }

    fun onUnderlineChange(currentPage: Int, selId: Int) {
        updateItemInPage(currentPage, selId) { it.copy(isUnderline = !it.isUnderline) }
        saveState(currentPage)
    }

    fun onTextAlignChange(currentPage: Int, selId: Int) {
        updateItemInPage(currentPage, selId) {
            val nextAlign = when (it.textAlign) {
                TextAlignOption.Left -> TextAlignOption.Center
                TextAlignOption.Center -> TextAlignOption.Right
                TextAlignOption.Right -> TextAlignOption.Left
            }
            it.copy(textAlign = nextAlign)
        }
        saveState(currentPage)
    }

    fun onTextChange(currentPage: Int, selId: Int, newText: String) {
        updateItemInPage(currentPage, selId) {
            it.copy(text = newText)
        }
        saveState(currentPage)
    }

    private fun saveState(pageIndex: Int) {
        val currentState = pageStates[pageIndex]
        val newUndoStack = currentState.undoStack + listOf(currentState.textItems)
        updatePageState(pageIndex) {
            it.copy(undoStack = newUndoStack, redoStack = emptyList())
        }
        persist()
    }

    private fun persist() {
        val uid = userId
        if (uid != null) {
            viewModelScope.launch {
                try {
                    repository.savePages(uid, pageStates)
                } catch (_: Exception) {
                    // swallow for now; in production, expose error state
                }
            }
        }
    }

    private fun updatePageState(index: Int, updater: (PageState) -> PageState) {
        pageStates = pageStates.mapIndexed { i, pageState ->
            if (i == index) updater(pageState) else pageState
        }
    }

    private fun updateItemInPage(
        pageIndex: Int,
        itemId: Int,
        updater: (TextItem) -> TextItem
    ) {
        updatePageState(pageIndex) { pageState ->
            pageState.copy(textItems = pageState.textItems.map { item ->
                if (item.id == itemId) updater(item) else item
            })
        }
    }

}