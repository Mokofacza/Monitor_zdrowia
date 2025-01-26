package temu.monitorzdrowia.model.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import temu.monitorzdrowia.model.entities.Mood
import temu.monitorzdrowia.model.entities.User

// Ten plik definiuje główną klasę bazy danych Room dla aplikacji,
// która zawiera tabele Mood oraz User, a także konwertery typów niestandardowych.
@Database(
    entities = [Mood::class, User::class],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class MoodDatabase : RoomDatabase() {
    abstract val dao: MoodDao
}
