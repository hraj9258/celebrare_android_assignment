package com.hraj9258.celebrareassignment.texteditor.domain

import androidx.compose.ui.text.font.FontFamily


enum class TextAlignOption { Left, Center, Right }

data class TextItem(
    val id: Int,
    val text: String,
    val offsetX: Float,
    val offsetY: Float,
    val fontSize: Float,
    val isBold: Boolean,
    val isItalic: Boolean,
    val isUnderline: Boolean,
    val textAlign: TextAlignOption,
    val fontFamily: FontFamily
)