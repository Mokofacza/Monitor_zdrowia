package temu.monitorzdrowia.ui.build

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import temu.monitorzdrowia.SortType
import java.time.format.DateTimeFormatter

@Composable
fun MoodScreen(
    state: MoodState, // Aktualny stan UI
    onEvent: (MoodEvent) -> Unit // Funkcja do obsługi zdarzeń
) {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm") // Formatter do daty

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(MoodEvent.ShowDialog) // Pokazuje dialog dodawania nastroju
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Dodaj") // Ikona dodawania
            }
        },
        modifier = Modifier.padding(16.dp) // Padding wokół Scaffold
    ) { padding ->
        if (state.isAddingMood) {
            AddMoodDialog(state = state, onEvent = onEvent) // Wyświetla dialog, jeśli jest aktywny
        }
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .horizontalScroll(rememberScrollState()), // Scrollowanie poziome
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SortType.values().forEach { sortType ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onEvent(MoodEvent.SortMood(sortType)) // Zmiana typu sortowania
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.sortType == sortType, // Zaznaczenie aktualnego sortowania
                                onClick = {
                                    onEvent(MoodEvent.SortMood(sortType)) // Zmiana sortowania
                                }
                            )
                            Text(text = sortType.name) // Nazwa typu sortowania
                        }
                    }
                }
            }
            items(state.mood) { mood ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp), // Padding pionowy
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ocena: ${mood.moodRating}",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp)) // Spacer między elementami
                        Text(
                            text = "Opis: ${mood.note}",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Data: ${mood.timestamp.format(formatter)}",
                            fontSize = 14.sp
                        )
                    }
                    IconButton(onClick = {
                        onEvent(MoodEvent.DeleteMood(mood)) // Usuwanie nastroju
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Usuń") // Ikona usuwania
                    }
                }
            }
        }
    }
}
