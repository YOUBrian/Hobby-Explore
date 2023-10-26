package brian.project.hobbyexplore.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import brian.project.hobbyexplore.data.Hobby
import brian.project.hobbyexplore.data.User

@Dao
interface HobbyDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(myData: User)

    @Query("INSERT INTO user_data (id,user_name,user_email,user_hobby) VALUES(:id,:name,:email,:hobby)")
    fun insertData(
        id: Int,
        name: String,
        email: String,
        hobby: List<String>,
    )

    @Query("SELECT * FROM user_data")
    fun displayAll(): MutableList<User>

    @Query("SELECT * FROM user_data WHERE user_name = :name")
    fun findDataByName(name: String): MutableList<User>

    @Update
    fun updateData(myData: User)

    @Query("UPDATE user_data SET user_name = :name,user_email=:email,user_hobby=:hobby WHERE id = :id")
    fun updateData(
        id: Int,
        name: String,
        email: String,
        hobby: List<String>,
    )

    @Delete
    fun deleteData(myData: User)

    @Query("DELETE  FROM user_data WHERE id = :id")
    fun deleteData(id: Int)
}