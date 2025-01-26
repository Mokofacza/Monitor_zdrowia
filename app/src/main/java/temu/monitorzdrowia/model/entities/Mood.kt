package temu.monitorzdrowia.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity
data class Mood(
    val moodRating: Int, //od 1 do 10
    val note: String,
    val timestamp: LocalDateTime = LocalDateTime.now(), // Automatycznie ustawiamy datę i czas
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
