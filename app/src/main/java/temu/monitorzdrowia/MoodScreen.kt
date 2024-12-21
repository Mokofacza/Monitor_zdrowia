package temu.monitorzdrowia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MoodScreen(
    state: MoodState,
    onEvent: (MoodEvent) -> Unit
){
    Scaffold (
        floatingActionButton = {}
    ){
        padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
//https://www.youtube.com/watch?v=bOd3wO0uFr8&t=1026s&ab_channel=PhilippLackner
 // 32:00
        }
    }
}


