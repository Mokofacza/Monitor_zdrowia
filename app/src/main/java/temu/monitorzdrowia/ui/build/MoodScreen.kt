package temu.monitorzdrowia.ui.build

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    // Stan lokalny do śledzenia, która karta jest obecnie kliknieta
    var expandedMoodId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.secondary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onEvent(MoodEvent.SortMood(SortType.RATING)) }
                    ) {
                        Text(text = "Od najlepszych")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onEvent(MoodEvent.SortMood(SortType.RATING1)) }
                    ) {
                        Text(text = "Od najgorszych")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onEvent(MoodEvent.SortMood(SortType.TIME)) }
                    ) {
                        Text(text = "Od najnowszych")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onEvent(MoodEvent.SortMood(SortType.TIME1)) }
                    ) {
                        Text(text = "Od najstarszych")
                    }
                }
            }
        },
        floatingActionButton = { //issue #3
            var menuExpanded by remember { mutableStateOf(false) }

            Box (
                modifier = Modifier
                    .offset(y = (-16).dp) // Przesunięcie o 16dp w górę
            ){
                FloatingActionButton(
                    onClick = { menuExpanded = !menuExpanded }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu"
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Dodaj nastrój") },
                        onClick = {
                            menuExpanded = false
                            onEvent(MoodEvent.ShowDialog)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Analiza nastroju") },
                        onClick = {
                            menuExpanded = false
                            onEvent(MoodEvent.ShowAnalysisDialog)
                        }
                    )
                }
            }
        },
        content = { padding ->
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.mood) { mood ->
                    // Sprawdzamy, czy dany element jest obecnie klikniety
                    val isExpanded = expandedMoodId == mood.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            // Kliknięcie w kartę będzie przełączało stan rozszerzenia
                            .clickable {
                                expandedMoodId = if (isExpanded) null else mood.id
                            },
                        // Jeżeli jest rozszerzona
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpanded) {
                                MaterialTheme.colorScheme.tertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.secondaryContainer
                            }
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


                                // wyświetlamy pierwsze 50 znaków
                                val shortNote = if (mood.note.length > 50) {
                                    mood.note.take(50) + "..."
                                } else {
                                    mood.note
                                }
                                val description = if (isExpanded) mood.note else shortNote

                                Text(
                                    text = "Opis: $description",
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
                                    tint = MaterialTheme.colorScheme.onBackground
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

    // Dialog do analizy nastroju przy użyciu GeminI
    if (state.isAnalyzingMood) {
        GeminiDialog(
            moodHistory = state.mood, // przekazujemy całą listę, suwak wybierze podzbiór
            analysisResult = state.analysisResult,
            onAnalyze = { selectedMoods ->
                // Przekazujemy wybrane wpisy do logiki analizy.
                // Implementację funkcji analizy możesz zrealizować według własnych potrzeb.
                onEvent(MoodEvent.AnalyzeMood(selectedMoods))
            },
            onDismiss = { onEvent(MoodEvent.HideAnalysisDialog) },
            onShake = {
                // To wywoła event do zresetowania wyniku
                onEvent(MoodEvent.ResetAnalysisResult)}
        )
    }

}
