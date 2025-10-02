package com.hraj9258.celebrareassignment.texteditor.data.dto

import androidx.compose.ui.text.font.FontFamily
import com.hraj9258.celebrareassignment.texteditor.domain.TextAlignOption
import com.hraj9258.celebrareassignment.texteditor.domain.TextItem
import com.hraj9258.celebrareassignment.texteditor.presentation.textEditorScreen.PageState


data class TextEditorDocumentDto(
    val pages: List<PageDto>? = null
)

data class PageDto(
    val id: Int? = null,
    val items: List<TextItemDto>? = null
)

data class TextItemDto(
    val id: Int? = null,
    val text: String? = null,
    val offsetX: Float? = null,
    val offsetY: Float? = null,
    val fontSize: Float? = null,
    val isBold: Boolean? = null,
    val isItalic: Boolean? = null,
    val isUnderline: Boolean? = null,
    val textAlign: String? = null,
    val fontFamily: String? = null
)

// Mappers

fun PageState.toDto(): PageDto = PageDto(
    id = id,
    items = textItems.map { it.toDto() }
)

fun PageDto.toDomain(): PageState = PageState(
    id = id ?: 0,
    textItems = (items ?: emptyList()).map { it.toDomain() },
    undoStack = emptyList(),
    redoStack = emptyList()
)

fun TextItem.toDto(): TextItemDto = TextItemDto(
    id = id,
    text = text,
    offsetX = offsetX,
    offsetY = offsetY,
    fontSize = fontSize,
    isBold = isBold,
    isItalic = isItalic,
    isUnderline = isUnderline,
    textAlign = textAlign.name,
    fontFamily = fontFamilyToName(fontFamily)
)

fun TextItemDto.toDomain(): TextItem = TextItem(
    id = id ?: 0,
    text = text ?: "",
    offsetX = offsetX ?: 0f,
    offsetY = offsetY ?: 0f,
    fontSize = fontSize ?: 16f,
    isBold = isBold ?: false,
    isItalic = isItalic ?: false,
    isUnderline = isUnderline ?: false,
    textAlign = when (textAlign) {
        TextAlignOption.Center.name -> TextAlignOption.Center
        TextAlignOption.Right.name -> TextAlignOption.Right
        else -> TextAlignOption.Left
    },
    fontFamily = nameToFontFamily(fontFamily)
)

private fun fontFamilyToName(fontFamily: FontFamily): String = when (fontFamily) {
    FontFamily.Default -> "Default"
    FontFamily.Serif -> "Serif"
    FontFamily.Monospace -> "Monospace"
    FontFamily.Cursive -> "Cursive"
    FontFamily.SansSerif -> "SansSerif"
    else -> "Default"
}

private fun nameToFontFamily(name: String?): FontFamily = when (name) {
    "Serif" -> FontFamily.Serif
    "Monospace" -> FontFamily.Monospace
    "Cursive" -> FontFamily.Cursive
    "SansSerif" -> FontFamily.SansSerif
    else -> FontFamily.Default
}
