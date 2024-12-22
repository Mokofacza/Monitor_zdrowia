package temu.monitorzdrowia.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import temu.monitorzdrowia.data.models.Mood

// Ten plik definiuje główną klasę bazy danych Room dla aplikacji,
// która zawiera tabelę Mood oraz konwertery typów niestandardowych.
@Database(
    entities = [Mood::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MoodDatabase: RoomDatabase(){
    abstract val dao: MoodDao // Abstrakcyjna właściwość DAO, Room automatycznie wygeneruje implementację

}