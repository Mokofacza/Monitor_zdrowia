package temu.monitorzdrowia.data.local

import UserDao
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import temu.monitorzdrowia.data.models.Mood
import temu.monitorzdrowia.data.models.User

// Ten plik definiuje główną klasę bazy danych Room dla aplikacji,
// która zawiera tabele Mood oraz User, a także konwertery typów niestandardowych.
@Database(
    entities = [Mood::class, User::class], // Poprawione dodanie klasy User jako drugiej tabeli
    version = 1,
    exportSchema = true // Zalecane, aby zachować schemat bazy danych dla przyszłych migracji
)
@TypeConverters(Converters::class) // Informuje Room o niestandardowych konwerterach typów
abstract class MoodDatabase : RoomDatabase() {
    abstract val moodDao: MoodDao // Abstrakcyjna właściwość DAO dla Mood
    abstract val userDao: UserDao // Abstrakcyjna właściwość DAO dla User
}
