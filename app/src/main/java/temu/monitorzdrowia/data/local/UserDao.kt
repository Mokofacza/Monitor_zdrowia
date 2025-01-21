import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import temu.monitorzdrowia.data.models.Mood
import temu.monitorzdrowia.data.models.User

// Interfejs UserDao to nasz Data Access Object (DAO), który definiuje metody do interakcji z bazą danych Room.
// Dzięki DAO możemy w prosty sposób dodawać, usuwać i pobierać dane z tabeli User.
@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)
    // suspend umożliwia wykonywanie tej operacji w tle.

    @Delete
    suspend fun deleteUser(user: User)


}