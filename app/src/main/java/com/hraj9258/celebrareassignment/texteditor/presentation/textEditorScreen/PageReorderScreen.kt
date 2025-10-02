package com.hraj9258.celebrareassignment.texteditor.presentation.textEditorScreen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hraj9258.celebrareassignment.texteditor.presentation.textEditorScreen.PageState
import com.hraj9258.celebrareassignment.core.ui.theme.CelebrareAssignmentTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun PageReorderScreen(
    pages: List<PageState>,
    onMove: (Int, Int) -> Unit,
    onDone: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState =
        rememberReorderableLazyListState(lazyListState,
            onMove ={ from, to ->
                onMove(from.index, to.index)
            })


    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            Text(
                "Reorder Pages",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                ,
                state =  lazyListState,
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(pages, key = {_, page-> page.id}) { index, page ->
                    ReorderableItem(reorderableLazyColumnState, page.id) {
                        val interactionSource = remember { MutableInteractionSource() }

                        Card(
                            onClick = {},
                            modifier = Modifier
                                .height(64.dp),
                            interactionSource = interactionSource,
                        ) {
                            Row(
                                Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(text = "Page $index", Modifier.padding(horizontal = 8.dp))
                                IconButton(
                                    modifier = Modifier
                                        .draggableHandle(
                                            onDragStarted = {
//                                                haptic.performHapticFeedback(ReorderHapticFeedbackType.START)
                                            },
                                            onDragStopped = {
//                                                haptic.performHapticFeedback(ReorderHapticFeedbackType.END)
                                            },
                                            interactionSource = interactionSource,
                                        ),
                                    onClick = {},
                                ) {
                                    Icon(Icons.Rounded.DragHandle, contentDescription = "Reorder")
                                }
                            }
                        }

                    }
                }
            }
            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Done")
            }
        }
    }
}

@Preview
@Composable
private fun PageReorderScreenPreview() {
    CelebrareAssignmentTheme {
        PageReorderScreen(
            pages = emptyList(),
            onMove = { x, y ->
            },
            onDone = {}
        )
    }
}
