package temu.monitorzdrowia.model.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import temu.monitorzdrowia.model.entities.Mood
import temu.monitorzdrowia.model.entities.User

@Dao
interface MoodDao {

    @Insert
    suspend fun insertMood(mood: Mood)

    @Delete
    suspend fun deleteMood(mood: Mood)

    // Sortowanie po dacie malejąco
    @Query("SELECT * FROM Mood ORDER BY timestamp DESC")
    fun orderByDateDescending(): Flow<List<Mood>>

    // Sortowanie po dacie rosnąco
    @Query("SELECT * FROM Mood ORDER BY timestamp ASC")
    fun orderByDateAscending(): Flow<List<Mood>>

    // Sortowanie po ocenie malejąco
    @Query("SELECT * FROM Mood ORDER BY MoodRating DESC")
    fun orderByRatingDescending(): Flow<List<Mood>>

    // Sortowanie po ocenie rosnąco
    @Query("SELECT * FROM Mood ORDER BY MoodRating ASC")
    fun orderByRatingAscending(): Flow<List<Mood>>



    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM User LIMIT 1")
    fun getUser(): Flow<User?>
}
