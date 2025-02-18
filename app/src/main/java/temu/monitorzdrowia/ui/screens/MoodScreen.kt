package temu.monitorzdrowia.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import temu.monitorzdrowia.model.SortType
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.platform.LocalContext
import temu.monitorzdrowia.model.entities.Mood
import temu.monitorzdrowia.viewmodel.MoodState
import temu.monitorzdrowia.viewmodel.MoodViewModel
import temu.monitorzdrowia.viewmodel.ProfileViewModel
import temu.monitorzdrowia.viewmodel.UiEvent
import temu.monitorzdrowia.ui.components.AddMoodDialog
import temu.monitorzdrowia.ui.components.GeminiDialog
import temu.monitorzdrowia.ui.components.MoodChartDialog
import temu.monitorzdrowia.viewmodel.MoodEvent

/**
 * Ekran wyświetlający listę nastrojów użytkownika, umożliwiający dodawanie, sortowanie,
 * analizowanie oraz wyświetlanie wykresów nastrojów.
 *
 * @param state Aktualny stan UI związany z nastrojami.
 * @param onEvent Funkcja do obsługi zdarzeń generowanych przez ekran.
 * @param viewModel ViewModel zarządzający logiką nastrojów.
 * @param profileViewModel ViewModel zarządzający logiką profilu użytkownika.
 */


@Composable
fun MoodScreen(
    state: MoodState,
    onEvent: (MoodEvent) -> Unit,
    viewModel: MoodViewModel,
    profileViewModel: ProfileViewModel
) {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
    val context = LocalContext.current

    // Stan lokalny do śledzenia, która karta jest obecnie kliknieta
    var expandedMoodId by remember { mutableStateOf<Int?>(null) }

    // Stan do śledzenia, który nastrój ma zostać usunięty
    var moodToDelete by remember { mutableStateOf<Mood?>(null) }

    // Obsługa zdarzeń UiEvent
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Pobranie danych użytkownika z ProfileViewModel
    val profileState by profileViewModel.state.collectAsState()
    val user = profileState.user

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
        floatingActionButton = {
            var menuExpanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .offset(y = (-16).dp)
            ) {
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
                    DropdownMenuItem(
                        text = { Text("Pokaż wykres nastroju") },
                        onClick = {
                            menuExpanded = false
                            onEvent(MoodEvent.ShowChart)
                        }
                    )
                }
            }
        },
        content = { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    contentPadding = padding,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.mood) { mood ->
                        val isExpanded = expandedMoodId == mood.id

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                .clickable {
                                    expandedMoodId = if (isExpanded) null else mood.id
                                },
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
                                    moodToDelete = mood
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

                // Dialog wykresu nastroju
                if (state.isChartVisible) {
                    MoodChartDialog(
                        moodEntries = state.moodEntries,
                        labels = state.mood.map { it.timestamp.format(DateTimeFormatter.ofPattern("dd-MM")) },
                        onDismiss = { onEvent(MoodEvent.HideChart) }
                    )
                }

                // Dialog potwierdzający usunięcie
                if (moodToDelete != null) {
                    AlertDialog(
                        onDismissRequest = { moodToDelete = null },
                        title = { Text(text = "Potwierdź usunięcie") },
                        text = { Text(text = "Czy na pewno chcesz usunąć ten wpis?") },
                        confirmButton = {
                            Button(onClick = {
                                moodToDelete?.let { onEvent(MoodEvent.DeleteMood(it)) }
                                moodToDelete = null
                            }) {
                                Text("Tak")
                            }
                        },
                        dismissButton = {
                            Button(onClick = {
                                moodToDelete = null
                            }) {
                                Text("Nie")
                            }
                        }
                    )
                }

                // Dialog dodawania nastroju
                if (state.isAddingMood) {
                    AddMoodDialog(state = state, onEvent = onEvent)
                }

                // Dialog analizy nastroju
                if (state.isAnalyzingMood) {
                    if (user != null) {
                        // Jeśli user != null, wyświetl standardowy dialog
                        GeminiDialog(
                            moodHistory = state.mood,
                            analysisResult = state.analysisResult,
                            onAnalyze = { selectedMoods, user ->
                                onEvent(MoodEvent.AnalyzeMood(selectedMoods, user))
                            },
                            onDismiss = { onEvent(MoodEvent.HideAnalysisDialog) },
                            onShake = {
                                onEvent(MoodEvent.ResetAnalysisResult)
                            },
                            userProfile = user
                        )
                    } else {
                        // Jeśli user == null, wyświetl Toast i schowaj dialog
                        Toast.makeText(
                            context,
                            "Uzupełnij dane użytkownika w profilu!",
                            Toast.LENGTH_SHORT
                        ).show()
                        onEvent(MoodEvent.HideAnalysisDialog)
                    }
                }
            }
        }
    )
}