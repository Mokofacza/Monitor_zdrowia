package temu.monitorzdrowia

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Mood(
    val moodRating: Int,
    val note: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
