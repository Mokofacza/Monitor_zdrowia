// MainActivity.kt
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
                val state by viewModel.state.collectAsState()
                MoodScreen(state = state, onEvent = viewModel::onEvent)
            }
        }
    }
}
