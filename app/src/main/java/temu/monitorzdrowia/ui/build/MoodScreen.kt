package temu.monitorzdrowia.ui.build

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import temu.monitorzdrowia.SortType
import java.time.format.DateTimeFormatter

@Composable
fun MoodScreen(
    state: MoodState,
    onEvent: (MoodEvent) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

    Scaffold(
        // Pasek sortowania znajduje się w topBar, ma ustaloną wysokość i przewijanie poziome.
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // aby w razie nadmiaru elementów pojawiło się przewijanie poziome.
                    Button(
                        onClick = { onEvent(MoodEvent.SortMood(SortType.RATING)) }
                    ) {
                        Text(text = "Ocena od góry")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onEvent(MoodEvent.SortMood(SortType.RATING1)) }
                    ) {
                        Text(text = "Ocena od dołu")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onEvent(MoodEvent.SortMood(SortType.TIME)) }
                    ) {
                        Text(text = "Data - najnowsze")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onEvent(MoodEvent.SortMood(SortType.TIME1)) }
                    ) {
                        Text(text = "Data - najstarsze")
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(MoodEvent.ShowDialog)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Dodaj")
            }
        },
        content = { padding ->
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Wyświetlanie listy nastrojów
                items(state.mood) { mood ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp,horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Ocena: ${mood.moodRating}",
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Opis: ${mood.note}",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Data: ${mood.timestamp.format(formatter)}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            IconButton(onClick = {
                                onEvent(MoodEvent.DeleteMood(mood))
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Usuń",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    )

    if (state.isAddingMood) {
        AddMoodDialog(state = state, onEvent = onEvent)
    }
}
