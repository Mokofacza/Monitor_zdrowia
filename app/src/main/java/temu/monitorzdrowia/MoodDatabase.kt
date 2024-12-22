package temu.monitorzdrowia

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Mood::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MoodDatabase: RoomDatabase(){
    abstract val dao: MoodDao

}