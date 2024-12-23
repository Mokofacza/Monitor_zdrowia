package temu.monitorzdrowia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import temu.monitorzdrowia.data.local.MoodDatabase
import temu.monitorzdrowia.navigation.AppNavGraph
import temu.monitorzdrowia.ui.components.TopBar
import temu.monitorzdrowia.ui.build.AddMoodDialog
import temu.monitorzdrowia.ui.build.MoodEvent
import temu.monitorzdrowia.ui.build.MoodViewModel
import temu.monitorzdrowia.ui.theme.MonitorZdrowiaTheme

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            MoodDatabase::class.java,
            "mood.db"
        ).build()
    }

    private val viewModel: MoodViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MoodViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MoodViewModel(db.dao) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonitorZdrowiaTheme {
                val navController = rememberNavController()
                val state by viewModel.state.collectAsState()

                Scaffold(
                    topBar = { TopBar(navController = navController) },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            viewModel.onEvent(MoodEvent.ShowDialog)
                        }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Dodaj")
                        }
                    },
                    content = { padding ->
                        Box(modifier = Modifier.padding(padding)) {
                            AppNavGraph(navController = navController, viewModel = viewModel)
                            if (state.isAddingMood) {
                                AddMoodDialog(state = state, onEvent = viewModel::onEvent)
                            }
                        }
                    }
                )
            }
        }
    }
}
