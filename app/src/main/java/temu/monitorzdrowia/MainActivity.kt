package temu.monitorzdrowia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import temu.monitorzdrowia.data.local.MoodDatabase
import temu.monitorzdrowia.ui.build.MoodScreen
import temu.monitorzdrowia.ui.build.MoodViewModel
import temu.monitorzdrowia.ui.theme.MonitorZdrowiaTheme

class MainActivity : ComponentActivity() {

    // Inicjalizacja bazy danych Room jako lazy, aby była utworzona tylko wtedy, gdy jest potrzebna
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            MoodDatabase::class.java, // Klasa bazy danych Room
            "mood.db" // Nazwa pliku bazy danych
        ).build()
    }

    // Inicjalizacja ViewModel za pomocą fabryki, aby przekazać DAO do ViewModelu
    private val viewModel: MoodViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MoodViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MoodViewModel(db.dao) as T // Przekazanie DAO do ViewModelu
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Ustawienie motywu aplikacji
            MonitorZdrowiaTheme {
                // Obserwowanie stanu ViewModelu jako State w Compose
                val state by viewModel.state.collectAsState()
                // Wyświetlenie głównego ekranu aplikacji z przekazaniem stanu i funkcji obsługi zdarzeń
                MoodScreen(state = state, onEvent = viewModel::onEvent)
            }
        }
    }
}
