package temu.monitorzdrowia.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {

    @Insert
    suspend fun insertMood(mood: Mood)

    @Delete
    suspend fun deleteMood(mood: Mood)

    @Query("SELECT * FROM Mood ORDER BY timestamp DESC") // sortowanie po dacie od góry
    fun orderByDateAndTime(): Flow<List<Mood>>

    @Query("SELECT * FROM Mood ORDER BY MoodRating DESC") // Sortowanie malejąco
    fun orderByRating(): Flow<List<Mood>>


}