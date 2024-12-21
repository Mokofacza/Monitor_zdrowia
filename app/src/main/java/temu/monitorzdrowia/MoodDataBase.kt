package temu.monitorzdrowia

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Mood::class],
    version = 1
)

abstract class MoodDataBase: RoomDatabase(){
    abstract val dao: MoodDao

}