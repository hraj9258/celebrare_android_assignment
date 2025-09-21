package com.hraj9258.celebrareassignment.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hraj9258.celebrareassignment.domain.TextAlignOption
import com.hraj9258.celebrareassignment.domain.TextItem
import kotlin.math.roundToInt
import com.hraj9258.celebrareassignment.R

@Composable
fun TextEditorScreen(
    modifier: Modifier = Modifier
) {
    val undoStack = remember { mutableStateListOf<List<TextItem>>() }
    val redoStack = remember { mutableStateListOf<List<TextItem>>() }
    var textItems by remember { mutableStateOf(listOf<TextItem>()) }
    var selectedId by remember { mutableStateOf<Int?>(null) }
    var currentId by remember { mutableIntStateOf(0) }

    var editingText by remember { mutableStateOf<String?>(null) } // dialog state

    val availableFonts = listOf(
        "Default" to FontFamily.Default,
        "Serif" to FontFamily.Serif,
        "Monospace" to FontFamily.Monospace,
        "Cursive" to FontFamily.Cursive,
        "SansSerif" to FontFamily.SansSerif
    )

    fun saveState() {
        undoStack.add(textItems.map { it.copy() }) // snapshot
        redoStack.clear()
    }

    Column(modifier = modifier
        .fillMaxSize()
    ) {

        // Undo/Redo Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if (undoStack.isNotEmpty()) {
                    textItems = undoStack.last()
//                    redoStack.add(undoStack.removeLast())
                    redoStack.add(undoStack.removeAt(undoStack.lastIndex))
                }
            }) { Text("Undo") }

            Button(onClick = {
                if (redoStack.isNotEmpty()) {
//                    val state = redoStack.removeLast()
                    val state = redoStack.removeAt(redoStack.lastIndex)
                    undoStack.add(state)
                    textItems = state
                }
            }) { Text("Redo") }
        }

        // Canvas Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Gray)
        ) {
            textItems.forEach { item ->
                DraggableText(
                    item = item,
                    isSelected = selectedId == item.id,
                    onPositionChange = { newX, newY ->
                        textItems = textItems.map {
                            if (it.id == item.id) it.copy(offsetX = newX, offsetY = newY) else it
                        }
                        saveState()
                    },
                    onSelect = { selectedId = item.id },
                    onEditText = { oldText ->
                        selectedId = item.id
                        editingText = oldText
                    }
                )
            }
        }

        // 🛠 Toolbar
        selectedId?.let { selId ->
            val selectedItem = textItems.find { it.id == selId }
            if (selectedItem != null) {
                Column(
                    modifier = Modifier
                        .background(Color(0xFFFAFAFA))
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    // Font Size & Style
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(
                            onClick = { expanded = true },
                            modifier = Modifier
                        ) {
                            Text("Font: " + availableFonts.find { it.second == selectedItem.fontFamily }?.first)
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }) {
                                availableFonts.forEach { (name, font) ->
                                    DropdownMenuItem(
                                        text = { Text(name) },
                                        onClick = {
                                            textItems = textItems.map {
                                                if (it.id == selId) it.copy(fontFamily = font) else it
                                            }
                                            saveState()
                                            expanded = false
                                        },
                                    )
                                }
                            }
                        }

                        Button(onClick = {
                            textItems = textItems.map {
                                if (it.id == selId) it.copy(fontSize = it.fontSize + 2) else it
                            }
                            saveState()
                        }) { Text("+") }

                        Button(onClick = {
                            textItems = textItems.map {
                                if (it.id == selId && it.fontSize > 8) it.copy(fontSize = it.fontSize - 2) else it
                            }
                            saveState()
                        }) { Text("-") }

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            textItems = textItems.map {
                                if (it.id == selId) it.copy(isBold = !it.isBold) else it
                            }
                            saveState()
                        }) { Text("B") }

                        Button(onClick = {
                            textItems = textItems.map {
                                if (it.id == selId) it.copy(isItalic = !it.isItalic) else it
                            }
                            saveState()
                        }) { Text("I") }

                        Button(onClick = {
                            textItems = textItems.map {
                                if (it.id == selId) it.copy(isUnderline = !it.isUnderline) else it
                            }
                            saveState()
                        }) { Text("U") }

                        Button(
                            onClick = {
                                textItems = textItems.map {
                                    if (it.id == selId) {
                                        val nextAlign = when (it.textAlign) {
                                            TextAlignOption.Left -> TextAlignOption.Center
                                            TextAlignOption.Center -> TextAlignOption.Right
                                            TextAlignOption.Right -> TextAlignOption.Left
                                        }
                                        it.copy(textAlign = nextAlign)
                                    } else it
                                }
                                saveState()
                            }
                        ) {
                            val currentAlign = selectedItem.textAlign
                            Icon(
                                painter = when (currentAlign) {
                                    TextAlignOption.Left -> painterResource(R.drawable.format_align_left_24px)
                                    TextAlignOption.Center -> painterResource(R.drawable.format_align_center_24px)
                                    TextAlignOption.Right -> painterResource(R.drawable.format_align_right_24px)
                                },
                                contentDescription = "Text Alignment"
                            )
                        }
                    }
                }
            }

            if (editingText != null && selectedId != null) {
                var newText by remember { mutableStateOf(editingText!!) }

                AlertDialog(
                    onDismissRequest = { editingText = null },
                    title = { Text("Edit Text") },
                    text = {
                        OutlinedTextField(
                            value = newText,
                            onValueChange = { newText = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            textItems = textItems.map {
                                if (it.id == selectedId) it.copy(text = newText) else it
                            }
                            saveState()
                            editingText = null
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { editingText = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        // Add Text
        Button(
            onClick = {
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
                    fontFamily = FontFamily.Default
                )
                textItems = textItems + newText
                selectedId = newText.id
                saveState()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Text")
        }
    }
}


@Composable
fun DraggableText(
    item: TextItem,
    isSelected: Boolean,
    onPositionChange: (Float, Float) -> Unit,
    onSelect: () -> Unit,
    onEditText: (String) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(item.offsetX) }
    var offsetY by remember { mutableFloatStateOf(item.offsetY) }

    Text(
        text = item.text,
        fontSize = item.fontSize.sp,
        fontWeight = if (item.isBold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (item.isItalic) FontStyle.Italic else FontStyle.Normal,
        textDecoration = if (item.isUnderline) TextDecoration.Underline else TextDecoration.None,
        fontFamily = item.fontFamily,
        textAlign = when (item.textAlign) {
            TextAlignOption.Left -> TextAlign.Left
            TextAlignOption.Center -> TextAlign.Center
            TextAlignOption.Right -> TextAlign.Right
        },
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onSelect() },
                    onDoubleTap = { onEditText(item.text) }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        onPositionChange(offsetX, offsetY)
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
//                    onPositionChange(offsetX, offsetY)
                }
            }
            .background(if (isSelected) Color.Yellow.copy(alpha = 0.3f) else Color.Transparent)
            .padding(4.dp)
    )
}


@Preview
@Composable
private fun TextEditorScreenPreview() {
    TextEditorScreen()
}

