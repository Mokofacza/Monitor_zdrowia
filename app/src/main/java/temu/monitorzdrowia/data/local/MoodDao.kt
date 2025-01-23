package temu.monitorzdrowia.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import temu.monitorzdrowia.data.models.Mood
import temu.monitorzdrowia.data.models.User

// Interfejs MoodDao to nasz Data Access Object (DAO), który definiuje metody do interakcji z bazą danych Room.
// Dzięki DAO możemy w prosty sposób dodawać, usuwać i pobierać dane z tabeli Mood.
@Dao
interface MoodDao {

    @Insert
    suspend fun insertMood(mood: Mood)

    @Delete
    suspend fun deleteMood(mood: Mood)

    // Sortowanie po dacie od najnowszej do najstarszej (malejąco)
    @Query("SELECT * FROM Mood ORDER BY timestamp DESC")
    fun orderByDateDescending(): Flow<List<Mood>>

    // Sortowanie po dacie od najstarszej do najnowszej (rosnąco)
    @Query("SELECT * FROM Mood ORDER BY timestamp ASC")
    fun orderByDateAscending(): Flow<List<Mood>>

    // Sortowanie po ocenie od najwyższej do najniższej (malejąco)
    @Query("SELECT * FROM Mood ORDER BY MoodRating DESC")
    fun orderByRatingDescending(): Flow<List<Mood>>

    // Sortowanie po ocenie od najniższej do najwyższej (rosnąco)
    @Query("SELECT * FROM Mood ORDER BY MoodRating ASC")
    fun orderByRatingAscending(): Flow<List<Mood>>

    @Insert
    suspend fun insertUser(user: User)
    // suspend umożliwia wykonywanie tej operacji w tle.

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM User LIMIT 1")
    fun getUser(): Flow<User?>
}
