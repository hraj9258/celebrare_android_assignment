package com.hraj9258.celebrareassignment.texteditor.presentation.textEditorScreen


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import com.hraj9258.celebrareassignment.R
import com.hraj9258.celebrareassignment.texteditor.domain.TextAlignOption
import com.hraj9258.celebrareassignment.texteditor.domain.TextItem
import com.hraj9258.celebrareassignment.core.ui.theme.CelebrareAssignmentTheme
import com.hraj9258.celebrareassignment.auth.presentation.AuthViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextEditorScreen(
    modifier: Modifier = Modifier,
    viewModel: TextEditorViewModel = koinViewModel(),
    onLogout: () -> Unit = {}
) {
    val pageStates = viewModel.pageStates
    val pagerState = rememberPagerState(pageCount = { pageStates.size })
    var editingText by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    viewModel.onUndo(pagerState.currentPage)
                }) { Text("Undo") }

                Button(onClick = {
                    viewModel.onRedo(pagerState.currentPage)
                }) { Text("Redo") }

                Button(onClick = onLogout) { Text("Logout") }
            }

            HorizontalPager(
                state = pagerState,
                pageSpacing = 4.dp,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(0.95f)
            ) { pageIndex ->
                val pageState = pageStates[pageIndex]
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(4.dp)
                            .align(Alignment.TopCenter),
                    ){Text(text = "${pageState.id}")}
                    val containerWidth = constraints.maxWidth.toFloat()
                    val containerHeight = constraints.maxHeight.toFloat()
                    pageState.textItems.forEach { item ->
                        DraggableText(
                            item = item,
                            isSelected = viewModel.selectedId == item.id,
                            onPositionChange = { newX, newY ->
                                viewModel.onTextPositionChanged(pageIndex, item.id, newX, newY)
                            },
                            onSelect = { viewModel.onTextSelected(item.id) },
                            onEditText = { oldText ->
                                viewModel.onTextSelected(item.id)
                                editingText = oldText
                            },
                            containerWidth = containerWidth,
                            containerHeight = containerHeight
                        )
                    }
                }
            }

            viewModel.selectedId?.let { selId ->
                val selectedItem = pageStates[pagerState.currentPage].textItems.find { it.id == selId }
                if (selectedItem != null) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        var expanded by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(onClick = { expanded = true }) {
                                Text("Font: " + viewModel.availableFonts.find { it.second == selectedItem.fontFamily }?.first)
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }) {
                                    viewModel.availableFonts.forEach { (name, font) ->
                                        DropdownMenuItem(
                                            text = { Text(name) },
                                            onClick = {
                                                viewModel.onFontFamilyChange(
                                                    pagerState.currentPage,
                                                    selId,
                                                    font
                                                )
                                                expanded = false
                                            },
                                        )
                                    }
                                }
                            }
                            Button(onClick = {
                                viewModel.onFontSizeIncrease(pagerState.currentPage, selId)
                            }) { Text("+") }

                            Button(onClick = {
                                viewModel.onFontSizeDecrease(pagerState.currentPage, selId)
                            }) { Text("-") }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = {
                                viewModel.onBoldChange(pagerState.currentPage, selId)
                            }) { Text("B") }

                            Button(onClick = {
                                viewModel.onItalicChange(pagerState.currentPage, selId)
                            }) { Text("I") }

                            Button(onClick = {
                                viewModel.onUnderlineChange(pagerState.currentPage, selId)
                            }) { Text("U") }

                            Button(onClick = {
                                viewModel.onTextAlignChange(pagerState.currentPage, selId)
                            }) {
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
            }

            if (editingText != null && viewModel.selectedId != null) {
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
                            viewModel.onTextChange(
                                pagerState.currentPage,
                                viewModel.selectedId!!,
                                newText
                            )
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        viewModel.onAddText(pagerState.currentPage)
                    }) {
                        Text("Add Text")
                    }

                    Button(onClick = {
                        viewModel.onAddPage()
                    }) {
                        Text("Add Page")
                    }
                    Button(onClick = {
                        viewModel.showReorderScreen = true
                    }) {
                        Text("Reorder Pages")
                    }

                }
                Text(
                    "No of pages: ${pageStates.size}, Current Page: ${pagerState.currentPage + 1}",
                    color = MaterialTheme.colorScheme.onSurface
                )

            }

        }

        if (viewModel.showReorderScreen) {
            PageReorderScreen(
                pages = viewModel.pageStates,
                onMove = { from, to -> viewModel.movePage(from, to) },
                onDone = { viewModel.showReorderScreen = false }
            )
        }

    }
}

@Composable
fun DraggableText(
    item: TextItem,
    isSelected: Boolean,
    onPositionChange: (Float, Float) -> Unit,
    onSelect: () -> Unit,
    onEditText: (String) -> Unit,
    containerWidth: Float,
    containerHeight: Float,
) {
    var offsetX by remember { mutableFloatStateOf(item.offsetX) }
    var offsetY by remember { mutableFloatStateOf(item.offsetY) }
    var textSize by remember { mutableStateOf(IntSize.Zero) }

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
            .onSizeChanged { textSize = it }
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
                    if (textSize != IntSize.Zero) {
                        val maxOffsetX = (containerWidth - textSize.width).coerceAtLeast(0f)
                        val maxOffsetY = (containerHeight - textSize.height).coerceAtLeast(0f)
                        offsetX = (offsetX + dragAmount.x).coerceIn(0f, maxOffsetX)
                        offsetY = (offsetY + dragAmount.y).coerceIn(0f, maxOffsetY)
                    }
                }
            }
            .background(if (isSelected) Color.Yellow.copy(alpha = 0.3f) else Color.Transparent)
            .padding(4.dp)
    )
}

@Preview
@Composable
private fun TextEditorScreenPreview() {
    CelebrareAssignmentTheme {
        TextEditorScreen()
    }
}
