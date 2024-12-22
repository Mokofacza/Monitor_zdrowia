package temu.monitorzdrowia.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import temu.monitorzdrowia.data.models.Mood

// Interfejs MoodDao to nasz Data Access Object (DAO), który definiuje metody do interakcji z bazą danych Room.
// Dzięki DAO możemy w prosty sposób dodawać, usuwać i pobierać dane z tabeli Mood.
@Dao
interface MoodDao {

    @Insert
    suspend fun insertMood(mood: Mood)
    // suspend umożliwia wykonywanie tej operacji w tle.

    @Delete
    suspend fun deleteMood(mood: Mood)

    @Query("SELECT * FROM Mood ORDER BY timestamp DESC") // sortowanie po dacie od góry
    fun orderByDateAndTime(): Flow<List<Mood>> // Zwraca Flow, co oznacza, że możemy obserwować zmiany w czasie rzeczywistym.

    @Query("SELECT * FROM Mood ORDER BY MoodRating DESC") // Sortowanie malejąco
    fun orderByRating(): Flow<List<Mood>>


}